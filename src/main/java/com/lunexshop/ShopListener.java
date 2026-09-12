package com.lunexshop;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShopListener implements Listener {

    private final LunexShop plugin;

    public ShopListener(LunexShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(plugin.getShopManager().getGuiTitle())) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        ShopItem shopItem = plugin.getShopManager().getItemAtSlot(event.getSlot());
        if (shopItem == null) {
            return;
        }

        ClickType click = event.getClick();

        if (click == ClickType.LEFT) {
            buy(player, shopItem);
        } else if (click == ClickType.RIGHT) {
            sell(player, shopItem);
        }
    }

    private void buy(Player player, ShopItem item) {
        if (!item.canBuy()) {
            player.sendMessage(ChatColor.RED + "That item isn't for sale.");
            return;
        }

        double balance = plugin.getEconomy().getBalance(player);
        if (balance < item.getBuyPrice()) {
            player.sendMessage(ChatColor.RED + "You can't afford that. You need "
                    + plugin.getShopManager().formatMoney(item.getBuyPrice()) + ".");
            return;
        }

        Map<Integer, ItemStack> leftover = player.getInventory()
                .addItem(new ItemStack(item.getMaterial(), item.getAmount()));
        if (!leftover.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Your inventory is full.");
            return;
        }

        EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, item.getBuyPrice());
        if (!response.transactionSuccess()) {
            player.getInventory().removeItem(new ItemStack(item.getMaterial(), item.getAmount()));
            player.sendMessage(ChatColor.RED + "Purchase failed: " + response.errorMessage);
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Bought " + item.getAmount() + "x " + item.getDisplayName()
                + ChatColor.GREEN + " for " + plugin.getShopManager().formatMoney(item.getBuyPrice()) + ".");
    }

    private void sell(Player player, ShopItem item) {
        if (!item.canSell()) {
            player.sendMessage(ChatColor.RED + "That item can't be sold here.");
            return;
        }

        ItemStack toRemove = new ItemStack(item.getMaterial(), item.getAmount());
        if (!player.getInventory().containsAtLeast(toRemove, item.getAmount())) {
            player.sendMessage(ChatColor.RED + "You don't have " + item.getAmount()
                    + "x " + item.getDisplayName() + ChatColor.RED + " to sell.");
            return;
        }

        Map<Integer, ItemStack> notRemoved = player.getInventory().removeItem(toRemove);
        if (!notRemoved.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Something went wrong removing the items.");
            return;
        }

        plugin.getEconomy().depositPlayer(player, item.getSellPrice());
        player.sendMessage(ChatColor.GREEN + "Sold " + item.getAmount() + "x " + item.getDisplayName()
                + ChatColor.GREEN + " for " + plugin.getShopManager().formatMoney(item.getSellPrice()) + ".");
    }
}
