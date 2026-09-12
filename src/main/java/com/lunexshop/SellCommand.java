package com.lunexshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SellCommand implements CommandExecutor {

    private final LunexShop plugin;

    public SellCommand(LunexShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can sell items.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /sell <hand|all>");
            return true;
        }

        PlayerInventory inv = player.getInventory();

        if (args[0].equalsIgnoreCase("hand")) {
            ItemStack hand = inv.getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You aren't holding anything.");
                return true;
            }
            sellStack(player, hand, hand.getAmount());
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {
            double total = 0;
            int totalItems = 0;
            for (ItemStack stack : inv.getContents()) {
                if (stack == null || stack.getType() == Material.AIR) continue;
                ShopItem shopItem = plugin.getShopManager().findByMaterial(stack.getType());
                if (shopItem == null || !shopItem.canSell()) continue;

                int amount = stack.getAmount();
                double unitPrice = shopItem.getSellPrice() / shopItem.getAmount();
                double payout = unitPrice * amount;

                total += payout;
                totalItems += amount;
                stack.setAmount(0);
            }

            if (totalItems == 0) {
                player.sendMessage(ChatColor.RED + "You have nothing sellable in your inventory.");
                return true;
            }

            plugin.getEconomy().depositPlayer(player, total);
            player.sendMessage(ChatColor.GREEN + "Sold " + totalItems + " items for "
                    + plugin.getShopManager().formatMoney(total) + ".");
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Usage: /sell <hand|all>");
        return true;
    }

    private void sellStack(Player player, ItemStack stack, int amount) {
        ShopItem shopItem = plugin.getShopManager().findByMaterial(stack.getType());
        if (shopItem == null || !shopItem.canSell()) {
            player.sendMessage(ChatColor.RED + "That item can't be sold here.");
            return;
        }

        double unitPrice = shopItem.getSellPrice() / shopItem.getAmount();
        double payout = unitPrice * amount;

        stack.setAmount(0);
        plugin.getEconomy().depositPlayer(player, payout);
        player.sendMessage(ChatColor.GREEN + "Sold " + amount + "x " + shopItem.getDisplayName()
                + ChatColor.GREEN + " for " + plugin.getShopManager().formatMoney(payout) + ".");
    }
}
