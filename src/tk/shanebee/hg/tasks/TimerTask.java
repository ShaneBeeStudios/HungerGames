package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;
import tk.shanebee.hg.Config;
import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;

public class TimerTask implements Runnable {

	private int remainingtime;
	private int teleportTimer;
	private int borderCountdownStart;
	private int borderCountdownEnd;
	private int id;
	private Game game;

	public TimerTask(Game g, int time) {
		this.remainingtime = time;
		this.game = g;
		this.teleportTimer = Config.teleportEndTime;
		this.borderCountdownStart = g.getBorderTimer().get(0);
		this.borderCountdownEnd = g.getBorderTimer().get(1);
		
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, 30 * 20L, 30 * 20L);
	}
	
	@Override
	public void run() {
		if (game == null || game.getStatus() != Status.RUNNING) stop(); //A quick null check!
		
		remainingtime = (remainingtime - 30);
		if (Config.bossbar) game.bossbarUpdate(remainingtime);

		if (Config.borderEnabled && !Config.borderOnStart && remainingtime == borderCountdownStart) {
			int closingIn = remainingtime - borderCountdownEnd;
			game.setBorder(closingIn);
			game.msgAll(HG.plugin.lang.game_border_closing.replace("<seconds>", String.valueOf(closingIn)));
		}

		if (game.getChestRefillTime() > 0 && remainingtime == game.getChestRefillTime()) {
			game.refillChests();
			game.msgAll(HG.plugin.lang.game_chest_refill);
		}

		if (remainingtime == teleportTimer && Config.teleportEnd) {
			game.msgAll(HG.plugin.lang.game_almost_over);
			game.respawnAll();
		} else if (this.remainingtime < 10) {
			stop();
			game.stop(false);
		} else {
			if (!Config.bossbar) {
				int minutes = this.remainingtime / 60;
				int asd = this.remainingtime % 60;
				if (minutes != 0) {
					if (asd == 0)
						game.msgAll(HG.plugin.lang.game_ending_min.replace("<minutes>", String.valueOf(minutes)));
					else

						game.msgAll(HG.plugin.lang.game_ending_minsec.replace("<minutes>", String.valueOf(minutes)).replace("<seconds>", String.valueOf(asd)));
				} else game.msgAll(HG.plugin.lang.game_ending_sec.replace("<seconds>", String.valueOf(this.remainingtime)));
			}
		}
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}

}
