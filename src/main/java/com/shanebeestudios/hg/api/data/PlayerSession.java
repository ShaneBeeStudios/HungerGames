package com.shanebeestudios.hg.api.data;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class PlayerSession {

    private enum Stage {
        CORNER_1,
        CORNER_2,
        SPAWN_LOCATIONS,
        SIGN
    }

    private final Language lang = HungerGames.getPlugin().getLang();
    private Stage stage = null;
    private final String name;
    private Block corner1;
    private Block corner2;
    private final int time;
    private final int minPlayers;
    private final int maxPlayers;
    private final int cost;
    private final List<Location> spawnLocations = new ArrayList<>();
    private Location signLocation;

    public PlayerSession(String name, int time, int minPlayers, int maxPlayers, int cost) {
        this.name = name;
        this.time = time;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.cost = cost;
    }

    @SuppressWarnings("UnstableApiUsage")
    public void start(Player player) {
        this.stage = Stage.CORNER_1;
        ItemStack itemStack = ItemType.STICK.createItemStack();
        itemStack.setData(DataComponentTypes.ITEM_NAME, Util.getMini(this.lang.command_create_session_stick_name));
        player.getWorld().dropItem(player.getLocation(), itemStack);
        Util.sendPrefixedMessage(player, this.lang.command_create_session_start);
    }

    public void click(Player player, Block block) {
        if (this.stage == Stage.CORNER_1) {
            this.corner1 = block;
            this.stage = Stage.CORNER_2;
            Util.sendPrefixedMessage(player, this.lang.command_create_session_next_corner);
        } else if (this.stage == Stage.CORNER_2) {
            this.corner2 = block;
            if (isTooSmall()) {
                Util.sendPrefixedMessage(player, this.lang.command_create_session_error_too_small);
            } else if (isOverlapping()) {
                Util.sendPrefixedMessage(player, this.lang.command_create_session_error_overlap);
            } else {
                this.stage = Stage.SPAWN_LOCATIONS;
                Util.sendPrefixedMessage(player, this.lang.command_create_session_select_spawns);
            }
        } else if (this.stage == Stage.SPAWN_LOCATIONS) {
            if (this.spawnLocations.size() >= this.maxPlayers) {
                this.stage = Stage.SIGN;
                Util.sendPrefixedMessage(player, this.lang.command_create_session_select_sign);
            } else {
                double height = block.getBoundingBox().getHeight();
                Location location = block.getLocation().add(0.5, height, 0.5);
                location.setYaw(player.getLocation().getYaw());
                this.spawnLocations.add(location);
                if (this.spawnLocations.size() >= this.maxPlayers) {
                    this.stage = Stage.SIGN;
                    Util.sendPrefixedMessage(player, this.lang.command_create_session_select_sign);
                } else {
                    int left = this.maxPlayers - this.spawnLocations.size();
                    Util.sendPrefixedMessage(player, this.lang.command_create_session_select_spawns_next
                        .replace("<selected>", String.valueOf(this.spawnLocations.size()))
                        .replace("<left>", String.valueOf(left)));
                }
            }
        } else if (this.stage == Stage.SIGN) {
            if (block.getState(false) instanceof Sign sign) {
                this.signLocation = sign.getLocation();
                Util.sendPrefixedMessage(player, this.lang.command_create_session_done);
                finalizeGame(player);
            } else {
                Util.sendPrefixedMessage(player, this.lang.command_create_session_sign_invalid);
            }
        }
    }

    private void finalizeGame(Player player) {
        HungerGames plugin = HungerGames.getPlugin();
        plugin.getGameManager().createGame(this.name,
            this.corner1, this.corner2, this.spawnLocations,
            this.signLocation, this.time, this.minPlayers, this.maxPlayers, this.cost);
        plugin.getSessionManager().endPlayerSession(player);
    }

    private boolean isTooSmall() {
        if (this.corner1 == null || this.corner2 == null) return true;
        BoundingBox boundingBox = BoundingBox.of(this.corner1, this.corner2);
        return boundingBox.getWidthX() <= 5 || boundingBox.getWidthZ() <= 5 || boundingBox.getHeight() <= 5;
    }

    private boolean isOverlapping() {
        if (this.corner1 == null || this.corner2 == null) return true;
        BoundingBox boundingBox = BoundingBox.of(this.corner1, this.corner2);
        for (Game game : HungerGames.getPlugin().getGameManager().getGames()) {
            BoundingBox gameBox = game.getGameArenaData().getGameRegion().getBoundingBox();
            if (boundingBox.overlaps(gameBox)) return true;
        }
        return false;
    }

}
