package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

/**
 * Internal event listener
 */
public class GameCommandListener extends GameListenerBase {

    public GameCommandListener(HungerGames instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (Permissions.BYPASS_COMMAND_RESTRICTION.has(player)) return;

        UUID uuid = player.getUniqueId();
        String[] st = event.getMessage().split(" ");
        // Prevent game players running non hunger games commands
        if (this.playerManager.hasData(uuid) && !st[0].equalsIgnoreCase("/login")) {
            if (st[0].equalsIgnoreCase("/hg") || st[0].equalsIgnoreCase("/hungergames")) {
                return;
            }
            event.setMessage("/");
            event.setCancelled(true);
            Util.sendMessage(player, this.lang.cmd_handler_nocmd);
        }
        // Prevent teleporting players out of an arena
        else if (("/tp".equalsIgnoreCase(st[0]) || "/teleport".equalsIgnoreCase(st[0])) && st.length >= 2) {
            Player p = Bukkit.getServer().getPlayer(st[1]);
            if (p != null) {
                if (this.playerManager.hasPlayerData(uuid)) {
                    Util.sendMessage(player, this.lang.cmd_handler_playing);
                    event.setMessage("/");
                    event.setCancelled(true);
                }
            }
        }
    }

}
