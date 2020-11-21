package tk.shanebee.hg.tasks;

import org.bukkit.Bukkit;

import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.managers.PlayerManager;
import tk.shanebee.hg.util.Util;

public class StartingTask implements Runnable {

	private int timer;
	private int id;
	private Game game;
	private PlayerManager playerManager;
	private int oldRdyPlayerCount = 0;

	public StartingTask(Game g) {
		this.timer = Config.gameStart;
		this.game = g;
		String name = g.getGameArenaData().getName();
		Util.broadcast(HG.getPlugin().getLang().game_started
				.replace("<arena>", name)
				.replace("<seconds>", "" + timer));
		Util.broadcast(HG.getPlugin().getLang().game_join.replace("<arena>", name));
		this.playerManager = HG.getPlugin().getPlayerManager();
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, 5 * 20L, 5 * 20L);
	}

	@Override
	public void run() {
		Integer rdy = this.playerManager.getRdyPlayerCount();

		timer = (timer - 5);
		if (timer <= 0 || this.playerManager.areAllPlayersRdy()) {
			stop();
			game.startFreeRoam();
		} else if(timer < 30 || this.oldRdyPlayerCount != rdy || (timer + 5) == Config.gameStart){
			this.oldRdyPlayerCount = rdy;
			Integer count = this.playerManager.getPlayerCount();
			game.getGamePlayerData().msgAll(HG.getPlugin().getLang().game_countdown.replace("<timer>", String.valueOf(timer)));
			game.getGamePlayerData().msgAll(HG.getPlugin().getLang().game_rdy_players.replace("<rdy>", rdy.toString()).replace("<players>", count.toString()));
		}
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
