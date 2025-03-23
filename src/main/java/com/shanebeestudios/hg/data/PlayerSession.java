package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class PlayerSession {

    private enum Stage {
        CORNER_1,
        CORNER_2,
        SPAWN_LOCATIONS,
        SIGN;
    }

    private Stage stage = null;
    private final String name;
    private Block corner1;
    private Block corner2;
    private final int time;
    private final int minPlayers;
    private final int maxPlayers;
    private final int cost;
    private final List<Location> spawnLocations = new ArrayList<>();
    private Sign signLocation;

    public PlayerSession(String name, int time, int minPlayers, int maxPlayers, int cost) {
        this.name = name;
        this.time = time;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.cost = cost;
    }

    public void start(Player player) {
        this.stage = Stage.CORNER_1;
        Util.sendPrefixedMessage(player, "Click your first corner");
    }

    public void click(Player player, Block block) {
        if (this.stage == Stage.CORNER_1) {
            this.corner1 = block;
            this.stage = Stage.CORNER_2;
            // TODO message set sign 2
            Util.sendPrefixedMessage(player, "First corner selected, now select second corner.");
        } else if (this.stage == Stage.CORNER_2) {
            this.corner2 = block;
            if (isBigEnough()) {
                this.stage = Stage.SPAWN_LOCATIONS;
                // TODO message start spawn locations
                Util.sendPrefixedMessage(player, "Second corner selected, now start selecting spawn locations.");
            } else {
                // TODO message not big enough
                Util.sendPrefixedMessage(player, "Too small, arena must be at least 5x5x5, please re-select second corner.");
            }
        } else if (this.stage == Stage.SPAWN_LOCATIONS) {
            if (this.spawnLocations.size() >= this.maxPlayers) {
                this.stage = Stage.SIGN;
                // TODO message
                Util.sendPrefixedMessage(player, "Congrats all locations set, now select your sign!");
            } else {
                double height = block.getBoundingBox().getHeight();
                Location location = block.getLocation().add(0.5, height, 0.5);
                location.setYaw(player.getLocation().getYaw());
                this.spawnLocations.add(location);
                // TODO count/next location
                if (this.spawnLocations.size() >= this.maxPlayers) {
                    this.stage = Stage.SIGN;
                    // TODO message
                    Util.sendPrefixedMessage(player, "Congrats all locations set, now select your sign!");
                } else {
                    int left = this.maxPlayers - this.spawnLocations.size();
                    Util.sendPrefixedMessage(player, "Selected %s, only %s more to go.", this.spawnLocations.size(), left);
                }
            }
        } else if (this.stage == Stage.SIGN) {
            // TODO handle sign
            if (block.getState() instanceof Sign sign) {
                this.signLocation = sign;
                // TODO all done message
                Util.sendPrefixedMessage(player, "Sign selected, you're dont, good job you!!");
                finalizeGame();
            } else {
                // TODO not a sign message
                Util.sendPrefixedMessage(player, "That's not a sign silly!");
            }
        }
    }

    public void finalizeGame() {
        HungerGames.getPlugin().getGameManager().createGame(this.name,
            this.corner1, this.corner2, this.spawnLocations,
            this.signLocation, this.time, this.minPlayers, this.maxPlayers, this.cost);
    }

    public boolean hasValidSelection() {
        return this.corner1 != null && this.corner2 != null;
    }

    public boolean isBigEnough() {
        if (this.corner1 == null || this.corner2 == null) return false;
        BoundingBox boundingBox = BoundingBox.of(this.corner1, this.corner2);
        return boundingBox.getWidthX() > 5 && boundingBox.getWidthZ() > 5 && boundingBox.getHeight() > 5;
    }

}
