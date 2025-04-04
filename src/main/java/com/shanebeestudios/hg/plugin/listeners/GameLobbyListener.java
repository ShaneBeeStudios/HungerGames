package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.Constants;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GameLobbyListener extends GameListenerBase {

    public GameLobbyListener(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        assert block != null;
        if (!Tag.WALL_SIGNS.isTagged(block.getType())) return;

        Sign sign = (Sign) block.getState();
        PersistentDataContainer pdc = sign.getPersistentDataContainer();
        if (pdc.has(Constants.LOBBY_SIGN_KEY, PersistentDataType.STRING)) {
            String name = pdc.get(Constants.LOBBY_SIGN_KEY, PersistentDataType.STRING);
            Game game = this.gameManager.getGame(name);
            if (game == null) {
                Util.sendMessage(player, this.lang.command_delete_noexist);
            } else {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    // Process this after event has finished running to prevent double click issues
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> game.joinGame(player), 2);
                } else {
                    Util.sendMessage(player, this.lang.listener_sign_click_hand);
                }
            }
        }
    }
}
