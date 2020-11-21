package tk.shanebee.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.game.Team;
import tk.shanebee.hg.util.Util;

import java.util.Arrays;
import java.util.UUID;

/**
 * Player data object for holding pre-game player info
 */
@SuppressWarnings("WeakerAccess")
public class PlayerData implements Cloneable {

    //Pregame data
    private final ItemStack[] inv;
    private final ItemStack[] equip;
    private final int expL;
    private final float expP;
    private final double health;
    private final int food;
    private final float saturation;
    private final GameMode mode;
    private final UUID uuid;
    private final Scoreboard scoreboard;
    private boolean rdy = false;

    //InGame data
    private Team team;
    private Team pendingTeam;
    private final Game game;

    /**
     * New player pre-game data file
     *  @param player Player to save
     * @param game   Game they will be entering
     * @param oldLoc
     */
    public PlayerData(Player player, Game game) {
        this.game = game;
        this.uuid = player.getUniqueId();
        inv = player.getInventory().getStorageContents();
        equip = player.getInventory().getArmorContents();
        expL = player.getLevel();
        expP = player.getExp();
        mode = player.getGameMode();
        food = player.getFoodLevel();
        saturation = player.getSaturation();
        health = player.getHealth();
        Util.clearInv(player);
        player.setLevel(0);
        player.setExp(0);
        scoreboard = player.getScoreboard();
    }

    /**
     * Restore a player's saved data
     *
     * @param player Player to restore data to
     */
    public void restore(Player player) {
        if (player == null) return;
        Util.clearInv(player);
        player.setWalkSpeed(0.2f);
        player.setLevel(expL);
        player.setExp(expP);
        player.setFoodLevel(food);
        player.setSaturation(saturation);
        player.getInventory().setStorageContents(inv);
        player.getInventory().setArmorContents(equip);
        player.setGameMode(mode);
        player.updateInventory();
        player.setInvulnerable(false);
        restoreHealth(player);
        player.setScoreboard(scoreboard);
    }

    // Restores later if player has an item in their inventory which changes their max health value
    @SuppressWarnings("ConstantConditions")
    private void restoreHealth(Player player) {
        double att = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (health > att) {
            Bukkit.getScheduler().runTaskLater(HG.getPlugin(), () -> player.setHealth(health), 10);
        } else {
            player.setHealth(health);
        }
    }

    /**
     * Check if a player is on a team
     *
     * @param uuid Uuid of player to check
     * @return True if player is on a team
     */
    public boolean isOnTeam(UUID uuid) {
        return (team != null && team.isOnTeam(uuid));
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
    public Team getTeam() {
        return team;
    }

    /**
     * Set the team of this player data
     *
     * @param team The team to set
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Get the pending team of this player data
     *
     * @return Pending team of this player data
     */
    public Team getPendingTeam() {
        return pendingTeam;
    }

    /**
     * Set the pending team of this player data
     *
     * @param pendingTeam Team for pending
     */
    public void setPendingTeam(Team pendingTeam) {
        this.pendingTeam = pendingTeam;
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
     * Get rdy status of this player data
     *
     * @return rdy status of this player data
     */
    public boolean getRdy() {
        return this.rdy;
    }

    /**
     * Set rdy status of this player data
     *
     * @param r rdy status of this player data
     */
    public void setRdy(boolean r) {
        this.rdy = r;
    }

    /**
     * Get the {@link Player Bukkit Player} belonging to this player data
     *
     * @return Player belonging to this player data
     */
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(this.uuid);
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
                ", team=" + team +
                ", pending=" + pendingTeam +
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
