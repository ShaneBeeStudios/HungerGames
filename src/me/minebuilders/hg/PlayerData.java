package me.minebuilders.hg;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerData {

	//Pregame data
	private ItemStack[] inv;
	private ItemStack[] equip;
	private int exp;
	private GameMode mode;
	
	//Ingame data
	private Team team;
	private Game game;
	
	PlayerData(Player p, Game game) {
		this.game = game;
		inv = p.getInventory().getContents();
		equip = p.getInventory().getArmorContents();
		exp = (int) p.getExp();
		mode = p.getGameMode();
		Util.clearInv(p);
	}

	void restore(Player p) {
		Util.clearInv(p);
		p.setExp(0);
		p.setWalkSpeed(0.2f);
		p.giveExp(exp);
		p.getInventory().setContents(inv);
		p.getInventory().setArmorContents(equip);
		p.setGameMode(mode);
		p.updateInventory();
	}
	
	public boolean isOnTeam(UUID u) {
		return (team != null && team.isOnTeam(u));
	}

	public Game getGame() {
		return game;
	}

	public Team getTeam() {
		return team;
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
}
