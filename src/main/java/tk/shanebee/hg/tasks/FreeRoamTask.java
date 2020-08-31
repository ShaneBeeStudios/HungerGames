package tk.shanebee.hg.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

public class FreeRoamTask implements Runnable {

	private Game game;
	private int id;

	public FreeRoamTask(Game g) {
		this.game = g;
		for (UUID u : g.getGamePlayerData().getPlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				Util.scm(p, HG.getPlugin().getLang().roam_game_started);
				Util.scm(p, HG.getPlugin().getLang().roam_time.replace("<roam>", String.valueOf(g.getRoamTime())));
				p.setHealth(20);
				p.setFoodLevel(20);
				g.getGamePlayerData().unFreeze(p);
			}
		}
		this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(HG.getPlugin(), this, g.getRoamTime() * 20L);
	}

	@Override
	public void run() {
		game.getGamePlayerData().msgAll(HG.getPlugin().getLang().roam_finished);
		game.startGame();
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
