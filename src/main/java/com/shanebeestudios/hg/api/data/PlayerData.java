package com.shanebeestudios.hg.api.data;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameTeam;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

/**
 * Player data object for holding pre-game player info
 */
@SuppressWarnings("WeakerAccess")
public class PlayerData implements Cloneable {

    private static final Scoreboard DUMMY = Bukkit.getScoreboardManager().getNewScoreboard();

    private final Game game;
    private final Player player;
    private final UUID uuid;

    //Pregame data
    private ItemStack[] inv;
    private ItemStack[] equip;
    private int expL;
    private float expP;
    private double health;
    private int food;
    private float saturation;
    private GameMode mode;
    private Scoreboard scoreboard;
    private Location previousLocation = null;
    private boolean online;
    private boolean hasGameStarted;

    //InGame data
    private GameTeam gameTeam;
    private GameTeam pendingGameTeam;

    /**
     * New player pre-game data file
     *
     * @param player Player to save
     * @param game   Game they will be entering
     */
    public PlayerData(Player player, Game game) {
        this.game = game;
        this.player = player;
        this.uuid = player.getUniqueId();
        this.online = true;
    }

    public void backup() {
        this.hasGameStarted = true;
        this.inv = this.player.getInventory().getStorageContents();
        this.equip = this.player.getInventory().getArmorContents();
        this.expL = this.player.getLevel();
        this.expP = this.player.getExp();
        this.mode = this.player.getGameMode();
        this.food = this.player.getFoodLevel();
        this.saturation = this.player.getSaturation();
        this.health = this.player.getHealth();
        Util.clearInv(this.player);
        this.player.setLevel(0);
        this.player.setExp(0);
        this.scoreboard = this.player.getScoreboard();
    }

    /**
     * Restore a player's saved data
     *
     * @param player Player to restore data to
     */
    public void restore(Player player) {
        if (player == null || !this.hasGameStarted) return;
        Util.clearInv(player);
        player.setLevel(this.expL);
        player.setExp(this.expP);
        player.setFoodLevel(this.food);
        player.setSaturation(this.saturation);
        player.getInventory().setStorageContents(this.inv);
        player.getInventory().setArmorContents(this.equip);
        player.setGameMode(this.mode);
        player.updateInventory();
        player.setInvulnerable(false);
        restoreHealth(player);
        player.setWorldBorder(player.getWorld().getWorldBorder());
        // Force back their original scoreboard
        player.setScoreboard(DUMMY);
        player.setScoreboard(this.scoreboard);
    }

    // Restores later if player has an item in their inventory which changes their max health value
    @SuppressWarnings("ConstantConditions")
    private void restoreHealth(Player player) {
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (this.health > maxHealth) {
            Bukkit.getScheduler().runTaskLater(HungerGames.getPlugin(), () -> player.setHealth(this.health), 10);
        } else {
            player.setHealth(this.health);
        }
    }

    /**
     * Check if a player is on a team
     *
     * @param player Player to check
     * @return True if player is on a team
     */
    public boolean isOnTeam(Player player) {
        return (this.gameTeam != null && this.gameTeam.isOnTeam(player));
    }

    /**
     * Get the game of this player data
     *
     * @return The game of this player data
     */
    public Game getGame() {
        return game;
    }

    /**
     * Get the team of this player data
     *
     * @return The team
     */
    public GameTeam getTeam() {
        return gameTeam;
    }

    /**
     * Set the team of this player data
     *
     * @param gameTeam The team to set
     */
    public void setTeam(GameTeam gameTeam) {
        this.gameTeam = gameTeam;
    }

    /**
     * Get the pending team of this player data
     *
     * @return Pending team of this player data
     */
    public GameTeam getPendingTeam() {
        return pendingGameTeam;
    }

    /**
     * Set the pending team of this player data
     *
     * @param pendingGameTeam Team for pending
     */
    public void setPendingTeam(GameTeam pendingGameTeam) {
        this.pendingGameTeam = pendingGameTeam;
    }

    /**
     * Get the gamemode of this player data
     *
     * @return Gamemode of this player data
     */
    public GameMode getGameMode() {
        return this.mode;
    }

    /**
     * Get the UUID belonging to this player data
     *
     * @return UUID belonging to this player data
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Set the previous location of the player
     *
     * @param previousLocation Location player was at before entering arena
     */
    public void setPreviousLocation(Location previousLocation) {
        this.previousLocation = previousLocation;
    }

    /**
     * Get the previous location of the player
     *
     * @return Location player was at before entering arena
     */
    @Nullable
    public Location getPreviousLocation() {
        if (previousLocation != null) {
            return previousLocation.clone();
        } else {
            return null;
        }
    }

    public boolean isOnline() {
        return online;
    }

    public boolean hasGameStared() {
        return this.hasGameStarted;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
            "inv=" + Arrays.toString(inv) +
            ", equip=" + Arrays.toString(equip) +
            ", expLevel=" + expL +
            ", expPoints=" + expP +
            ", health=" + health +
            ", food=" + food +
            ", saturation=" + saturation +
            ", mode=" + mode +
            ", uuid=" + uuid +
            ", team=" + gameTeam +
            ", pending=" + pendingGameTeam +
            ", game=" + game +
            '}';
    }

    @Override
    public PlayerData clone() {
        try {
            return (PlayerData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
