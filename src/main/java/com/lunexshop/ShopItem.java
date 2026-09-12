package com.lunexshop;

import org.bukkit.Material;

public class ShopItem {

    private final int slot;
    private final Material material;
    private final int amount;
    private final double buyPrice;
    private final double sellPrice;
    private final String displayName;

    public ShopItem(int slot, Material material, int amount, double buyPrice, double sellPrice, String displayName) {
        this.slot = slot;
        this.material = material;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.displayName = displayName;
    }

    public int getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canBuy() {
        return buyPrice >= 0;
    }

    public boolean canSell() {
        return sellPrice >= 0;
    }
}
