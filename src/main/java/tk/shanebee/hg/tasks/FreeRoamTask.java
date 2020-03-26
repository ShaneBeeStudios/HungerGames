package tk.shanebee.hg.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

public class FreeRoamTask implements Runnable {

	private final Game game;
	private final int id;

	public FreeRoamTask(Game game) {
		this.game = game;
		for (UUID uuid : game.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				Util.scm(player, HG.getPlugin().getLang().roam_game_started);
				Util.scm(player, HG.getPlugin().getLang().roam_time.replace("<roam>", String.valueOf(game.getRoamTime())));
				player.setHealth(20);
				player.setFoodLevel(20);
				game.unFreeze(player);
			}
		}
		this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(HG.getPlugin(), this, game.getRoamTime() * 20L);
	}

	@Override
	public void run() {
		game.msgAll(HG.getPlugin().getLang().roam_finished);
		game.startGame();
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
