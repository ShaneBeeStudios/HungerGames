package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.api.gui.KitGUI;
import com.shanebeestudios.hg.api.gui.KitsGUI;
import com.shanebeestudios.hg.plugin.HungerGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class GameKitGuiListener implements Listener {

    public GameKitGuiListener(HungerGames plugin) {
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof KitsGUI kitsGUI) {
            event.setCancelled(true);
            kitsGUI.click(event.getRawSlot());
        } else if (holder instanceof KitGUI kitGUI) {
            event.setCancelled(true);
            kitGUI.click(event.getRawSlot());
        }
    }

}
