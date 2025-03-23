package com.shanebeestudios.hg.tasks;

import com.shanebeestudios.hg.game.GameBlockData;
import com.shanebeestudios.hg.game.GameBorderData;
import com.shanebeestudios.hg.game.GamePlayerData;
import org.bukkit.Bukkit;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.game.GameArenaData;

public class TimerTask implements Runnable {

	private int timer = 0;
	private int remainingtime;
	private final int teleportTimer;
	private final int borderCountdownStart;
	private final int borderCountdownEnd;
	private final int taskId;
	private final Game game;
	private final Language lang;
    private final String end_min;
    private final String end_minsec;
    private final String end_sec;

	public TimerTask(Game game, int time) {
		this.remainingtime = time;
		this.game = game;
		HungerGames plugin = this.game.getGameArenaData().getPlugin();
		this.lang = plugin.getLang();
		this.teleportTimer = Config.teleportEndTime;
		this.borderCountdownStart = game.getGameBorderData().getBorderTimer().get(0);
		this.borderCountdownEnd = game.getGameBorderData().getBorderTimer().get(1);
		game.getGamePlayerData().getPlayers().forEach(player -> player.setInvulnerable(false));

		this.end_min = lang.game_ending_min;
		this.end_minsec = lang.game_ending_minsec;
		this.end_sec = lang.game_ending_sec;

		this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 30 * 20L);
	}

	@Override
	public void run() {
		GameArenaData gameArenaData = this.game.getGameArenaData();
        GameBorderData gameBorderData = this.game.getGameBorderData();
        GameBlockData gameBlockData = this.game.getGameBlockData();
        GamePlayerData gamePlayerData = this.game.getGamePlayerData();
        if (gameArenaData.getStatus() != Status.RUNNING) stop(); //A quick null check!


		if (Config.bossbar) game.getGameBarData().bossbarUpdate(remainingtime);

		if (Config.borderEnabled && remainingtime == borderCountdownStart) {
			int closingIn = remainingtime - borderCountdownEnd;
			gameBorderData.setBorder(closingIn);
			gamePlayerData.msgAll(lang.game_border_closing.replace("<seconds>", String.valueOf(closingIn)));
		}

		if (gameArenaData.getChestRefillTime() > 0 && remainingtime == gameArenaData.getChestRefillTime()) {
			gameBlockData.refillChests();
			gamePlayerData.msgAll(lang.game_chest_refill);
		}

		int refillRepeat = gameArenaData.getChestRefillRepeat();
		if (refillRepeat > 0 && this.timer % refillRepeat == 0) {
			gameBlockData.refillChests();
			gamePlayerData.msgAll(this.lang.game_chest_refill);
		}

		if (this.remainingtime == this.teleportTimer && Config.teleportEnd) {
			gamePlayerData.msgAll(lang.game_almost_over);
			gamePlayerData.respawnAll();
		} else if (this.remainingtime < 10) {
			stop();
			this.game.stop(false);
		} else {
			if (!Config.bossbar) {
				int minutes = this.remainingtime / 60;
				int asd = this.remainingtime % 60;
				if (minutes != 0) {
					if (asd == 0) {
					    if (this.end_min.isEmpty()) return;
                        gamePlayerData.msgAll(this.end_min.replace("<minutes>", "" + minutes));
                    } else {
					    if (this.end_minsec.isEmpty()) return;
                        gamePlayerData.msgAll(this.end_minsec.replace("<minutes>", "" + minutes).replace("<seconds>", "" + asd));
                    }
				} else {
				    if (this.end_sec.isEmpty()) return;
				    gamePlayerData.msgAll(this.end_sec.replace("<seconds>", "" + this.remainingtime));
                }
			}
		}
		this.remainingtime = (this.remainingtime - 30);
		this.timer += 30;
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(this.taskId);
	}

}
