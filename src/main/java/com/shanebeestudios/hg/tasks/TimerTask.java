package com.shanebeestudios.hg.tasks;

import org.bukkit.Bukkit;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.game.GameArenaData;

import java.util.Objects;

public class TimerTask implements Runnable {

	private int timer = 0;
	private int remainingtime;
	private final int teleportTimer;
	private final int borderCountdownStart;
	private final int borderCountdownEnd;
	private final int id;
	private final Game game;
	private final Language lang;
    private final String end_min;
    private final String end_minsec;
    private final String end_sec;

	public TimerTask(Game g, int time) {
		this.remainingtime = time;
		this.game = g;
		HungerGames plugin = game.getGameArenaData().getPlugin();
		this.lang = plugin.getLang();
		this.teleportTimer = Config.teleportEndTime;
		this.borderCountdownStart = g.getGameBorderData().getBorderTimer().get(0);
		this.borderCountdownEnd = g.getGameBorderData().getBorderTimer().get(1);
		g.getGamePlayerData().getPlayers().forEach(uuid -> Objects.requireNonNull(Bukkit.getPlayer(uuid)).setInvulnerable(false));

		this.end_min = lang.game_ending_min;
		this.end_minsec = lang.game_ending_minsec;
		this.end_sec = lang.game_ending_sec;

		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 30 * 20L);
	}

	@Override
	public void run() {
		GameArenaData gameArenaData = game.getGameArenaData();
		if (game == null || gameArenaData.getStatus() != Status.RUNNING) stop(); //A quick null check!


		if (Config.bossbar) game.getGameBarData().bossbarUpdate(remainingtime);

		if (Config.borderEnabled && remainingtime == borderCountdownStart) {
			int closingIn = remainingtime - borderCountdownEnd;
			game.getGameBorderData().setBorder(closingIn);
			game.getGamePlayerData().msgAll(lang.game_border_closing.replace("<seconds>", String.valueOf(closingIn)));
		}

		if (gameArenaData.getChestRefillTime() > 0 && remainingtime == gameArenaData.getChestRefillTime()) {
			game.getGameBlockData().refillChests();
			game.getGamePlayerData().msgAll(lang.game_chest_refill);
		}

		int refillRepeat = gameArenaData.getChestRefillRepeat();
		if (refillRepeat > 0 && timer % refillRepeat == 0) {
			game.getGameBlockData().refillChests();
			game.getGamePlayerData().msgAll(lang.game_chest_refill);
		}

		if (remainingtime == teleportTimer && Config.teleportEnd) {
			game.getGamePlayerData().msgAll(lang.game_almost_over);
			game.getGamePlayerData().respawnAll();
		} else if (this.remainingtime < 10) {
			stop();
			game.stop(false);
		} else {
			if (!Config.bossbar) {
				int minutes = this.remainingtime / 60;
				int asd = this.remainingtime % 60;
				if (minutes != 0) {
					if (asd == 0) {
					    if (end_min.length() < 1) return;
                        game.getGamePlayerData().msgAll(end_min.replace("<minutes>", "" + minutes));
                    } else {
					    if (end_minsec.length() < 1) return;
                        game.getGamePlayerData().msgAll(end_minsec.replace("<minutes>", "" + minutes).replace("<seconds>", "" + asd));
                    }
				} else {
				    if (end_sec.length() < 1) return;
				    game.getGamePlayerData().msgAll(end_sec.replace("<seconds>", "" + this.remainingtime));
                }
			}
		}
		remainingtime = (remainingtime - 30);
		timer += 30;
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}

}
