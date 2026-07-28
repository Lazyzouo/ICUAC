package com.icu.icuac;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StackSizeChecker {

    private final ICUAC plugin;
    private final Map<Material, Integer> maxStackSizes;

    public StackSizeChecker(ICUAC plugin) {
        this.plugin = plugin;
        this.maxStackSizes = new HashMap<>();
        loadMaxStackSizes();
    }

    private void loadMaxStackSizes() {
        for (Material material : Material.values()) {
            if (material.isItem()) {
                maxStackSizes.put(material, material.getMaxStackSize());
            }
        }
    }

    public boolean hasIllegalStackSize(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        int maxSize = maxStackSizes.getOrDefault(item.getType(), item.getType().getMaxStackSize());
        return item.getAmount() > maxSize;
    }

    public void fixIllegalStackSize(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        int maxSize = maxStackSizes.getOrDefault(item.getType(), item.getType().getMaxStackSize());
        if (item.getAmount() > maxSize) {
            item.setAmount(maxSize);
        }
    }

    public void checkAndFixInventory(Inventory inventory, Player player) {
        boolean hasFixed = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && hasIllegalStackSize(item)) {
                int originalAmount = item.getAmount();
                fixIllegalStackSize(item);
                hasFixed = true;
                plugin.getLogger().info("[ICUAC] " + player.getName() + " 的背包中发现异常堆叠物品: "
                        + item.getType() + " (" + originalAmount + " -> " + item.getAmount() + ")");
            }
        }
        if (hasFixed) {
            player.sendMessage(plugin.getMsg("stack-inventory-fixed"));
        }
    }

    public void checkAndFixOffHand(Player player) {
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && hasIllegalStackSize(offHandItem)) {
            int originalAmount = offHandItem.getAmount();
            fixIllegalStackSize(offHandItem);
            player.sendMessage(plugin.getMsg("stack-offhand-fixed"));
            plugin.getLogger().info("[ICUAC] " + player.getName() + " 的副手中发现异常堆叠物品: "
                    + offHandItem.getType() + " (" + originalAmount + " -> " + offHandItem.getAmount() + ")");
        }
    }

    public void reload() {
        maxStackSizes.clear();
        loadMaxStackSizes();
    }
}