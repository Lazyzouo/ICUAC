package com.icu.icuac;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class ConfigMigrator {
    static final String SCHEMA_VERSION_PATH = "config-version";

    private static final DateTimeFormatter BACKUP_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    private static final String CHINESE_DEFAULTS = "defaults/config.zh_CN.yml";
    private static final String ENGLISH_DEFAULTS = "defaults/config.en_US.yml";

    private ConfigMigrator() {}

    static MigrationResult migrateMainConfig(ICUAC plugin) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            int version = plugin.getConfig().getInt(SCHEMA_VERSION_PATH, 1);
            return MigrationResult.created(version);
        }

        try {
            YamlConfiguration current = loadFile(configFile);
            YamlConfiguration packagedDefault = loadResource(plugin, "config.yml");
            String packagedLanguage = packagedDefault.getString("language", LanguageManager.CHINESE);
            String language = normalizeLanguage(current.getString("language", packagedLanguage));
            String defaultsResource = LanguageManager.ENGLISH.equals(language)
                    ? ENGLISH_DEFAULTS : CHINESE_DEFAULTS;
            YamlConfiguration defaults = loadResource(plugin, defaultsResource);

            MergeSummary summary = mergeConfiguration(current, defaults);
            if (summary.skippedNewerVersion()) {
                plugin.reloadConfig();
                return MigrationResult.skipped(summary);
            }
            if (!summary.changed()) {
                plugin.reloadConfig();
                return MigrationResult.unchanged(summary);
            }

            Path backup = createBackup(plugin, configFile.toPath(), summary);
            saveAtomically(current, configFile.toPath());
            plugin.reloadConfig();
            return MigrationResult.updated(summary, backup);
        } catch (IOException | InvalidConfigurationException exception) {
            throw new IllegalStateException(
                    "Unable to migrate config.yml safely; the original file was left in place.", exception);
        }
    }

    static MergeSummary mergeConfiguration(YamlConfiguration current, YamlConfiguration defaults) {
        int currentVersion = Math.max(0, current.getInt(SCHEMA_VERSION_PATH, 0));
        int targetVersion = Math.max(1, defaults.getInt(SCHEMA_VERSION_PATH, 1));
        if (currentVersion > targetVersion) {
            return new MergeSummary(currentVersion, targetVersion, 0, 0, 0,
                    List.of(), false, true);
        }

        MergeStats stats = new MergeStats();
        mergeSection(current, defaults, defaults, "", stats);
        boolean versionChanged = currentVersion != targetVersion;
        if (versionChanged) current.set(SCHEMA_VERSION_PATH, targetVersion);
        copyComments(current, defaults, SCHEMA_VERSION_PATH, stats);

        return new MergeSummary(currentVersion, targetVersion, stats.addedKeys,
                stats.transformedKeys, stats.copiedComments, List.copyOf(stats.conflicts),
                versionChanged, false);
    }

    private static void mergeSection(YamlConfiguration current, YamlConfiguration defaultsRoot,
                                     ConfigurationSection defaults, String parentPath, MergeStats stats) {
        for (String key : defaults.getKeys(false)) {
            String path = parentPath.isEmpty() ? key : parentPath + "." + key;
            if (SCHEMA_VERSION_PATH.equals(path)) continue;

            Object defaultValue = defaults.get(key);
            if (defaultValue instanceof ConfigurationSection defaultSection) {
                if (!current.contains(path)) {
                    current.createSection(path);
                } else if (!current.isConfigurationSection(path)) {
                    Object existingValue = current.get(path);
                    if (existingValue instanceof Boolean enabled && defaultSection.contains("enabled")) {
                        List<String> existingComments = current.getComments(path);
                        List<String> existingInlineComments = current.getInlineComments(path);
                        current.set(path, null);
                        current.createSection(path);
                        current.set(path + ".enabled", enabled);
                        if (!existingComments.isEmpty()) current.setComments(path + ".enabled", existingComments);
                        if (!existingInlineComments.isEmpty()) {
                            current.setInlineComments(path + ".enabled", existingInlineComments);
                        }
                        stats.transformedKeys++;
                    } else {
                        stats.conflicts.add(path);
                        continue;
                    }
                }
                copyComments(current, defaultsRoot, path, stats);
                mergeSection(current, defaultsRoot, defaultSection, path, stats);
            } else {
                if (!current.contains(path)) {
                    current.set(path, copyValue(defaultValue));
                    stats.addedKeys++;
                } else if (current.isConfigurationSection(path)) {
                    stats.conflicts.add(path);
                    continue;
                }
                copyComments(current, defaultsRoot, path, stats);
            }
        }
    }

    private static Object copyValue(Object value) {
        if (value instanceof List<?> list) return new ArrayList<>(list);
        return value;
    }

    private static void copyComments(YamlConfiguration current, YamlConfiguration defaults,
                                     String path, MergeStats stats) {
        List<String> comments = defaults.getComments(path);
        if (!comments.isEmpty() && current.getComments(path).isEmpty()) {
            current.setComments(path, comments);
            stats.copiedComments++;
        }
        List<String> inlineComments = defaults.getInlineComments(path);
        if (!inlineComments.isEmpty() && current.getInlineComments(path).isEmpty()) {
            current.setInlineComments(path, inlineComments);
            stats.copiedComments++;
        }
    }

    private static YamlConfiguration loadFile(File file)
            throws IOException, InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        configuration.load(file);
        return configuration;
    }

    private static YamlConfiguration loadResource(ICUAC plugin, String resourcePath)
            throws IOException, InvalidConfigurationException {
        InputStream input = plugin.getResource(resourcePath);
        if (input == null) throw new IOException("Missing bundled resource: " + resourcePath);
        try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.options().parseComments(true);
            configuration.load(reader);
            return configuration;
        }
    }

    private static Path createBackup(ICUAC plugin, Path configPath, MergeSummary summary)
            throws IOException {
        Path backupDirectory = plugin.getDataFolder().toPath().resolve("backups");
        Files.createDirectories(backupDirectory);
        String timestamp = BACKUP_TIMESTAMP.format(LocalDateTime.now());
        String prefix = "config-before-v" + summary.targetVersion() + "-" + timestamp + "-";
        Path backup = Files.createTempFile(backupDirectory, prefix, ".yml");
        Files.copy(configPath, backup, StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES);
        return backup;
    }

    private static void saveAtomically(YamlConfiguration configuration, Path target)
            throws IOException, InvalidConfigurationException {
        Path parent = target.toAbsolutePath().getParent();
        if (parent == null) throw new IOException("config.yml has no parent directory");
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, "config-migration-", ".tmp");
        try {
            Files.writeString(temporary, configuration.saveToString(), StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            loadFile(temporary.toFile());
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static String normalizeLanguage(String value) {
        if (value == null) return LanguageManager.CHINESE;
        String normalized = value.trim().toLowerCase(Locale.ROOT).replace('-', '_');
        return normalized.equals("en") || normalized.equals("en_us")
                ? LanguageManager.ENGLISH : LanguageManager.CHINESE;
    }

    record MergeSummary(int previousVersion, int targetVersion, int addedKeys,
                        int transformedKeys, int copiedComments, List<String> conflicts,
                        boolean versionChanged, boolean skippedNewerVersion) {
        boolean changed() {
            return !skippedNewerVersion && (versionChanged || addedKeys > 0
                    || transformedKeys > 0 || copiedComments > 0);
        }
    }

    record MigrationResult(boolean created, boolean updated, boolean skippedNewerVersion,
                           MergeSummary summary, Path backup) {
        static MigrationResult created(int version) {
            MergeSummary summary = new MergeSummary(version, version, 0, 0, 0,
                    List.of(), false, false);
            return new MigrationResult(true, false, false, summary, null);
        }

        static MigrationResult updated(MergeSummary summary, Path backup) {
            return new MigrationResult(false, true, false, summary, backup);
        }

        static MigrationResult unchanged(MergeSummary summary) {
            return new MigrationResult(false, false, false, summary, null);
        }

        static MigrationResult skipped(MergeSummary summary) {
            return new MigrationResult(false, false, true, summary, null);
        }
    }

    private static final class MergeStats {
        private int addedKeys;
        private int transformedKeys;
        private int copiedComments;
        private final List<String> conflicts = new ArrayList<>();
    }
}
