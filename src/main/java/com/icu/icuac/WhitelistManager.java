package com.icu.icuac;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WhitelistManager {

    private final ICUAC plugin;
    private final File whitelistFile;
    private final Set<UUID> whitelist;
    private final Set<String> whitelistNames;

    public WhitelistManager(ICUAC plugin) {
        this.plugin = plugin;
        this.whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml");
        this.whitelist = new HashSet<>();
        this.whitelistNames = new HashSet<>();
        loadWhitelist();
    }

    public void loadWhitelist() {
        if (!whitelistFile.exists()) {
            try {
                whitelistFile.getParentFile().mkdirs();
                whitelistFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建白名单文件: " + e.getMessage());
            }
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(whitelistFile);
        List<String> uuidList = config.getStringList("whitelist");

        whitelist.clear();
        whitelistNames.clear();

        for (String entry : uuidList) {
            try {
                UUID uuid = UUID.fromString(entry);
                whitelist.add(uuid);
            } catch (IllegalArgumentException e) {
                whitelistNames.add(entry.toLowerCase());
            }
        }
        refreshOnlineCommandTrees();
        plugin.getLogger().info("已加载 " + whitelist.size() + " 个UUID白名单, " + whitelistNames.size() + " 个名称白名单");
    }

    public void saveWhitelist() {
        YamlConfiguration config = new YamlConfiguration();
        List<String> list = new java.util.ArrayList<>();

        for (UUID uuid : whitelist) list.add(uuid.toString());
        for (String name : whitelistNames) list.add(name);

        config.set("whitelist", list);
        try {
            config.save(whitelistFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存白名单文件: " + e.getMessage());
        }
    }

    public boolean isWhitelisted(UUID uuid, String playerName) {
        return whitelist.contains(uuid) || whitelistNames.contains(playerName.toLowerCase());
    }

    public void addToWhitelist(UUID uuid, String playerName) {
        whitelist.add(uuid);
        whitelistNames.add(playerName.toLowerCase());
        saveWhitelist();
        refreshCommandTree(uuid);
    }

    public void removeFromWhitelist(UUID uuid, String playerName) {
        whitelist.remove(uuid);
        whitelistNames.remove(playerName.toLowerCase());
        saveWhitelist();
        refreshCommandTree(uuid);
    }

    private void refreshOnlineCommandTrees() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshCommandTree(player);
        }
    }

    private void refreshCommandTree(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) refreshCommandTree(player);
    }

    private void refreshCommandTree(Player player) {
        player.getScheduler().run(plugin, task -> player.updateCommands(), null);
    }

    public Set<UUID> getWhitelistUuids() { return new HashSet<>(whitelist); }
    public Set<String> getWhitelistNames() { return new HashSet<>(whitelistNames); }
}
