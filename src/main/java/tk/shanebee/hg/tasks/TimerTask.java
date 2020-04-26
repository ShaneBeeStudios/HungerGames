package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;

public class TimerTask implements Runnable {

	private int remainingTime;
	private final int teleportTimer;
	private final int borderCountdownStart;
	private final int borderCountdownEnd;
	private final int id;
	private final Game game;

	public TimerTask(Game g, int time) {
        this.remainingTime = time;
        this.game = g;
        this.teleportTimer = Config.teleportEndTime;
        this.borderCountdownStart = g.getBorderTimer().get(0);
        this.borderCountdownEnd = g.getBorderTimer().get(1);
        g.getPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setInvulnerable(false);
            }
        });
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, 0, 30 * 20L);
    }
	
	@Override
	public void run() {
		if (game == null || game.getStatus() != Status.RUNNING) {
		    stop(); //A quick null check!
            return;
        }

		if (Config.bossbar) game.bossbarUpdate(remainingTime);

		if (Config.borderEnabled && remainingTime == borderCountdownStart) {
			int closingIn = remainingTime - borderCountdownEnd;
			game.setBorder(closingIn);
			game.msgAllInGame(HG.getPlugin().getLang().game_border_closing.replace("<seconds>", String.valueOf(closingIn)));
		}

		if (game.getChestRefillTime() > 0 && remainingTime == game.getChestRefillTime()) {
			game.refillChests();
			game.msgAllInGame(HG.getPlugin().getLang().game_chest_refill);
		}

		if (remainingTime == teleportTimer && Config.teleportEnd) {
			game.msgAllInGame(HG.getPlugin().getLang().game_almost_over);
			game.respawnAll();
		} else if (this.remainingTime < 10) {
			stop();
			game.stop(false);
		} else {
			if (!Config.bossbar) {
				int minutes = this.remainingTime / 60;
				int asd = this.remainingTime % 60;
				if (minutes != 0) {
					if (asd == 0)
						game.msgAllInGame(HG.getPlugin().getLang().game_ending_min.replace("<minutes>", String.valueOf(minutes)));
					else

						game.msgAllInGame(HG.getPlugin().getLang().game_ending_minsec.replace("<minutes>", String.valueOf(minutes)).replace("<seconds>", String.valueOf(asd)));
				} else game.msgAllInGame(HG.getPlugin().getLang().game_ending_sec.replace("<seconds>", String.valueOf(this.remainingTime)));
			}
		}
		remainingTime = (remainingTime - 30);
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}

}
