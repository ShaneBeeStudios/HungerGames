package com.shanebeestudios.hg.api.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * General Vault class
 */
public class Vault {

    public static Economy ECONOMY = null;

    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            ECONOMY = economyProvider.getProvider();
        }
        return (ECONOMY != null);
    }

}
