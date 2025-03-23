package com.shanebeestudios.hg.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.PlayerSession;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Internal event listener
 */
public class WandListener implements Listener {

    private final HungerGames plugin;
    private final Language lang;

    public WandListener(HungerGames instance) {
        this.plugin = instance;
        this.lang = this.plugin.getLang();
    }

    @EventHandler
    private void onSelection(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if (block == null) return;

        Location location = block.getLocation();
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;

        PlayerSession session = this.plugin.getSessionManager().getPlayerSession(player.getUniqueId());

        if (session != null && (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_BLOCK))) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            event.setCancelled(true);

            for (Game game : this.plugin.getGameManager().getGames()) {
                if (game.getGameArenaData().getBound().isInRegion(location)) {
                    Util.sendPrefixedMessage(player, "&cThis location is already within an arena");
                    return;
                }
            }

            session.click(player, block);
        }
    }

}
