package me.minebuilders.hg.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class FreeRoamTask implements Runnable {

	private Game game;
	private int id;

	public FreeRoamTask(Game g) {
		this.game = g;
		for (UUID u : g.getPlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				Util.scm(p,"&4[]---------[ &6&lThe game has started! &4]---------[]"); 
				Util.scm(p," &e You have " + g.getRoamTime() + " seconds to roam without taking damage!"); 
				p.setHealth(20);
				p.setFoodLevel(20);
				g.unFreeze(p);
			}
		}
		this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(HG.plugin, this, g.getRoamTime() * 20L);
	}

	@Override
	public void run() {
		game.msgAll("&c&lFree-Roam is over, PVP is now enabled!");
		game.startGame();
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
