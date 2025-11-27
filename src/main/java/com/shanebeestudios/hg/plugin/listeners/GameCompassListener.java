package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.api.data.PlayerData;
import com.shanebeestudios.hg.api.gui.SpectatorGUI;
import com.shanebeestudios.hg.api.util.Constants;
import com.shanebeestudios.hg.plugin.HungerGames;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GameCompassListener extends GameListenerBase {

    public GameCompassListener(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            if (isSpectatorCompass(event)) {
                handleSpectatorCompass(player);
            }
        }
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (!(clickedInventory.getHolder() instanceof SpectatorGUI spectatorGUI))
            return;

        Player player = (Player) event.getWhoClicked();
        if (!this.playerManager.hasSpectatorData(player)) return;

        event.setCancelled(true);
        spectatorGUI.click(event.getRawSlot());
    }

    // UTIL

    private boolean isSpectatorCompass(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return false;
        if (!this.playerManager.hasSpectatorData(player)) return false;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.COMPASS) return false;

        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        return pdc.has(Constants.SPECTATOR_COMPASS_KEY, PersistentDataType.BOOLEAN);
    }

    private void handleSpectatorCompass(Player player) {
        PlayerData spectatorData = this.playerManager.getSpectatorData(player);
        assert spectatorData != null;
        new SpectatorGUI(spectatorData.getGame(), player).open();
    }

}
