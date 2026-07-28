package com.icu.icuac;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;
import java.util.List;

public class CommandListener implements Listener {
    private final ICUAC plugin;

    public CommandListener(ICUAC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getConfig().getBoolean("command-blocking-enabled", true)) return;
        Player player = event.getPlayer();
        if (shouldBlock(player, event.getMessage())) {
            event.setCancelled(true);
            sendDenyMessage(player);
            plugin.getLogger().info("[ICUAC] " + player.getName() + " executed blocked command: " + event.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        if (!plugin.getConfig().getBoolean("command-blocking-enabled", true)) return;
        if (shouldBlock(event.getSender(), event.getCommand())) {
            event.setCancelled(true);
            sendDenyMessage(event.getSender());
            plugin.getLogger().info("[ICUAC] " + event.getSender().getName() + " executed blocked command: " + event.getCommand());
        }
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        event.getCommands().removeIf(command -> {
            String root = stripNamespace(command);
            if (root.equalsIgnoreCase("crystallimit")) return !player.isOp();
            if (root.equalsIgnoreCase("crystalreload")) return !player.hasPermission("crystalpvp.reload");
            return false;
        });

        if (!plugin.getConfig().getBoolean("command-blocking-enabled", true)
                || !plugin.getConfig().getBoolean("hide-blocked-commands-from-tab", true)) return;
        if (canBypass(player)) return;

        List<String> blockedCommands = plugin.getConfig().getStringList("blocked-commands");
        event.getCommands().removeIf(command -> isBlockedRoot(command, blockedCommands));
    }

    private boolean shouldBlock(CommandSender sender, String rawCommand) {
        List<String> blockedCommands = plugin.getConfig().getStringList("blocked-commands");
        if (!containsBlockedCommand(rawCommand, blockedCommands, 0)) return false;
        if (sender instanceof Player player) return !canBypass(player);
        if (sender instanceof BlockCommandSender) return plugin.getConfig().getBoolean("block-command-blocks", true);
        return plugin.getConfig().getBoolean("block-console-commands", false);
    }

    private boolean canBypass(Player player) {
        // 白名单玩家直接放行指令拦截
        if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return true;

        return plugin.getConfig().getBoolean("permission-bypass-enabled", false)
                && player.hasPermission("icuac.bypass");
    }

    private void sendDenyMessage(CommandSender sender) {
        String denyMessage = ICUAC.getInstance().getMsg("deny-message");
        if (!denyMessage.isEmpty()) {
            sender.sendMessage(denyMessage);
        }
    }

    private boolean containsBlockedCommand(String rawCommand, List<String> blockedCommands, int depth) {
        int maxDepth = plugin.getConfig().getInt("command-blocking-max-depth", 4);
        if (depth > maxDepth) return false;

        String normalizedCommand = normalizeCommand(rawCommand);
        if (normalizedCommand.isEmpty()) return false;
        String commandName = extractCommandName(normalizedCommand);
        if (isBlockedRoot(commandName, blockedCommands)) return true;
        if (!stripNamespace(commandName).equals("execute")) return false;

        List<String> parts = Arrays.asList(normalizedCommand.split("\\s+"));
        for (int i = 1; i < parts.size() - 1; i++) {
            if (parts.get(i).equalsIgnoreCase("run")) {
                String nestedCommand = String.join(" ", parts.subList(i + 1, parts.size()));
                if (containsBlockedCommand(nestedCommand, blockedCommands, depth + 1)) return true;
            }
        }
        return false;
    }

    private boolean isBlockedRoot(String commandName, List<String> blockedCommands) {
        String lowerCommand = normalizeCommand(commandName).toLowerCase();
        String strippedCommand = stripNamespace(lowerCommand);
        for (String blocked : blockedCommands) {
            String lowerBlocked = normalizeCommand(blocked).toLowerCase();
            String strippedBlocked = stripNamespace(lowerBlocked);
            if (lowerCommand.equals(lowerBlocked) || strippedCommand.equals(strippedBlocked)) return true;
        }
        return false;
    }

    private String normalizeCommand(String command) {
        command = command.trim();
        while (command.startsWith("/")) command = command.substring(1).trim();
        return command;
    }

    private String extractCommandName(String message) {
        int spaceIndex = message.indexOf(' ');
        if (spaceIndex > 0) return message.substring(0, spaceIndex);
        return message;
    }

    private String stripNamespace(String commandName) {
        int namespaceIndex = commandName.indexOf(':');
        if (namespaceIndex >= 0 && namespaceIndex < commandName.length() - 1) {
            return commandName.substring(namespaceIndex + 1);
        }
        return commandName;
    }
}
