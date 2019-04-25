package tk.shanebee.hg.listeners;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.PlayerSession;
import tk.shanebee.hg.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener implements Listener {
	public HG plugin;

	public WandListener(HG instance) {
		plugin = instance;
	}

	@EventHandler
	public void onSelection(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();

		if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			if (!player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;
			if (!plugin.playerses.containsKey(player.getUniqueId())) return;
			Location l = event.getClickedBlock().getLocation();
			event.setCancelled(true);
			for (Game game : HG.plugin.games) {
				if (game.getRegion().isInRegion(l)) {
					Util.scm(player, "&cThis location is already within an arena");
					return;
				}
			}
			PlayerSession ses = plugin.playerses.get(player.getUniqueId());
			ses.setLoc2(l);
			Util.msg(player, "Pos2: "+l.getX()+", "+l.getY()+", "+l.getZ());
			if (!ses.hasValidSelection()) {
				Util.msg(player, "Now you need to set position 1!");
			}
		} else if (action.equals(Action.LEFT_CLICK_BLOCK)) {
			if (!player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;
			if (!plugin.playerses.containsKey(player.getUniqueId())) return;
			Location l = event.getClickedBlock().getLocation();
			event.setCancelled(true);
			for (Game game : HG.plugin.games) {
				if (game.getRegion().isInRegion(l)) {
					Util.scm(player, "&cThis location is already within an arena");
					return;
				}
			}
			PlayerSession ses = plugin.playerses.get(player.getUniqueId());
			ses.setLoc1(l);
			Util.msg(player, "Pos1: "+l.getX()+", "+l.getY()+", "+l.getZ());
			if (!ses.hasValidSelection()) {
				Util.msg(player, "Now you need to set position 2!");
			}
		}
	}
}