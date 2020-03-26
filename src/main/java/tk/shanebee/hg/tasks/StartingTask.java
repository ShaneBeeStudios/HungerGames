package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

public class StartingTask implements Runnable {

	private int timer;
	private final int id;
	private final Game game;

	public StartingTask(Game game) {
		this.timer = 30;
		this.game = game;
		Util.broadcast(HG.getPlugin().getLang().game_started.replace("<arena>", game.getName()));
		Util.broadcast(HG.getPlugin().getLang().game_join.replace("<arena>", game.getName()));

		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, 5 * 20L, 5 * 20L);
	}

	@Override
	public void run() {
		timer = (timer - 5);

		if (timer <= 0) {
			stop();
			game.startFreeRoam();
		} else {
			game.msgAll(HG.getPlugin().getLang().game_countdown.replace("<timer>", String.valueOf(timer)));
		}
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
