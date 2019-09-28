package tk.shanebee.hg.tasks;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.Status;

/**
 * Rollback task for resetting blocks after a game finishes
 */
public class Rollback implements Runnable {

	private final Iterator<BlockState> session;
	private Game game;
	private int blocks_per_second;
	private int timerID;

	public Rollback(Game game) {
		this.game = game;
		this.blocks_per_second = Config.blocks_per_second / 10;
		game.setStatus(Status.ROLLBACK);
		this.session = game.getBlocks().iterator();
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, 1, 2);
	}

	public void run() {
		int i = 0;
		while (i < blocks_per_second && session.hasNext()) {
			session.next().update(true);
			i++;
		}
		if (!session.hasNext()) {
			Bukkit.getServer().getScheduler().cancelTask(timerID);
			game.resetBlocks();
			game.setStatus(Status.READY);
		}
	}

}
