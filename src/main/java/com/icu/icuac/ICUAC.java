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
    private static final int BANNER_WIDTH = 57;
    private static final String CONSOLE_PREFIX = "&#00D2FF[&#3A7BD5ICUAC&#00D2FF] &8» ";
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
        logLocalized("plugin-enabled", "platform", detectServerPlatform());
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
        logConsole("&cICUAC v" + getDescription().getVersion() + " disabled.");
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
        message = message
                .replace("{name}", getDescription().getName())
                .replace("{version}", getDescription().getVersion())
                .replace("{author}", getDescription().getAuthors().isEmpty()
                        ? "Lazyz"
                        : getDescription().getAuthors().get(0));
        String statusColor = switch (path) {
            case "update-checking" -> "&b";
            case "plugin-enabled", "update-latest", "update-downloaded" -> "&a";
            case "update-available", "update-manual" -> "&e";
            case "update-failed" -> "&c";
            default -> "&7";
        };
        logConsole(statusColor + message);
    }

    private void printStartupBanner() {
        String version = getDescription().getVersion();
        logConsole(bannerDivider());
        logConsole(centerBannerLine("ICUAC v" + version, "&bICUAC v" + version));
        logConsole(bannerField("Version / 版本", version, "&a"));
        logConsole(bannerField("Author  / 作者", "Lazyz", "&e"));
        logConsole(bannerField("Tested  / 测试", "Paper & Folia 1.21.11", "&a"));
        logConsole(bannerField("Language/ 语言", languageManager.getLanguage(), "&b"));
        logConsole(bannerField("GitHub", "https://github.com/Lazyzouo/ICUAC", "&9"));
        logConsole(bannerNotice(
                "Open source | No telemetry or server data upload.",
                "&aOpen source &8| &fNo telemetry or server data upload."
        ));
        logConsole(bannerDivider());
    }

    public void logConsole(String message) {
        MessageUtils.sendRaw(getServer().getConsoleSender(), CONSOLE_PREFIX + message);
    }

    private String bannerDivider() {
        int sideWidth = (BANNER_WIDTH - 1) / 2;
        return "&3+" + "=".repeat(sideWidth) + "&b✧&3" + "=".repeat(sideWidth) + "+";
    }

    private String centerBannerLine(String plain, String styled) {
        int leftPadding = Math.max(0, (BANNER_WIDTH - displayWidth(plain)) / 2);
        int rightPadding = Math.max(0, BANNER_WIDTH - displayWidth(plain) - leftPadding);
        return "&3|" + " ".repeat(leftPadding) + styled
                + "&3" + " ".repeat(rightPadding) + "|";
    }

    private String bannerField(String label, String value, String valueColor) {
        String plain = label + " : " + value;
        return centerBannerLine(plain, "&f" + label + " &8: " + valueColor + value);
    }

    private String bannerNotice(String plain, String styled) {
        return centerBannerLine(plain, styled);
    }

    private int displayWidth(String text) {
        return text.codePoints().map(codePoint -> codePoint <= 0x7F ? 1 : 2).sum();
    }

    private String detectServerPlatform() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer", false, getClass().getClassLoader());
            return "Folia";
        } catch (ClassNotFoundException ignored) {
            return "Paper";
        }
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
