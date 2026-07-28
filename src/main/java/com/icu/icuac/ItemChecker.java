package com.icu.icuac;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ItemChecker {
    private final ICUAC plugin;
    private final Set<Material> bannedItems;

    public ItemChecker(ICUAC plugin) {
        this.plugin = plugin;
        this.bannedItems = new HashSet<>();
        loadBannedItems();
    }

    private void loadBannedItems() {
        if (plugin.getConfig().contains("banned-items")) {
            for (String materialName : plugin.getConfig().getStringList("banned-items")) {
                try {
                    Material material = Material.valueOf(materialName.toUpperCase());
                    bannedItems.add(material);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("无效的物品类型: " + materialName);
                }
            }
        }
    }

    public boolean isBanned(ItemStack item) {
        if (item == null) return false;
        return bannedItems.contains(item.getType());
    }

    public void reload() {
        bannedItems.clear();
        loadBannedItems();
    }
}