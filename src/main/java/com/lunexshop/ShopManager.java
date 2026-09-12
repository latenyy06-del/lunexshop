package com.lunexshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopManager {

    private final LunexShop plugin;
    private String guiTitle;
    private int size;
    private final Map<Integer, ShopItem> items = new HashMap<>();

    public ShopManager(LunexShop plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.reloadConfig();
        items.clear();

        guiTitle = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("gui-title", "&6&lLunexShop"));
        size = plugin.getConfig().getInt("size", 54);
        if (size % 9 != 0 || size <= 0 || size > 54) {
            size = 54;
        }

        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("items");
        if (itemsSection == null) {
            return;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection sec = itemsSection.getConfigurationSection(key);
            if (sec == null) continue;

            try {
                int slot = Integer.parseInt(key);
                Material material = Material.matchMaterial(sec.getString("material", "STONE"));
                if (material == null) {
                    plugin.getLogger().warning("Unknown material for shop slot " + key + ", skipping.");
                    continue;
                }
                int amount = sec.getInt("amount", 1);
                double buyPrice = sec.getDouble("buy-price", -1);
                double sellPrice = sec.getDouble("sell-price", -1);
                String name = ChatColor.translateAlternateColorCodes('&',
                        sec.getString("name", material.name()));

                items.put(slot, new ShopItem(slot, material, amount, buyPrice, sellPrice, name));
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Shop slot key '" + key + "' is not a number, skipping.");
            }
        }
    }

    public Inventory buildInventory() {
        Inventory inv = plugin.getServer().createInventory(null, size, guiTitle);
        for (ShopItem item : items.values()) {
            if (item.getSlot() < 0 || item.getSlot() >= size) continue;
            inv.setItem(item.getSlot(), buildIcon(item));
        }
        return inv;
    }

    private ItemStack buildIcon(ShopItem item) {
        ItemStack stack = new ItemStack(item.getMaterial(), Math.max(1, item.getAmount()));
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(item.getDisplayName());
            List<String> lore = new ArrayList<>();
            if (item.canBuy()) {
                lore.add(ChatColor.GREEN + "Left-click to buy " + item.getAmount()
                        + " for " + formatMoney(item.getBuyPrice()));
            }
            if (item.canSell()) {
                lore.add(ChatColor.GOLD + "Right-click to sell " + item.getAmount()
                        + " for " + formatMoney(item.getSellPrice()));
            }
            if (!item.canBuy() && !item.canSell()) {
                lore.add(ChatColor.GRAY + "Not for sale");
            }
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public String formatMoney(double amount) {
        return String.format("%.2f", amount);
    }

    public ShopItem getItemAtSlot(int slot) {
        return items.get(slot);
    }

    public ShopItem findByMaterial(Material material) {
        for (ShopItem item : items.values()) {
            if (item.getMaterial() == material) {
                return item;
            }
        }
        return null;
    }

    public String getGuiTitle() {
        return guiTitle;
    }
}
