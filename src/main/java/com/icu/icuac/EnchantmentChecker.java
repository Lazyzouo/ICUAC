package com.icu.icuac;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class EnchantmentChecker {
    private final ICUAC plugin;

    public EnchantmentChecker(ICUAC plugin) {
        this.plugin = plugin;
    }

    public boolean hasIllegalEnchantments(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchants()) return false;

        Map<Enchantment, Integer> enchants = meta.getEnchants();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (level > enchantment.getMaxLevel() || !enchantment.canEnchantItem(item)) return true;
        }
        return false;
    }

    public void removeIllegalEnchantments(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchants()) return;

        Map<Enchantment, Integer> enchants = meta.getEnchants();
        boolean hasIllegal = false;

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (level > enchantment.getMaxLevel() || !enchantment.canEnchantItem(item)) {
                meta.removeEnchant(enchantment);
                hasIllegal = true;
            }
        }
        if (hasIllegal) item.setItemMeta(meta);
    }
}