package tk.shanebee.hg.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.PlayerSession;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

/**
 * Internal event listener
 */
public class WandListener implements Listener {

    private final HG plugin;

    public WandListener(HG instance) {
        plugin = instance;
    }

    @EventHandler
    private void onSelection(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if (block == null) return;

        Location location = block.getLocation();
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;
        PlayerSession session = plugin.getPlayerSessions().get(player.getUniqueId());

        if (session != null && (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_BLOCK))) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            event.setCancelled(true);

            for (Game game : plugin.getGames()) {
                if (game.getGameArenaData().getBound().isInRegion(location)) {
                    Util.sendPrefixedMessage(player, "&cThis location is already within an arena");
                    return;
                }
            }

            String pos;
            if (session.getLoc1() == null) {
                session.setLoc1(location);
                pos = "Pos1";
            } else {
                session.setLoc2(location);
                pos = "Pos2";
            }
            Util.sendPrefixedMessage(player, "&b%s: &r%s, %s, %s", pos, location.getX(), location.getY(), location.getZ());
            if (!session.hasValidSelection()) {
                Util.sendPrefixedMessage(player, "&6Now you need to set position 2!");
            } else {
                Util.sendPrefixedMessage(player, "&6You're now ready to create your arena with '&a/hg create&6'");
            }
        }
    }

}
