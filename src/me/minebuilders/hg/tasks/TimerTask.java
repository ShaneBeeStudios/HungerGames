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
			game.msgAll("&l&cThe game is almost over, fight to the death!");
			game.respawnAll();
		} else if (this.remainingtime < 10) {
			stop();
			game.stop();
		} else {
			int minutes = this.remainingtime / 60;
			int asd = Integer.valueOf(this.remainingtime % 60);
			if (minutes != 0) game.msgAll(ChatColor.GREEN+"The game is ending in " + minutes + (asd == 0?" minute(s)!":" minute(s), and " + asd+" seconds!"));
			else game.msgAll(ChatColor.GREEN+"The game is ending in " + this.remainingtime +" seconds!");
		}
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
