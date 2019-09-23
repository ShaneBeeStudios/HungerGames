package tk.shanebee.hg;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tk.shanebee.hg.util.Util;

import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class PlayerData {

	//Pregame data
	private ItemStack[] inv;
	private ItemStack[] equip;
	private int expL;
	private float expP;
	private double health;
	private int food;
	private float saturation;
	private GameMode mode;
	
	//InGame data
	private Team team;
	private Game game;

	/** New player pre-game data file
	 * @param player Player to save
	 * @param game Game they will be entering
	 */
	public PlayerData(Player player, Game game) {
		this.game = game;
		inv = player.getInventory().getContents();
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
	}

	/** Restore a player's saved data
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
		player.getInventory().setContents(inv);
		player.getInventory().setArmorContents(equip);
		player.setGameMode(mode);
		player.updateInventory();
		player.setInvulnerable(false);
		restoreHealth(player);
	}

	// Restores later if player has an item in their inventory which changes their max health value
	private void restoreHealth(Player player) {
		double att = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		if (health > att) {
			Bukkit.getScheduler().runTaskLater(HG.getPlugin(), () -> player.setHealth(health), 10);
		} else {
			player.setHealth(health);
		}
	}

	/** Check if a player is on a team
	 * @param uuid Uuid of player to check
	 * @return True if player is on a team
	 */
	public boolean isOnTeam(UUID uuid) {
		return (team != null && team.isOnTeam(uuid));
	}

	/** Get the game of this player data
	 * @return The game of this player data
	 */
	public Game getGame() {
		return game;
	}

	/** Get the team of this player data
	 * @return The team
	 */
	public Team getTeam() {
		return team;
	}

	/** Set the team of this player data
	 * @param team The team to set
	 */
	public void setTeam(Team team) {
		this.team = team;
	}

	/** Get the gamemode of this player data
	 * @return Gamemode of this player data
	 */
	public GameMode getGameMode() {
		return this.mode;
	}

}
