package com.icu.icuac;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ICUAC extends JavaPlugin {
    private static ICUAC instance;
    private WhitelistManager whitelistManager;
    private ItemChecker itemChecker;
    private EnchantmentChecker enchantmentChecker;
    private NBTChecker nbtChecker;
    private EffectChecker effectChecker;
    private StackSizeChecker stackSizeChecker;
    private NearbyNonOpGameModeGuard gameModeGuard;
    private CrystalPVPFeature crystalPVPFeature;
    private LanguageManager languageManager;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        languageManager = new LanguageManager(this);

        whitelistManager = new WhitelistManager(this);
        itemChecker = new ItemChecker(this);
        enchantmentChecker = new EnchantmentChecker(this);
        nbtChecker = new NBTChecker(this);
        effectChecker = new EffectChecker(this);
        stackSizeChecker = new StackSizeChecker(this);

        Bukkit.getPluginManager().registerEvents(new ItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);

        crystalPVPFeature = new CrystalPVPFeature(this);
        Bukkit.getPluginManager().registerEvents(crystalPVPFeature, this);
        getCommand("crystalreload").setExecutor(crystalPVPFeature);
        getCommand("crystallimit").setExecutor(crystalPVPFeature);
        getCommand("crystallimit").setTabCompleter(crystalPVPFeature);

        gameModeGuard = new NearbyNonOpGameModeGuard(this);
        Bukkit.getPluginManager().registerEvents(gameModeGuard, this);
        gameModeGuard.start();

        CommandHandler commandHandler = new CommandHandler(this);
        getCommand("icuac").setExecutor(commandHandler);
        getCommand("icuac").setTabCompleter(commandHandler);

        long checkInterval = getConfig().getLong("global-settings.inventory-check-interval-ticks", 1L);
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, task -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                checkPlayerInventory(player);
            }
        }, 1L, checkInterval);

        printStartupBanner();
        updateChecker = new UpdateChecker(this);
        updateChecker.checkOnStartup();
    }

    public String getMsg(String path) {
        Object value = languageManager.getMessage(path);
        return value instanceof String message ? MessageUtils.format(this, message) : "";
    }

    public String translateHexColorCodes(String message) {
        char colorChar = '§';
        Matcher matcher = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            StringBuilder replacement = new StringBuilder().append(colorChar).append('x');
            for (char c : group.toCharArray()) {
                replacement.append(colorChar).append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private void checkPlayerInventory(Player player) {
        if (whitelistManager.isWhitelisted(player.getUniqueId(), player.getName())) {
            return;
        }

        boolean hasBannedItem = false;
        boolean hasIllegalNbtItem = false;

        for (ItemStack item : player.getInventory()) {
            if (item != null && itemChecker.isBanned(item)) {
                player.getInventory().remove(item);
                hasBannedItem = true;
                getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
            } else if (item != null && nbtChecker.hasIllegalNbt(item)) {
                String reason = nbtChecker.getIllegalReason(item);
                player.getInventory().remove(item);
                hasIllegalNbtItem = true;
                getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType() + " " + reason);
            } else if (item != null && enchantmentChecker.hasIllegalEnchantments(item)) {
                enchantmentChecker.removeIllegalEnchantments(item);
                String msg = getMsg("enchant-inventory-remove");
                player.sendMessage(msg);
                getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
            }
        }

        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && itemChecker.isBanned(offHandItem)) {
            player.getInventory().setItemInOffHand(null);
            hasBannedItem = true;
            getLogger().info("[ICUAC] " + player.getName() + " : " + offHandItem.getType());
        } else if (offHandItem != null && nbtChecker.hasIllegalNbt(offHandItem)) {
            String reason = nbtChecker.getIllegalReason(offHandItem);
            player.getInventory().setItemInOffHand(null);
            hasIllegalNbtItem = true;
            getLogger().info("[ICUAC] " + player.getName() + " : " + offHandItem.getType() + " " + reason);
        } else if (offHandItem != null && enchantmentChecker.hasIllegalEnchantments(offHandItem)) {
            enchantmentChecker.removeIllegalEnchantments(offHandItem);
            String msg = getMsg("enchant-offhand-remove");
            player.sendMessage(msg);
            getLogger().info("[ICUAC] " + player.getName() + " : " + offHandItem.getType());
        }

        if (hasBannedItem) player.sendMessage(getMsg("banned-item-inventory"));
        if (hasIllegalNbtItem) player.sendMessage(getMsg("nbt-pickup-delete"));

        effectChecker.clearIllegalEffects(player);

        if (isIllegalStackCheckEnabled()) {
            stackSizeChecker.checkAndFixInventory(player.getInventory(), player);
            stackSizeChecker.checkAndFixOffHand(player);
        }
    }

    @Override
    public void onDisable() {
        if (whitelistManager != null) whitelistManager.saveWhitelist();
        if (gameModeGuard != null) gameModeGuard.stop();
        if (crystalPVPFeature != null) crystalPVPFeature.clear();
        getLogger().info("ICUAC disabled.");
    }

    public static ICUAC getInstance() { return instance; }
    public WhitelistManager getWhitelistManager() { return whitelistManager; }
    public ItemChecker getItemChecker() { return itemChecker; }
    public EnchantmentChecker getEnchantmentChecker() { return enchantmentChecker; }
    public NBTChecker getNBTChecker() { return nbtChecker; }
    public EffectChecker getEffectChecker() { return effectChecker; }
    public StackSizeChecker getStackSizeChecker() { return stackSizeChecker; }
    public LanguageManager getLanguageManager() { return languageManager; }

    public void logLocalized(String path, String... replacements) {
        String message = languageManager.getMessageString(path, path);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        message = message.replaceAll("(?i)&#[0-9a-f]{6}", "").replaceAll("(?i)&[0-9a-fk-or]", "");
        getLogger().info(message);
    }

    private void printStartupBanner() {
        String version = getDescription().getVersion();
        getLogger().info("+================================================+");
        getLogger().info("|              ICUAC SECURITY CORE               |");
        getLogger().info("| Version / 版本 : " + version);
        getLogger().info("| Author  / 作者 : Lazyz");
        getLogger().info("| Tested  / 测试 : Paper & Folia 1.21.11");
        getLogger().info("| Language/ 语言 : " + languageManager.getLanguage());
        getLogger().info("| GitHub         : https://github.com/Lazyzouo/ICUAC");
        getLogger().info("+================================================+");
    }

    public void reloadFeatures() {
        languageManager.reload();
        if (gameModeGuard != null) gameModeGuard.reload();
        itemChecker.reload();
        nbtChecker.reload();
        effectChecker.reload();
        stackSizeChecker.reload();
        if (crystalPVPFeature != null) crystalPVPFeature.loadConfig();
    }

    public boolean isPreventBelowBedrockEnabled() {
        if (getConfig().contains("features.prevent-below-bedrock.enabled")) {
            return getConfig().getBoolean("features.prevent-below-bedrock.enabled", true);
        }
        return getConfig().getBoolean("features.prevent-below-bedrock", true);
    }

    public double getBedrockThresholdNormal() {
        return getConfig().getDouble("features.prevent-below-bedrock.threshold-normal", -65.0);
    }

    public double getBedrockThresholdNether() {
        return getConfig().getDouble("features.prevent-below-bedrock.threshold-nether", -1.0);
    }

    public boolean isPreventAboveNetherBedrockEnabled() {
        return getConfig().getBoolean("features.prevent-above-nether-bedrock.enabled", true);
    }

    public double getNetherBedrockThreshold() {
        return getConfig().getDouble("features.prevent-above-nether-bedrock.threshold", 127);
    }

    public List<String> getNetherBedrockWorlds() {
        return getConfig().getStringList("features.prevent-above-nether-bedrock.worlds");
    }

    public boolean shouldCheckNetherBedrock(World world) {
        if (!isPreventAboveNetherBedrockEnabled()) return false;
        if (world.getEnvironment() != World.Environment.NETHER) return false;
        List<String> worlds = getNetherBedrockWorlds();
        return worlds.isEmpty() || worlds.contains(world.getName());
    }

    public boolean isIllegalStackCheckEnabled() {
        return getConfig().getBoolean("illegal-stack-settings.enabled", true);
    }

    public boolean isDeathDropControlEnabled() {
        return getConfig().getBoolean("features.death-drop-control.enabled", false);
    }

    public List<String> getNoDropWorlds() {
        return getConfig().getStringList("features.death-drop-control.no-drop-worlds");
    }
}
