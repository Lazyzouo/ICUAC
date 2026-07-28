package com.icu.icuac;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {
    private final ICUAC plugin;

    public ItemListener(ICUAC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;

        checkAndRemoveBannedItems(player);
        checkAndRemoveIllegalNbt(player);
        checkAndRemoveIllegalEnchantments(player);
        plugin.getEffectChecker().clearIllegalEffects(player);

        if (plugin.isIllegalStackCheckEnabled()) {
            plugin.getStackSizeChecker().checkAndFixInventory(player.getInventory(), player);
            plugin.getStackSizeChecker().checkAndFixOffHand(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.isDeathDropControlEnabled()) return;

        Player player = event.getEntity();
        String currentWorld = player.getWorld().getName();

        if (plugin.getNoDropWorlds().contains(currentWorld)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;

        ItemStack item = event.getItem();
        if (item != null) {
            boolean shouldCancel = false;
            if (plugin.getItemChecker().isBanned(item)) {
                shouldCancel = true;
            } else if (plugin.getNBTChecker().hasIllegalNbt(item)) {
                shouldCancel = true;
            }

            if (shouldCancel) {
                event.setCancelled(true);
            }
        }

        checkAndRemoveBannedItems(player);
        checkAndRemoveIllegalNbt(player);
        checkAndRemoveIllegalEnchantments(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;

        ItemStack item = event.getItem().getItemStack();
        NBTChecker.NBTCheckResult nbtResult = plugin.getNBTChecker().check(item);
        if (nbtResult.isIllegal()) {
            event.setCancelled(true);
            event.getItem().remove();
            player.sendMessage(plugin.getMsg("nbt-pickup-delete"));
            plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType() + " " + nbtResult.getReason());
            return;
        }

        if (plugin.getItemChecker().isBanned(item)) {
            event.setCancelled(true);
            event.getItem().remove();
            player.sendMessage(plugin.getMsg("banned-item-inventory"));
            plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
            return;
        }

        if (plugin.getEnchantmentChecker().hasIllegalEnchantments(item)) {
            plugin.getEnchantmentChecker().removeIllegalEnchantments(item);
            player.sendMessage(plugin.getMsg("enchant-pickup-remove"));
            plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
        }

        checkAndRemoveBannedItems(player);
        checkAndRemoveIllegalNbt(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;

        ItemStack item = event.getItemDrop().getItemStack();
        NBTChecker.NBTCheckResult nbtResult = plugin.getNBTChecker().check(item);
        if (nbtResult.isIllegal()) {
            event.getItemDrop().remove();
            player.sendMessage(plugin.getMsg("nbt-drop-delete"));
            plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType() + " " + nbtResult.getReason());
            checkAndRemoveIllegalNbt(player);
            return;
        }

        if (plugin.getItemChecker().isBanned(item)) {
            event.getItemDrop().remove();
            player.sendMessage(plugin.getMsg("banned-item-inventory"));
            plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
            checkAndRemoveBannedItems(player);
            return;
        }

        if (plugin.getEnchantmentChecker().hasIllegalEnchantments(item)) {
            plugin.getEnchantmentChecker().removeIllegalEnchantments(item);
            player.sendMessage(plugin.getMsg("enchant-drop-remove"));
            plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
        }

        checkAndRemoveBannedItems(player);
        checkAndRemoveIllegalNbt(player);
        checkAndRemoveIllegalEnchantments(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = null;
        if (event.getWhoClicked() instanceof Player) {
            player = (Player) event.getWhoClicked();
            if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;
        }

        ItemStack cursorItem = event.getCursor();
        if (cursorItem != null && !cursorItem.getType().isAir()) {
            boolean isBanned = plugin.getItemChecker().isBanned(cursorItem);
            NBTChecker.NBTCheckResult cursorNbtResult = plugin.getNBTChecker().check(cursorItem);

            if (isBanned || cursorNbtResult.isIllegal()) {
                event.setCancelled(true);
                event.setCursor(null);
                if (player != null) {
                    if (isBanned) {
                        player.sendMessage(plugin.getMsg("banned-item-inventory"));
                    } else {
                        player.sendMessage(plugin.getMsg("nbt-cursor-delete"));
                    }
                }
                return;
            }
        }

        ItemStack item = event.getCurrentItem();
        if (item != null && !item.getType().isAir()) {
            boolean isBanned = plugin.getItemChecker().isBanned(item);
            NBTChecker.NBTCheckResult nbtResult = plugin.getNBTChecker().check(item);

            if (isBanned || nbtResult.isIllegal()) {
                event.setCancelled(true);
                event.setCurrentItem(null);
                if (player != null) {
                    if (isBanned) {
                        player.sendMessage(plugin.getMsg("banned-item-inventory"));
                    } else {
                        player.sendMessage(plugin.getMsg("nbt-use-delete"));
                    }
                }
                return;
            }

            if (plugin.getEnchantmentChecker().hasIllegalEnchantments(item)) {
                plugin.getEnchantmentChecker().removeIllegalEnchantments(item);
                if (player != null) {
                    player.sendMessage(plugin.getMsg("enchant-use-remove"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;

            boolean hasBannedItem = false;
            for (ItemStack item : player.getInventory()) {
                if (item != null && plugin.getItemChecker().isBanned(item)) {
                    player.getInventory().remove(item);
                    hasBannedItem = true;
                    plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
                }
            }
            if (hasBannedItem) player.sendMessage(plugin.getMsg("banned-item-inventory"));

            plugin.getNBTChecker().checkAndRemoveInventory(player.getInventory(), player, "inventory");
            plugin.getNBTChecker().checkAndRemoveOffHand(player);

            if (isContainer(event.getInventory().getType())) {
                for (ItemStack item : event.getInventory()) {
                    if (item != null && plugin.getItemChecker().isBanned(item)) {
                        event.getInventory().remove(item);
                        player.sendMessage(plugin.getMsg("banned-item-container"));
                        plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
                    }
                }
                plugin.getNBTChecker().checkAndRemoveInventory(event.getInventory(), player, "container");
            }

            for (ItemStack item : event.getInventory()) {
                if (item != null && plugin.getEnchantmentChecker().hasIllegalEnchantments(item)) {
                    plugin.getEnchantmentChecker().removeIllegalEnchantments(item);
                    player.sendMessage(plugin.getMsg("enchant-inventory-remove"));
                }
            }

            for (ItemStack item : player.getInventory()) {
                if (item != null && plugin.getEnchantmentChecker().hasIllegalEnchantments(item)) {
                    plugin.getEnchantmentChecker().removeIllegalEnchantments(item);
                    player.sendMessage(plugin.getMsg("enchant-inventory-remove"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() instanceof Player) {
            Player player = (Player) event.getSource().getHolder();
            if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;
        }

        if (isContainer(event.getDestination().getType())) {
            ItemStack item = event.getItem();
            if (plugin.getItemChecker().isBanned(item)) {
                event.setCancelled(true);
                if (event.getSource().getHolder() instanceof Player) {
                    Player player = (Player) event.getSource().getHolder();
                    player.sendMessage(plugin.getMsg("banned-item-into-container"));
                    plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
                }
                return;
            }

            NBTChecker.NBTCheckResult nbtResult = plugin.getNBTChecker().check(item);
            if (nbtResult.isIllegal()) {
                event.setCancelled(true);
                if (event.getSource().getHolder() instanceof Player) {
                    Player player = (Player) event.getSource().getHolder();
                    player.sendMessage(plugin.getMsg("nbt-item-into-container"));
                    plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType() + " " + nbtResult.getReason());
                }
            }
        }
    }

    private void checkAndRemoveBannedItems(Player player) {
        boolean hasBannedItem = false;
        for (ItemStack item : player.getInventory()) {
            if (item != null && plugin.getItemChecker().isBanned(item)) {
                player.getInventory().remove(item);
                hasBannedItem = true;
                plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + item.getType());
            }
        }

        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && plugin.getItemChecker().isBanned(offHandItem)) {
            player.getInventory().setItemInOffHand(null);
            hasBannedItem = true;
            plugin.getLogger().info("[ICUAC] " + player.getName() + " : " + offHandItem.getType());
        }

        if (hasBannedItem) player.sendMessage(plugin.getMsg("banned-item-inventory"));
    }

    private void checkAndRemoveIllegalNbt(Player player) {
        plugin.getNBTChecker().checkAndRemoveInventory(player.getInventory(), player, "inventory");
        plugin.getNBTChecker().checkAndRemoveOffHand(player);
    }

    private void checkAndRemoveIllegalEnchantments(Player player) {
        for (ItemStack item : player.getInventory()) {
            if (item != null && plugin.getEnchantmentChecker().hasIllegalEnchantments(item)) {
                plugin.getEnchantmentChecker().removeIllegalEnchantments(item);
                player.sendMessage(plugin.getMsg("enchant-inventory-remove"));
            }
        }

        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && plugin.getEnchantmentChecker().hasIllegalEnchantments(offHandItem)) {
            plugin.getEnchantmentChecker().removeIllegalEnchantments(offHandItem);
            player.sendMessage(plugin.getMsg("enchant-offhand-remove"));
        }
    }

    private boolean isContainer(InventoryType type) {
        return type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST ||
                type == InventoryType.SHULKER_BOX || type == InventoryType.BARREL ||
                type == InventoryType.DISPENSER || type == InventoryType.DROPPER ||
                type == InventoryType.HOPPER || type == InventoryType.CRAFTING ||
                type == InventoryType.ANVIL || type == InventoryType.BREWING ||
                type == InventoryType.ENCHANTING || type == InventoryType.FURNACE ||
                type == InventoryType.SMOKER || type == InventoryType.BLAST_FURNACE ||
                type == InventoryType.LOOM || type == InventoryType.CARTOGRAPHY ||
                type == InventoryType.GRINDSTONE || type == InventoryType.STONECUTTER ||
                type == InventoryType.SMITHING;
    }

    private void checkAndKillAboveNetherBedrock(Player player) {
        if (!plugin.shouldCheckNetherBedrock(player.getWorld())) return;
        double threshold = plugin.getNetherBedrockThreshold();
        if (player.getLocation().getY() > threshold) {
            player.setHealth(0.0);
            plugin.getLogger().info("[ICUAC] " + player.getName() + " is above Nether bedrock threshold");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;

        if (plugin.isPreventBelowBedrockEnabled()) {
            double threshold = plugin.getBedrockThresholdNormal();
            switch (player.getWorld().getEnvironment()) {
                case NETHER: case THE_END:
                    threshold = plugin.getBedrockThresholdNether();
                    break;
                default:
                    threshold = plugin.getBedrockThresholdNormal();
                    break;
            }
            if (player.getLocation().getY() < threshold) {
                player.setHealth(0.0);
                plugin.getLogger().info("[ICUAC] " + player.getName() + " fell below bedrock threshold (" + threshold + ")");
            }
        }
        checkAndKillAboveNetherBedrock(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWhitelistManager().isWhitelisted(player.getUniqueId(), player.getName())) return;

        if (plugin.isPreventBelowBedrockEnabled()) {
            double threshold = plugin.getBedrockThresholdNormal();
            switch (event.getTo().getWorld().getEnvironment()) {
                case NETHER: case THE_END:
                    threshold = plugin.getBedrockThresholdNether();
                    break;
                default:
                    threshold = plugin.getBedrockThresholdNormal();
                    break;
            }
            if (event.getTo().getY() < threshold) {
                player.setHealth(0.0);
                plugin.getLogger().info("[ICUAC] " + player.getName() + " teleported below bedrock threshold (" + threshold + ")");
            }
        }

        if (plugin.shouldCheckNetherBedrock(event.getTo().getWorld())) {
            double threshold = plugin.getNetherBedrockThreshold();
            if (event.getTo().getY() > threshold) {
                player.setHealth(0.0);
                plugin.getLogger().info("[ICUAC] " + player.getName() + " teleported above Nether bedrock threshold");
            }
        }
    }
}