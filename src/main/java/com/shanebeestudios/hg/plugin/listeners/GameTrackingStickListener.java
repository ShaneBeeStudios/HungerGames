package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class GameTrackingStickListener extends GameListenerBase {

    public GameTrackingStickListener(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR) {
            if (player.getInventory().getItemInMainHand().getType().equals(Material.STICK) && playerManager.hasPlayerData(player)) {
                useTrackStick(player);
            }
        }
    }

    // UTIL
    private void useTrackStick(Player p) {
//        ItemStack i = p.getInventory().getItemInMainHand();
//        ItemMeta im = i.getItemMeta();
//        assert im != null;
//        im.getDisplayName();
//        if (im.getDisplayName().startsWith(tsn)) {
//            int uses = Integer.parseInt(im.getDisplayName().replace(tsn, ""));
//            if (uses == 0) {
//                Util.sendMessage(p, lang.track_empty);
//            } else {
//                PlayerData pd = playerManager.getPlayerData(p);
//                final Game g = pd.getGame();
//                for (Entity e : p.getNearbyEntities(120, 50, 120)) {
//                    if (e instanceof Player) {
//                        if (!g.getGamePlayerData().getPlayers().contains(e.getUniqueId())) continue;
//                        im.setDisplayName(tsn + (uses - 1));
//                        Location l = e.getLocation();
//                        int range = (int) p.getLocation().distance(l);
//                        Util.sendMessage(p, lang.track_nearest
//                            .replace("<player>", e.getName())
//                            .replace("<range>", String.valueOf(range))
//                            .replace("<location>", getDirection(p.getLocation().getBlock(), l.getBlock())));
//                        i.setItemMeta(im);
//                        p.updateInventory();
//                        return;
//                    }
//                }
//                Util.sendMessage(p, lang.track_no_near);
//            }
//        }
    }

    private String getDirection(Block block, Block block1) {
        Vector bv = block.getLocation().toVector();
        Vector bv2 = block1.getLocation().toVector();
        float y = (float) angle(bv.getX(), bv.getZ(), bv2.getX(), bv2.getZ());
        float cal = (y * 10);
        int c = (int) cal;
        if (c <= 1 && c >= -1) {
            return "South";
        } else if (c > -14 && c < -1) {
            return "SouthWest";
        } else if (c >= -17 && c <= -14) {
            return "West";
        } else if (c > -29 && c < -17) {
            return "NorthWest";
        } else if (c > 17 && c < 29) {
            return "NorthEast";
        } else if (c <= 17 && c >= 14) {
            return "East";
        } else if (c > 1 && c < 14) {
            return "SouthEast";
        } else if (c <= 29 && c >= -29) {
            return "North";
        } else {
            return "UnKnown";
        }
    }

    private double angle(double d, double e, double f, double g) {
        //Vector differences
        int x = (int) (f - d);
        int z = (int) (g - e);

        return Math.atan2(x, z);
    }

}
