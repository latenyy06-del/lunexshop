package com.lunexshop;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final LunexShop plugin;

    public ShopCommand(LunexShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("lshop")) {
            if (!sender.hasPermission("lunexshop.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                return true;
            }
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                plugin.getShopManager().load();
                sender.sendMessage(ChatColor.GREEN + "LunexShop config reloaded.");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /lshop reload");
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can open the shop.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("lunexshop.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
            return true;
        }

        player.openInventory(plugin.getShopManager().buildInventory());
        return true;
    }
}
