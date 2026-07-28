package com.icu.icuac;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class EffectChecker {
    private final ICUAC plugin;
    private int maxDuration;
    private boolean checkDuration;
    private boolean checkLevel;
    private Map<String, Integer> specificLevelLimits;
    private Map<String, Integer> defaultLevelLimits;
    private int defaultLevelFallback;

    public EffectChecker(ICUAC plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        checkDuration = plugin.getConfig().getBoolean("illegal-effects-settings.check-duration", true);
        maxDuration = plugin.getConfig().getInt("illegal-effects-settings.max-duration-ticks", 12000);
        checkLevel = plugin.getConfig().getBoolean("illegal-effects-settings.check-level", true);

        specificLevelLimits = new HashMap<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("illegal-effects-settings.specific-level-limits");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                int limit = section.getInt(key, -1);
                if (limit >= 0) {
                    specificLevelLimits.put(key.toUpperCase(), limit);
                }
            }
        }

        defaultLevelFallback = plugin.getConfig().getInt("illegal-effects-settings.default-level-fallback", 10);
        defaultLevelLimits = new HashMap<>();
        ConfigurationSection defaultSection = plugin.getConfig().getConfigurationSection("illegal-effects-settings.default-level-limits");
        if (defaultSection != null) {
            for (String key : defaultSection.getKeys(false)) {
                defaultLevelLimits.put(key.toUpperCase(), defaultSection.getInt(key));
            }
        }
    }

    public boolean hasIllegalEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (isIllegalEffect(effect)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIllegalEffect(PotionEffect effect) {
        if (checkDuration && effect.getDuration() > maxDuration) {
            return true;
        }
        if (checkLevel) {
            int maxLevel = getMaxAllowedLevel(effect.getType());
            if (effect.getAmplifier() > maxLevel) {
                return true;
            }
        }
        return false;
    }

    private int getMaxAllowedLevel(PotionEffectType type) {
        String name = type.getName();
        if (specificLevelLimits.containsKey(name)) {
            return specificLevelLimits.get(name);
        }
        return getMaxNormalLevel(type);
    }

    private int getMaxNormalLevel(PotionEffectType type) {
        String name = type.getName();
        return defaultLevelLimits.getOrDefault(name, defaultLevelFallback);
    }

    public void clearIllegalEffects(Player player) {
        boolean hasIllegalEffect = false;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (isIllegalEffect(effect)) {
                String reason = "";
                if (checkDuration && effect.getDuration() > maxDuration) {
                    reason = " : " + formatDuration(effect.getDuration());
                } else if (checkLevel) {
                    int maxAllowedLevel = getMaxAllowedLevel(effect.getType());
                    if (effect.getAmplifier() > maxAllowedLevel) {
                        reason = " : " + (effect.getAmplifier() + 1) + " : " + (maxAllowedLevel + 1);
                    }
                }
                player.removePotionEffect(effect.getType());
                hasIllegalEffect = true;
                plugin.getLogger().info("[ICUAC] " + player.getName() + " " + effect.getType().getName() + " " + reason);
            }
        }
        if (hasIllegalEffect) {
            player.sendMessage(plugin.getMsg("effect-cleared"));
        }
    }

    private String formatDuration(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        int secs = seconds % 60;
        if (minutes > 0) {
            return minutes + "m " + secs + "s";
        }
        return seconds + "s";
    }

    public void reload() {
        loadConfig();
    }
}