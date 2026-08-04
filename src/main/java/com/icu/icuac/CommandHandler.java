package com.icu.icuac;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final ICUAC plugin;

    public CommandHandler(ICUAC plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            MessageUtils.send(sender, plugin, "deny-message");
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        return switch (args[0].toLowerCase()) {
            case "help" -> { sendHelp(sender); yield true; }
            case "add" -> handleAdd(sender, args);
            case "remove", "del", "delete" -> handleRemove(sender, args);
            case "list" -> handleList(sender);
            case "reload" -> handleReload(sender);
            default -> { sendHelp(sender); yield true; }
        };
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.send(sender, plugin, "command-add-usage");
            return true;
        }
        String playerName = args[1];
        Player target = Bukkit.getPlayer(playerName);
        if (target != null) plugin.getWhitelistManager().addToWhitelist(target.getUniqueId(), target.getName());
        else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            plugin.getWhitelistManager().addToWhitelist(offlinePlayer.getUniqueId(), playerName);
        }
        MessageUtils.send(sender, plugin, "whitelist-add-message", "player", playerName);
        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.send(sender, plugin, "command-remove-usage");
            return true;
        }
        String playerName = args[1];
        Player target = Bukkit.getPlayer(playerName);
        if (target != null) plugin.getWhitelistManager().removeFromWhitelist(target.getUniqueId(), target.getName());
        else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            plugin.getWhitelistManager().removeFromWhitelist(offlinePlayer.getUniqueId(), playerName);
        }
        MessageUtils.send(sender, plugin, "whitelist-remove-message", "player", playerName);
        return true;
    }

    private boolean handleList(CommandSender sender) {
        Set<String> names = plugin.getWhitelistManager().getWhitelistNames();
        if (names.isEmpty()) MessageUtils.send(sender, plugin, "whitelist-empty-message");
        else MessageUtils.send(sender, plugin, "whitelist-list-message", "players", String.join("&8, &f", names));
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        try {
            ConfigMigrator.MigrationResult migration = ConfigMigrator.migrateMainConfig(plugin);
            plugin.getWhitelistManager().loadWhitelist();
            plugin.reloadFeatures();
            plugin.logConfigMigration(migration);
            MessageUtils.send(sender, plugin, "reload-success");
        } catch (IllegalStateException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reload config.yml safely.", exception);
            MessageUtils.send(sender, plugin, "reload-failed");
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        List<String> authors = plugin.getDescription().getAuthors();
        MessageUtils.send(sender, plugin, "help-menu",
                "name", plugin.getDescription().getName(), "version", plugin.getDescription().getVersion(),
                "author", authors.isEmpty() ? "Unknown" : authors.get(0));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (!command.getName().equalsIgnoreCase("icuac") || !sender.isOp()) return completions;
        if (args.length == 1) completions.addAll(Arrays.asList("help", "add", "remove", "list", "reload"));
        else if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")
                || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete"))) {
            for (Player player : Bukkit.getOnlinePlayers()) completions.add(player.getName());
        }
        String lastArg = args[args.length - 1].toLowerCase();
        completions.removeIf(value -> !value.toLowerCase().startsWith(lastArg));
        return completions;
    }
}
