package tk.shanebee.hg.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

public class FreeRoamTask extends BukkitRunnable {

	private Game game;

	public FreeRoamTask(Game g) {
		this.game = g;
		for (UUID u : g.getPlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				Util.scm(p, HG.getPlugin().getLang().roam_game_started);
				Util.scm(p, HG.getPlugin().getLang().roam_time.replace("<roam>", String.valueOf(g.getRoamTime())));
				p.setHealth(20);
				p.setFoodLevel(20);
				g.unFreeze(p);
			}
		}
		this.runTaskLater(HG.getPlugin(), g.getRoamTime() * 20L);
	}

	@Override
	public void run() {
		game.msgAllInGame(HG.getPlugin().getLang().roam_finished);
		game.startGame();
	}

}
