package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.ItemUtils;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.game.Game;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class GameTrackingStickListener extends GameListenerBase {

    public GameTrackingStickListener(HungerGames plugin) {
        super(plugin);
    }

    @EventHandler
    private void onClickWithStick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            if (this.playerManager.hasPlayerData(player) && ItemUtils.isTrackingStick(item)) {
                event.setCancelled(true);
                useTrackStick(player, item);
            }
        }
    }

    // UTIL
    private void useTrackStick(Player player, ItemStack itemStack) {
        PlayerData playerData = this.playerManager.getPlayerData(player);
        assert playerData != null;
        final Game game = playerData.getGame();
        BoundingBox box = game.getGameArenaData().getGameRegion().getBoundingBox();

        int distance = (int) Math.min(120, Math.max(box.getWidthX() / 2, box.getWidthZ() / 2));
        for (Entity nearbyEntity : player.getNearbyEntities(distance, 50, distance)) {
            if (nearbyEntity instanceof Player nearbyPlayer) {
                if (!game.getGamePlayerData().getPlayers().contains(nearbyPlayer)) continue;

                Location location = nearbyEntity.getLocation();
                int range = (int) player.getLocation().distance(location);
                Util.sendMessage(player, this.lang.tracking_stick_nearest
                    .replace("<player>", nearbyEntity.getName())
                    .replace("<range>", "" + range)
                    .replace("<location>", getDirection(player.getLocation().getBlock(), location.getBlock())));
                itemStack.damage(1, player);
                player.updateInventory();
                return;
            }
        }
        Util.sendMessage(player, this.lang.tracking_stick_no_near);
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
