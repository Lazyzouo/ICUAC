package com.icu.icuac;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Locale;

public final class LanguageManager {
    public static final String CHINESE = "zh_CN";
    public static final String ENGLISH = "en_US";

    private final ICUAC plugin;
    private String language = CHINESE;
    private YamlConfiguration languageConfig;

    public LanguageManager(ICUAC plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        language = normalize(plugin.getConfig().getString("language", CHINESE));
        languageConfig = null;
        if (ENGLISH.equals(language)) {
            String resourcePath = "lang/en_US.yml";
            File languageFile = new File(plugin.getDataFolder(), resourcePath);
            if (!languageFile.exists()) plugin.saveResource(resourcePath, false);
            languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        }
    }

    public Object getMessage(String path) {
        if (languageConfig != null) {
            Object localized = languageConfig.get("messages." + path);
            if (localized != null) return localized;
        }
        return plugin.getConfig().get("messages." + path);
    }

    public String getMessageString(String path, String fallback) {
        Object value = getMessage(path);
        return value instanceof String message ? message : fallback;
    }

    public String getLanguage() {
        return language;
    }

    private String normalize(String value) {
        if (value == null) return CHINESE;
        String normalized = value.trim().toLowerCase(Locale.ROOT).replace('-', '_');
        if (normalized.equals("en") || normalized.equals("en_us")) return ENGLISH;
        if (normalized.equals("zh") || normalized.equals("zh_cn")) return CHINESE;
        plugin.getLogger().warning("Unsupported language '" + value + "'; falling back to zh_CN.");
        return CHINESE;
    }
}
