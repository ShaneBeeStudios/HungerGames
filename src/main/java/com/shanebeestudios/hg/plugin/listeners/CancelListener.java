package com.shanebeestudios.hg.plugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.managers.PlayerManager;
import com.shanebeestudios.hg.util.Util;

import java.util.UUID;

/**
 * Internal event listener
 */
public class CancelListener implements Listener {

	private PlayerManager playerManager;

	public CancelListener(HungerGames instance) {
		this.playerManager = instance.getPlayerManager();
	}

	@EventHandler(priority=EventPriority.LOWEST)
	private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("hg.command.bypass")) return;
        UUID uuid = player.getUniqueId();
		String[] st = event.getMessage().split(" ");
		if (playerManager.hasData(uuid) && !st[0].equalsIgnoreCase("/login")) {
			if (st[0].equalsIgnoreCase("/hg")) {
				if (st.length >= 2 && st[1].equalsIgnoreCase("kit") && playerManager.getData(uuid).getGame().getGameArenaData().getStatus() == Status.RUNNING) {
					event.setMessage("/");
					event.setCancelled(true);
					Util.scm(player, HungerGames.getPlugin().getLang().cmd_handler_nokit);
				}
				return;
			}
			event.setMessage("/");
			event.setCancelled(true);
			Util.scm(player, HungerGames.getPlugin().getLang().cmd_handler_nocmd);
		} else if ("/tp".equalsIgnoreCase(st[0]) && st.length >= 2) {
			Player p = Bukkit.getServer().getPlayer(st[1]);
			if (p != null) {
				if (playerManager.hasPlayerData(uuid)) {
					Util.scm(player, HungerGames.getPlugin().getLang().cmd_handler_playing);
					event.setMessage("/");
					event.setCancelled(true);
				}
			}
		}
	}
}
