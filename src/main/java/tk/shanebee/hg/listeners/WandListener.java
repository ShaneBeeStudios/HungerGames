package tk.shanebee.hg.listeners;

import org.bukkit.inventory.EquipmentSlot;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.PlayerSession;
import tk.shanebee.hg.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Internal event listener
 */
public class WandListener implements Listener {

	public HG plugin;

	public WandListener(HG instance) {
		plugin = instance;
	}

	@EventHandler
	private void onSelection(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();

		assert event.getClickedBlock() != null;
		if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			if (!player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;
			if (!plugin.getPlayerSessions().containsKey(player.getUniqueId())) return;
			if (event.getHand() == EquipmentSlot.OFF_HAND) return;
			Location l = event.getClickedBlock().getLocation();
			event.setCancelled(true);
			for (Game game : HG.getPlugin().getGames()) {
				if (game.getGameArenaData().getBound().isInRegion(l)) {
					Util.scm(player, "&cThis location is already within an arena");
					return;
				}
			}
			PlayerSession ses = plugin.getPlayerSessions().get(player.getUniqueId());
			ses.setLoc2(l);
			Util.scm(player, "Pos2: "+l.getX()+", "+l.getY()+", "+l.getZ());
			if (!ses.hasValidSelection()) {
				Util.scm(player, "Now you need to set position 1!");
			}
		} else if (action.equals(Action.LEFT_CLICK_BLOCK)) {
			if (!player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;
			if (!plugin.getPlayerSessions().containsKey(player.getUniqueId())) return;
			Location l = event.getClickedBlock().getLocation();
			event.setCancelled(true);
			for (Game game : HG.getPlugin().getGames()) {
				if (game.getGameArenaData().getBound().isInRegion(l)) {
					Util.scm(player, "&cThis location is already within an arena");
					return;
				}
			}
			PlayerSession ses = plugin.getPlayerSessions().get(player.getUniqueId());
			ses.setLoc1(l);
			Util.scm(player, "Pos1: "+l.getX()+", "+l.getY()+", "+l.getZ());
			if (!ses.hasValidSelection()) {
				Util.scm(player, "Now you need to set position 2!");
			}
		}
	}

}
