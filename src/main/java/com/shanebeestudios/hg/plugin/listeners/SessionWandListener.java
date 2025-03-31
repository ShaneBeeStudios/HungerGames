package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.PlayerSession;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.managers.GameManager;
import com.shanebeestudios.hg.managers.SessionManager;
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
public class SessionWandListener implements Listener {

    private final SessionManager sessionManager;
    private final GameManager gameManager;
    private final Language lang;

    public SessionWandListener(HungerGames plugin) {
        this.sessionManager = plugin.getSessionManager();
        this.gameManager = plugin.getGameManager();
        this.lang = plugin.getLang();
    }

    @EventHandler
    private void onSelection(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if (block == null) return;

        Location location = block.getLocation();
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.STICK)) return;

        PlayerSession session = this.sessionManager.getPlayerSession(player);

        if (session != null && (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_BLOCK))) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            event.setCancelled(true);

            for (Game game : this.gameManager.getGames()) {
                if (game.getGameArenaData().getGameRegion().isInRegion(location)) {
                    Util.sendPrefixedMessage(player, this.lang.command_create_session_already_in_arena);
                    return;
                }
            }

            session.click(player, block);
        }
    }

}
