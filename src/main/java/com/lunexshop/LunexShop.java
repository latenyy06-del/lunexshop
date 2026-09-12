package com.lunexshop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class LunexShop extends JavaPlugin {

    private static LunexShop instance;
    private Economy economy;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault + an economy plugin (e.g. EssentialsX) not found! Disabling LunexShop.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.shopManager = new ShopManager(this);

        ShopCommand shopCommand = new ShopCommand(this);
        getCommand("shop").setExecutor(shopCommand);
        getCommand("market").setExecutor(shopCommand);
        getCommand("lshop").setExecutor(shopCommand);
        getCommand("sell").setExecutor(new SellCommand(this));

        getServer().getPluginManager().registerEvents(new ShopListener(this), this);

        getLogger().info("LunexShop enabled. Hooked into economy: " + economy.getName());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public static LunexShop getInstance() {
        return instance;
    }
}
