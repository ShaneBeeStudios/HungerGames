package tk.shanebee.hg.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

public class FreeRoamTask implements Runnable {

	private Game game;
	private int id;

	public FreeRoamTask(Game g) {
		this.game = g;
		for (UUID u : g.getPlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				Util.scm(p,HG.plugin.lang.roam_game_started);
				Util.scm(p,HG.plugin.lang.roam_time.replace("<roam>", String.valueOf(g.getRoamTime())));
				p.setHealth(20);
				p.setFoodLevel(20);
				g.unFreeze(p);
			}
		}
		this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(HG.plugin, this, g.getRoamTime() * 20L);
	}

	@Override
	public void run() {
		game.msgAll(HG.plugin.lang.roam_finished);
		game.startGame();
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
