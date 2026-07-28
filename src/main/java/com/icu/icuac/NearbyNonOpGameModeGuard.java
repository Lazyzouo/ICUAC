package com.icu.icuac;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NearbyNonOpGameModeGuard implements Listener {
    private static final String CONFIG_PATH = "creative-world-whitelist";
    private final ICUAC plugin;
    private final Map<UUID, ScheduledTask> tasks = new ConcurrentHashMap<>();

    private boolean enabled;
    private boolean logSwitches;
    private long checkIntervalTicks;
    private Set<GameMode> sourceGameModes;
    private Set<GameMode> ignoredGameModes;
    private GameMode targetGameMode;
    private Set<String> worldWhitelist;

    public NearbyNonOpGameModeGuard(ICUAC plugin) {
        this.plugin = plugin;
        loadSettings();
    }

    public void start() {
        if (!enabled) return;
        for (Player player : plugin.getServer().getOnlinePlayers()) schedule(player);
    }

    public void stop() {
        for (ScheduledTask task : new ArrayList<>(tasks.values())) task.cancel();
        tasks.clear();
    }

    public void reload() {
        stop();
        loadSettings();
        start();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { if (enabled) schedule(event.getPlayer()); }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ScheduledTask task = tasks.remove(event.getPlayer().getUniqueId());
        if (task != null) task.cancel();
    }

    private void schedule(Player player) {
        UUID uuid = player.getUniqueId();
        if (tasks.containsKey(uuid)) return;

        ScheduledTask task = player.getScheduler().runAtFixedRate(
                plugin,
                scheduledTask -> checkPlayer(player),
                () -> tasks.remove(uuid),
                checkIntervalTicks,
                checkIntervalTicks
        );
        if (task != null) tasks.put(uuid, task);
    }

    private void checkPlayer(Player player) {
        if (!enabled || !player.isOnline()) return;

        // 如果玩家存在于 ICUAC 白名单内，则允许他在任何世界使用创造模式
        if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) {
            return;
        }

        GameMode currentGameMode = player.getGameMode();
        if (ignoredGameModes.contains(currentGameMode) || !sourceGameModes.contains(currentGameMode)) return;

        String worldName = player.getWorld().getName();
        if (worldWhitelist.contains(worldName)) return;

        player.setGameMode(targetGameMode);

        String switchMessage = plugin.getMsg("switch-message");
        if (switchMessage != null && !switchMessage.isBlank()) {
            player.sendMessage(switchMessage);
        }

        if (logSwitches) {
            plugin.getLogger().info("[ICUAC] Changed " + player.getName() + " from " + currentGameMode
                    + " to " + targetGameMode + " because world '" + worldName + "' is not in whitelist.");
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) { if (enabled) checkPlayer(event.getPlayer()); }

    private void loadSettings() {
        enabled = plugin.getConfig().getBoolean(CONFIG_PATH + ".enabled", true);
        logSwitches = plugin.getConfig().getBoolean(CONFIG_PATH + ".log-switches", true);
        checkIntervalTicks = Math.max(1L, plugin.getConfig().getLong(CONFIG_PATH + ".check-interval-ticks", 20L));
        sourceGameModes = parseGameModes(CONFIG_PATH + ".source-game-modes", EnumSet.of(GameMode.CREATIVE));
        ignoredGameModes = parseGameModes(CONFIG_PATH + ".ignored-game-modes", EnumSet.of(GameMode.SPECTATOR));
        targetGameMode = parseGameMode(plugin.getConfig().getString(CONFIG_PATH + ".target-game-mode", "SURVIVAL"), GameMode.SURVIVAL);
        worldWhitelist = new HashSet<>(plugin.getConfig().getStringList(CONFIG_PATH + ".worlds"));
    }

    private Set<GameMode> parseGameModes(String path, Set<GameMode> fallback) {
        Set<GameMode> modes = EnumSet.noneOf(GameMode.class);
        for (String value : plugin.getConfig().getStringList(path)) {
            GameMode mode = parseGameMode(value, null);
            if (mode != null) modes.add(mode);
        }
        return modes.isEmpty() ? fallback : modes;
    }

    private GameMode parseGameMode(String value, GameMode fallback) {
        if (value == null) return fallback;
        try {
            return GameMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }
}