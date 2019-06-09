package tk.shanebee.hg;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerData {

	//Pregame data
	private ItemStack[] inv;
	private ItemStack[] equip;
	private int expL;
	private float expP;
	private GameMode mode;
	
	//Ingame data
	private Team team;
	private Game game;

	/** New player pre-game data file
	 * @param player Player to save
	 * @param game Game they will be entering
	 */
	PlayerData(Player player, Game game) {
		this.game = game;
		inv = player.getInventory().getContents();
		equip = player.getInventory().getArmorContents();
		expL = player.getLevel();
		expP = player.getExp();
		mode = player.getGameMode();
		Util.clearInv(player);
		player.setLevel(0);
		player.setExp(0);
	}

	void restore(Player p) {
		if (p == null) return;
		Util.clearInv(p);
		p.setWalkSpeed(0.2f);
		p.setLevel(expL);
		p.setExp(expP);
		p.getInventory().setContents(inv);
		p.getInventory().setArmorContents(equip);
		p.setGameMode(mode);
		p.updateInventory();
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
}
