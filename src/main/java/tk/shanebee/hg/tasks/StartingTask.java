package tk.shanebee.hg.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

public class StartingTask extends BukkitRunnable {

	private int timer;
	private Game game;
	private Language lang;

	public StartingTask(Game g) {
		this.timer = 15;
		this.game = g;
		this.lang = HG.getPlugin().getLang();
		Util.broadcast(lang.game_started.replace("<arena>", g.getName()));
		Util.broadcast(lang.game_join.replace("<arena>", g.getName()));

		this.runTaskTimer(HG.getPlugin(), 5 * 20L, 5 * 20L);
	}

	@Override
	public void run() {
	    if (timer > 0) {
            game.msgAll(lang.game_starting
                    .replace("<arena>", game.getName())
                    .replace("<timer>", String.valueOf(timer)));
        } else {
	        this.game.startCountdown(false);
            this.cancel();
        }
        timer -= 5;
	}

}
