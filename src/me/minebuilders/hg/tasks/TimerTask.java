package me.minebuilders.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;

public class TimerTask implements Runnable {

	private int remainingtime;
	private int id;
	private Game game;

	public TimerTask(Game g, int time) {
		this.remainingtime = time;
		this.game = g;
		
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, 30 * 20L, 30 * 20L);
	}
	
	@Override
	public void run() {
		if (game == null || game.getStatus() != Status.RUNNING) stop(); //A quick null check!
		
		remainingtime = (remainingtime - 30);

		if (remainingtime == 30 && HG.plugin.getConfig().getBoolean("settings.teleport-at-end")) {
			game.msgAll(HG.lang.game_almost_over);
			game.respawnAll();
		} else if (this.remainingtime < 10) {
			stop();
			game.stop();
		} else {
			int minutes = this.remainingtime / 60;
			int asd = this.remainingtime % 60;
			if (minutes != 0) {
				if (asd == 0)
					game.msgAll(HG.lang.game_ending_minsec.replace("<minutes>", String.valueOf(minutes)).replace("<seconds>", String.valueOf(asd)));
				else
					game.msgAll(HG.lang.game_ending_min.replace("<minutes>", String.valueOf(minutes)));
			}
			else game.msgAll(HG.lang.game_ending_sec.replace("<seconds>", String.valueOf(this.remainingtime)));
		}
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
