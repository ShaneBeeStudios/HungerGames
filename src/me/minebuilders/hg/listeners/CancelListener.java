package me.minebuilders.hg.listeners;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;
import me.minebuilders.hg.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CancelListener implements Listener {

	public HG plugin;

	public CancelListener(HG instance) {
		plugin = instance;
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String[] st = event.getMessage().split(" ");
		if (plugin.players.containsKey(player.getUniqueId()) && !st[0].equalsIgnoreCase("/login")) {
			if (st[0].equalsIgnoreCase("/hg")) {
				if (st.length >= 2 && st[1].equalsIgnoreCase("kit") && plugin.players.get(player.getUniqueId()).getGame().getStatus() == Status.RUNNING) {
					event.setMessage("/");
					event.setCancelled(true);
					Util.msg(player, ChatColor.RED + "You can't choose a kit while the game is running!");
				}
				return;
			}
			event.setMessage("/");
			event.setCancelled(true);
			Util.msg(player, ChatColor.RED + "You can not use non-hungergames commands while ingame!");
		} else if ("/tp".equalsIgnoreCase(st[0]) && st.length >= 2) {
			Player p = Bukkit.getServer().getPlayer(st[1]);
			if (p != null) {
				if (plugin.players.containsKey(p.getUniqueId())) {
					player.sendMessage(ChatColor.RED+"This player is currently playing Hungergames!");
					event.setMessage("/");
					event.setCancelled(true);
				}
			}
		} 
	}
}
