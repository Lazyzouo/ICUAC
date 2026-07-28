package com.icu.icuac;

import com.google.common.collect.Multimap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class NBTChecker {

    private static final NBTCheckResult OK = new NBTCheckResult(false, "");

    private final ICUAC plugin;
    private boolean enabled;
    private boolean checkSerializedSize;
    private int maxSerializedBytes;
    private boolean checkMetaStringSize;
    private int maxMetaStringLength;
    private boolean checkBannedKeywords;
    private Set<String> bannedMetaKeywords;
    private boolean checkPersistentData;
    private int maxPersistentDataKeys;
    private int maxPersistentDataBytes;
    private boolean checkText;
    private int maxDisplayNameLength;
    private int maxLoreLines;
    private int maxLoreLineLength;
    private boolean checkAttributeModifiers;
    private boolean blockAttributeModifiers;
    private int maxAttributeModifiers;
    private double maxAttributeAmount;
    private Map<String, Double> maxAttributeAmounts;

    private boolean blockUnbreakable;
    private boolean blockHideTooltip;
    private boolean blockCustomModelData;
    private boolean blockCustomMaxStackSize;
    private boolean blockFireResistant;
    private boolean blockRarity;

    private boolean checkNestedItems;
    private int maxNestedDepth;

    public NBTChecker(ICUAC plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        enabled = plugin.getConfig().getBoolean("nbt-settings.enabled", true);
        checkSerializedSize = plugin.getConfig().getBoolean("nbt-settings.check-serialized-size", true);
        maxSerializedBytes = plugin.getConfig().getInt("nbt-settings.max-serialized-bytes", 65536);
        checkMetaStringSize = plugin.getConfig().getBoolean("nbt-settings.check-meta-string-size", true);
        maxMetaStringLength = plugin.getConfig().getInt("nbt-settings.max-meta-string-length", 16384);
        checkBannedKeywords = plugin.getConfig().getBoolean("nbt-settings.check-banned-keywords", true);
        bannedMetaKeywords = new HashSet<>();
        for (String keyword : plugin.getConfig().getStringList("nbt-settings.banned-meta-keywords")) {
            String normalized = keyword.toLowerCase(Locale.ROOT).trim();
            if (!normalized.isEmpty()) {
                bannedMetaKeywords.add(normalized);
            }
        }
        checkPersistentData = plugin.getConfig().getBoolean("nbt-settings.check-persistent-data", true);
        maxPersistentDataKeys = plugin.getConfig().getInt("nbt-settings.max-persistent-data-keys", 16);
        maxPersistentDataBytes = plugin.getConfig().getInt("nbt-settings.max-persistent-data-bytes", 4096);
        checkText = plugin.getConfig().getBoolean("nbt-settings.check-text", true);
        maxDisplayNameLength = plugin.getConfig().getInt("nbt-settings.max-display-name-length", 128);
        maxLoreLines = plugin.getConfig().getInt("nbt-settings.max-lore-lines", 20);
        maxLoreLineLength = plugin.getConfig().getInt("nbt-settings.max-lore-line-length", 256);
        checkAttributeModifiers = plugin.getConfig().getBoolean("nbt-settings.check-attribute-modifiers", true);
        blockAttributeModifiers = plugin.getConfig().getBoolean("nbt-settings.unsafe-flags.block-attribute-modifiers", true);
        maxAttributeModifiers = plugin.getConfig().getInt("nbt-settings.max-attribute-modifiers", 16);
        maxAttributeAmount = plugin.getConfig().getDouble("nbt-settings.max-attribute-amount", 1024.0D);
        maxAttributeAmounts = new HashMap<>();
        ConfigurationSection attributeSection = plugin.getConfig().getConfigurationSection("nbt-settings.max-attribute-amounts");
        if (attributeSection != null) {
            for (String key : attributeSection.getKeys(false)) {
                maxAttributeAmounts.put(normalizeAttributeName(key), attributeSection.getDouble(key));
            }
        }

        blockUnbreakable = plugin.getConfig().getBoolean("nbt-settings.unsafe-flags.block-unbreakable", true);
        blockHideTooltip = plugin.getConfig().getBoolean("nbt-settings.unsafe-flags.block-hide-tooltip", true);
        blockCustomModelData = plugin.getConfig().getBoolean("nbt-settings.unsafe-flags.block-custom-model-data", true);
        blockCustomMaxStackSize = plugin.getConfig().getBoolean("nbt-settings.unsafe-flags.block-custom-max-stack-size", true);
        blockFireResistant = plugin.getConfig().getBoolean("nbt-settings.unsafe-flags.block-fire-resistant", true);
        blockRarity = plugin.getConfig().getBoolean("nbt-settings.unsafe-flags.block-rarity", true);

        checkNestedItems = plugin.getConfig().getBoolean("nbt-settings.check-nested-items", true);
        maxNestedDepth = plugin.getConfig().getInt("nbt-settings.max-nested-depth", 2);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasIllegalNbt(ItemStack item) {
        return check(item).isIllegal();
    }

    public String getIllegalReason(ItemStack item) {
        return check(item).getReason();
    }

    public NBTCheckResult check(ItemStack item) {
        if (!enabled) {
            return OK;
        }
        return checkItem(item, 0);
    }

    public int checkAndRemoveInventory(Inventory inventory, Player player, String sourceName) {
        if (!enabled || inventory == null) {
            return 0;
        }

        int removed = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            NBTCheckResult result = check(item);
            if (result.isIllegal()) {
                inventory.setItem(i, null);
                removed++;
                plugin.getLogger().info("[ICUAC] " + player.getName() + " 的" + sourceName
                        + "中发现异常NBT物品: " + item.getType() + "（" + result.getReason() + "），已删除");
            }
        }

        if (removed > 0) {
            MessageUtils.send(player, plugin, "nbt-inventory-cleaned", "source", sourceName);
        }
        return removed;
    }

    public boolean checkAndRemoveOffHand(Player player) {
        if (!enabled) {
            return false;
        }

        ItemStack offHand = player.getInventory().getItemInOffHand();
        NBTCheckResult result = check(offHand);
        if (!result.isIllegal()) {
            return false;
        }

        player.getInventory().setItemInOffHand(null);
        MessageUtils.send(player, plugin, "nbt-offhand-cleaned");

        plugin.getLogger().info("[ICUAC] " + player.getName() + " 的副手中发现异常NBT物品: "
                + offHand.getType() + "（" + result.getReason() + "），已删除");
        return true;
    }

    public void reload() {
        loadConfig();
    }

    private NBTCheckResult checkItem(ItemStack item, int depth) {
        if (item == null || item.getType() == Material.AIR || item.getType().isAir()) {
            return OK;
        }

        if (depth > maxNestedDepth) {
            return illegal("NBT嵌套层数过深");
        }

        if (checkSerializedSize) {
            try {
                int size = item.serializeAsBytes().length;
                if (size > maxSerializedBytes) {
                    return illegal("序列化大小过大: " + size + " bytes");
                }
            } catch (RuntimeException exception) {
                return illegal("物品序列化失败");
            }
        }

        if (!item.hasItemMeta()) {
            return OK;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return OK;
        }

        NBTCheckResult textResult = checkText(meta);
        if (textResult.isIllegal()) {
            return textResult;
        }

        NBTCheckResult unsafeFlagResult = checkUnsafeFlags(meta);
        if (unsafeFlagResult.isIllegal()) {
            return unsafeFlagResult;
        }

        NBTCheckResult metaStringResult = checkMetaString(meta);
        if (metaStringResult.isIllegal()) {
            return metaStringResult;
        }

        NBTCheckResult persistentDataResult = checkPersistentData(meta);
        if (persistentDataResult.isIllegal()) {
            return persistentDataResult;
        }

        NBTCheckResult attributeResult = checkAttributeModifiers(meta);
        if (attributeResult.isIllegal()) {
            return attributeResult;
        }

        if (checkNestedItems) {
            NBTCheckResult nestedResult = checkNestedItems(meta, depth);
            if (nestedResult.isIllegal()) {
                return nestedResult;
            }
        }

        return OK;
    }

    private NBTCheckResult checkText(ItemMeta meta) {
        if (!checkText) {
            return OK;
        }

        if (meta.hasDisplayName() && meta.getDisplayName().length() > maxDisplayNameLength) {
            return illegal("显示名过长: " + meta.getDisplayName().length());
        }

        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                if (lore.size() > maxLoreLines) {
                    return illegal("Lore行数过多: " + lore.size());
                }
                for (String line : lore) {
                    if (line != null && line.length() > maxLoreLineLength) {
                        return illegal("Lore单行过长: " + line.length());
                    }
                }
            }
        }

        return OK;
    }

    private NBTCheckResult checkUnsafeFlags(ItemMeta meta) {
        if (blockUnbreakable && meta.isUnbreakable()) {
            return illegal("包含Unbreakable标签");
        }
        if (blockHideTooltip && meta.isHideTooltip()) {
            return illegal("隐藏物品提示");
        }
        if (blockCustomModelData && meta.hasCustomModelData()) {
            return illegal("自定义模型数据");
        }
        if (blockCustomMaxStackSize && meta.hasMaxStackSize()) {
            return illegal("自定义最大堆叠数量");
        }
        if (blockFireResistant && meta.isFireResistant()) {
            return illegal("自定义防火标签");
        }
        if (blockRarity && meta.hasRarity()) {
            return illegal("自定义稀有度");
        }

        return OK;
    }

    private NBTCheckResult checkMetaString(ItemMeta meta) {
        String metaString;
        try {
            metaString = meta.getAsString();
        } catch (RuntimeException exception) {
            return illegal("NBT字符串读取失败");
        }

        if (metaString == null) {
            return OK;
        }

        if (checkMetaStringSize && metaString.length() > maxMetaStringLength) {
            return illegal("NBT字符串过长: " + metaString.length());
        }

        if (checkBannedKeywords && !bannedMetaKeywords.isEmpty()) {
            String normalized = metaString.toLowerCase(Locale.ROOT);
            for (String keyword : bannedMetaKeywords) {
                if (normalized.contains(keyword)) {
                    return illegal("包含禁止NBT关键字: " + keyword);
                }
            }
        }

        return OK;
    }

    private NBTCheckResult checkPersistentData(ItemMeta meta) {
        if (!checkPersistentData) {
            return OK;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        Set<NamespacedKey> keys = container.getKeys();
        if (keys.size() > maxPersistentDataKeys) {
            return illegal("自定义NBT键过多: " + keys.size());
        }

        try {
            int size = container.serializeToBytes().length;
            if (size > maxPersistentDataBytes) {
                return illegal("自定义NBT数据过大: " + size + " bytes");
            }
        } catch (IOException exception) {
            return illegal("自定义NBT序列化失败");
        }

        return OK;
    }

    private NBTCheckResult checkAttributeModifiers(ItemMeta meta) {
        if (!checkAttributeModifiers || !meta.hasAttributeModifiers()) {
            return OK;
        }

        Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
        if (modifiers == null || modifiers.isEmpty()) {
            return OK;
        }

        if (blockAttributeModifiers) {
            return illegal("包含自定义属性修改器");
        }

        if (modifiers.size() > maxAttributeModifiers) {
            return illegal("属性修改器过多: " + modifiers.size());
        }

        for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : modifiers.asMap().entrySet()) {
            Attribute attribute = entry.getKey();
            double maxAllowedAmount = getMaxAllowedAttributeAmount(attribute);
            for (AttributeModifier modifier : entry.getValue()) {
                double amount = modifier.getAmount();
                if (!Double.isFinite(amount)) {
                    return illegal("属性修改器数值无效: " + getAttributeName(attribute));
                }
                if (Math.abs(amount) > maxAllowedAmount) {
                    return illegal("属性修改器数值过大: " + getAttributeName(attribute) + "=" + amount
                            + "，最大允许: " + maxAllowedAmount);
                }
            }
        }

        return OK;
    }

    private double getMaxAllowedAttributeAmount(Attribute attribute) {
        Double specificLimit = maxAttributeAmounts.get(normalizeAttributeName(getAttributeName(attribute)));
        if (specificLimit != null) {
            return specificLimit;
        }
        return maxAttributeAmount;
    }

    private String getAttributeName(Attribute attribute) {
        if (attribute == null) {
            return "UNKNOWN";
        }
        NamespacedKey key = attribute.getKey();
        if (key == null) {
            return attribute.toString();
        }
        return key.getKey();
    }

    private String normalizeAttributeName(String name) {
        String normalized = name.toUpperCase(Locale.ROOT).trim().replace('-', '_');
        int namespaceIndex = normalized.indexOf(':');
        if (namespaceIndex >= 0 && namespaceIndex + 1 < normalized.length()) {
            normalized = normalized.substring(namespaceIndex + 1);
        }
        return normalized;
    }

    private NBTCheckResult checkNestedItems(ItemMeta meta, int depth) {
        if (meta instanceof BlockStateMeta blockStateMeta && blockStateMeta.hasBlockState()) {
            BlockState state = blockStateMeta.getBlockState();
            if (state instanceof Container container) {
                NBTCheckResult result = checkInventoryContents(container.getSnapshotInventory(), depth + 1);
                if (result.isIllegal()) {
                    return result;
                }
            }
        }

        if (meta instanceof BundleMeta bundleMeta && bundleMeta.hasItems()) {
            for (ItemStack nested : bundleMeta.getItems()) {
                NBTCheckResult result = checkContainedItem(nested, depth + 1);
                if (result.isIllegal()) {
                    return result;
                }
            }
        }

        return OK;
    }

    private NBTCheckResult checkInventoryContents(Inventory inventory, int depth) {
        for (ItemStack nested : inventory.getContents()) {
            NBTCheckResult result = checkContainedItem(nested, depth);
            if (result.isIllegal()) {
                return result;
            }
        }
        return OK;
    }

    private NBTCheckResult checkContainedItem(ItemStack nested, int depth) {
        if (nested == null || nested.getType() == Material.AIR || nested.getType().isAir()) {
            return OK;
        }

        if (plugin.getItemChecker() != null && plugin.getItemChecker().isBanned(nested)) {
            return illegal("容器NBT内包含违禁物品: " + nested.getType());
        }

        if (plugin.getEnchantmentChecker() != null && plugin.getEnchantmentChecker().hasIllegalEnchantments(nested)) {
            return illegal("容器NBT内包含异常附魔物品: " + nested.getType());
        }

        if (plugin.getStackSizeChecker() != null && plugin.isIllegalStackCheckEnabled()
                && plugin.getStackSizeChecker().hasIllegalStackSize(nested)) {
            return illegal("容器NBT内包含异常堆叠物品: " + nested.getType());
        }

        NBTCheckResult result = checkItem(nested, depth);
        if (result.isIllegal()) {
            return illegal("容器NBT内包含异常物品: " + result.getReason());
        }

        return OK;
    }

    private NBTCheckResult illegal(String reason) {
        return new NBTCheckResult(true, reason);
    }

    public static final class NBTCheckResult {
        private final boolean illegal;
        private final String reason;

        private NBTCheckResult(boolean illegal, String reason) {
            this.illegal = illegal;
            this.reason = reason;
        }

        public boolean isIllegal() {
            return illegal;
        }

        public String getReason() {
            return reason;
        }
    }
}
