package com.icu.icuac;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CrystalPVPFeature implements Listener, CommandExecutor, TabCompleter {
    private final ICUAC plugin;
    private final Map<UUID, PlayerCrystalData> playerCrystalData = new HashMap<>();
    private final Set<UUID> cooldownBypassPlayers = ConcurrentHashMap.newKeySet();
    private double hitInterval = 200.0D;

    public CrystalPVPFeature(ICUAC plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("crystallimit")) {
            return handleLimitCommand(sender, args);
        }
        if (!command.getName().equalsIgnoreCase("crystalreload")) return false;
        if (!sender.hasPermission("crystalpvp.reload")) {
            MessageUtils.send(sender, plugin, "unknown-command");
            return true;
        }

        loadConfig();
        MessageUtils.send(sender, plugin, "crystal-reload-success", "interval", String.valueOf(hitInterval));
        return true;
    }

    private boolean handleLimitCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player) || !player.isOp()) {
            MessageUtils.send(sender, plugin, "unknown-command");
            return true;
        }
        if (args.length != 1 || !args[0].equalsIgnoreCase("bypass")) {
            MessageUtils.send(sender, plugin, "crystal-limit-usage");
            return true;
        }

        UUID playerId = player.getUniqueId();
        if (cooldownBypassPlayers.remove(playerId)) {
            MessageUtils.send(player, plugin, "crystal-limit-restored");
        } else {
            cooldownBypassPlayers.add(playerId);
            MessageUtils.send(player, plugin, "crystal-limit-bypassed");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("crystallimit") || !sender.isOp() || args.length != 1) {
            return List.of();
        }
        return "bypass".startsWith(args[0].toLowerCase()) ? List.of("bypass") : List.of();
    }

    public void loadConfig() {
        plugin.reloadConfig();
        hitInterval = plugin.getConfig().getDouble("crystal.hit-interval", 200.0D);
    }

    public void clear() {
        playerCrystalData.clear();
        cooldownBypassPlayers.clear();
    }

    private PlayerCrystalData getPlayerData(Player player) {
        return playerCrystalData.computeIfAbsent(player.getUniqueId(), ignored -> new PlayerCrystalData());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (event.getEntityType() != EntityType.END_CRYSTAL) return;
        if (player.isOp() && cooldownBypassPlayers.contains(player.getUniqueId())) return;
        PlayerCrystalData data = getPlayerData(player);
        long currentTime = System.currentTimeMillis();
        if (currentTime - data.lastHitTime < hitInterval) {
            event.setCancelled(true);
        } else {
            data.lastHitTime = currentTime;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerCrystalData.remove(event.getPlayer().getUniqueId());
    }

    private static final class PlayerCrystalData {
        private long lastHitTime = 0L;
    }
}
