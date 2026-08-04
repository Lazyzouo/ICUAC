package com.icu.icuac;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMigratorTest {
    @Test
    void preservesExistingValuesAndCustomKeysWhileAddingDefaults() {
        YamlConfiguration current = new YamlConfiguration();
        current.set("language", "zh_CN");
        current.set("updates.enabled", false);
        current.set("custom.private-value", 73);
        current.set("messages.optional", "");
        current.setComments("updates.enabled", List.of("Administrator comment"));

        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("config-version", 1);
        defaults.set("language", "en_US");
        defaults.set("updates.enabled", true);
        defaults.set("updates.auto-download", true);
        defaults.set("messages.optional", "Official text");
        defaults.setComments("updates.enabled", List.of("Official comment"));
        defaults.setComments("updates.auto-download", List.of("New option"));

        ConfigMigrator.MergeSummary summary = ConfigMigrator.mergeConfiguration(current, defaults);

        assertTrue(summary.changed());
        assertEquals(1, summary.addedKeys());
        assertEquals(1, current.getInt("config-version"));
        assertEquals("zh_CN", current.getString("language"));
        assertFalse(current.getBoolean("updates.enabled"));
        assertTrue(current.getBoolean("updates.auto-download"));
        assertEquals(73, current.getInt("custom.private-value"));
        assertEquals("", current.getString("messages.optional"));
        assertEquals(List.of("Administrator comment"), current.getComments("updates.enabled"));
        assertEquals(List.of("New option"), current.getComments("updates.auto-download"));
    }

    @Test
    void migratesLegacyBooleanIntoEnabledWithoutChangingItsValue() {
        YamlConfiguration current = new YamlConfiguration();
        current.set("features.prevent-below-bedrock", false);

        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("config-version", 1);
        defaults.set("features.prevent-below-bedrock.enabled", true);
        defaults.set("features.prevent-below-bedrock.threshold-normal", -65.0D);
        defaults.set("features.prevent-below-bedrock.threshold-nether", -1.0D);

        ConfigMigrator.MergeSummary summary = ConfigMigrator.mergeConfiguration(current, defaults);

        assertEquals(1, summary.transformedKeys());
        assertTrue(current.isConfigurationSection("features.prevent-below-bedrock"));
        assertFalse(current.getBoolean("features.prevent-below-bedrock.enabled"));
        assertEquals(-65.0D, current.getDouble("features.prevent-below-bedrock.threshold-normal"));
        assertEquals(-1.0D, current.getDouble("features.prevent-below-bedrock.threshold-nether"));
    }

    @Test
    void preservesUnknownTypeConflictsInsteadOfOverwritingThem() {
        YamlConfiguration current = new YamlConfiguration();
        current.set("updates", "administrator-owned-value");
        current.set("command.custom-child", true);

        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("config-version", 1);
        defaults.set("updates.enabled", true);
        defaults.set("command", false);
        defaults.setComments("command", List.of("Official scalar comment"));

        ConfigMigrator.MergeSummary summary = ConfigMigrator.mergeConfiguration(current, defaults);

        assertEquals(List.of("updates", "command"), summary.conflicts());
        assertEquals("administrator-owned-value", current.getString("updates"));
        assertFalse(current.contains("updates.enabled"));
        assertTrue(current.getBoolean("command.custom-child"));
        assertTrue(current.getComments("command").isEmpty());
    }

    @Test
    void doesNotDowngradeAConfigurationFromANewerPlugin() {
        YamlConfiguration current = new YamlConfiguration();
        current.set("config-version", 2);
        current.set("custom.keep", true);

        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("config-version", 1);
        defaults.set("new-default", true);

        ConfigMigrator.MergeSummary summary = ConfigMigrator.mergeConfiguration(current, defaults);

        assertTrue(summary.skippedNewerVersion());
        assertFalse(summary.changed());
        assertEquals(2, current.getInt("config-version"));
        assertFalse(current.contains("new-default"));
        assertTrue(current.getBoolean("custom.keep"));
    }

    @Test
    void becomesIdempotentAfterTheFirstMerge() {
        YamlConfiguration current = new YamlConfiguration();
        current.set("language", "zh_CN");

        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("config-version", 1);
        defaults.set("language", "zh_CN");
        defaults.set("updates.enabled", true);
        defaults.setComments("updates.enabled", List.of("Update switch"));

        ConfigMigrator.MergeSummary first = ConfigMigrator.mergeConfiguration(current, defaults);
        ConfigMigrator.MergeSummary second = ConfigMigrator.mergeConfiguration(current, defaults);

        assertTrue(first.changed());
        assertFalse(second.changed());
        assertEquals(0, second.addedKeys());
        assertEquals(0, second.copiedComments());
    }

    @Test
    void bundledLanguagePresetsExposeTheSameConfigurationSchema() throws Exception {
        YamlConfiguration chinese = loadBundledYaml("/defaults/config.zh_CN.yml");
        YamlConfiguration english = loadBundledYaml("/defaults/config.en_US.yml");

        assertEquals(chinese.getKeys(true), english.getKeys(true));
        assertEquals(1, chinese.getInt("config-version"));
        assertEquals(1, english.getInt("config-version"));
    }

    private YamlConfiguration loadBundledYaml(String path) throws Exception {
        InputStream input = getClass().getResourceAsStream(path);
        assertNotNull(input, "Missing test resource " + path);
        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(reader);
            return configuration;
        }
    }
}
