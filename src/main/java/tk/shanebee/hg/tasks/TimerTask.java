package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;

import java.util.Objects;

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
		g.getPlayers().forEach(uuid -> Objects.requireNonNull(Bukkit.getPlayer(uuid)).setInvulnerable(false));
		
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, 0, 30 * 20L);
	}
	
	@Override
	public void run() {
		if (game == null || game.getStatus() != Status.RUNNING) stop(); //A quick null check!
		

		if (Config.bossbar) game.getGameBar().bossbarUpdate(remainingtime);

		if (Config.borderEnabled && remainingtime == borderCountdownStart) {
			int closingIn = remainingtime - borderCountdownEnd;
			game.setBorder(closingIn);
			game.msgAll(HG.getPlugin().getLang().game_border_closing.replace("<seconds>", String.valueOf(closingIn)));
		}

		if (game.getChestRefillTime() > 0 && remainingtime == game.getChestRefillTime()) {
			game.refillChests();
			game.msgAll(HG.getPlugin().getLang().game_chest_refill);
		}

		if (remainingtime == teleportTimer && Config.teleportEnd) {
			game.msgAll(HG.getPlugin().getLang().game_almost_over);
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
						game.msgAll(HG.getPlugin().getLang().game_ending_min.replace("<minutes>", String.valueOf(minutes)));
					else

						game.msgAll(HG.getPlugin().getLang().game_ending_minsec.replace("<minutes>", String.valueOf(minutes)).replace("<seconds>", String.valueOf(asd)));
				} else game.msgAll(HG.getPlugin().getLang().game_ending_sec.replace("<seconds>", String.valueOf(this.remainingtime)));
			}
		}
		remainingtime = (remainingtime - 30);
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}

}
