package tk.shanebee.hg.tasks;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.util.Util;

/**
 * Rollback task for resetting blocks after a game finishes
 */
public class Rollback implements Runnable {

	private final Iterator<BlockState> session;
	private final Game game;
	private final int blocks_per_second;
	private final int timerID;
	private final int total_blocks;
	private final long start;
	private final boolean logToConsole;
	private int blocks;
	private int ticks = 0;

	public Rollback(Game game) {
        this.logToConsole = Config.rollback_log_console;
        if (logToConsole) {
            Util.log("Rollback started on arena: " + game.getName());
        }
		this.game = game;
		this.blocks_per_second = Config.blocks_per_second / 10;
		this.start = System.currentTimeMillis();
		this.total_blocks = game.getBlocks().size();
		this.blocks = game.getBlocks().size();
		this.session = game.getBlocks().iterator();
        game.setStatus(Status.ROLLBACK);
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(HG.getPlugin(), this, 1, 2);
	}

	public void run() {
	    this.ticks += 2;
		int i = 0;
		while (i < blocks_per_second && session.hasNext()) {
			session.next().update(true);
			i++;
		}
		this.blocks -= i;
		if (this.ticks >= 40) {
            this.ticks = 0;
            printProgress();
        }
		if (!session.hasNext()) {
            if (logToConsole) {
                long finished = (int)((double)(System.currentTimeMillis() - this.start) / 1000);
                Util.log("&aRollback finished for arena: " + game.getName());
                Util.log(" - Completed in &b" + finished + " &7seconds");
            }
			Bukkit.getServer().getScheduler().cancelTask(timerID);
			game.resetBlocks();
			game.setStatus(Status.READY);
		}
	}

	private void printProgress() {
	    int percent = (int)((((double)(this.total_blocks - this.blocks) / (double)this.total_blocks)) * 100);
	    game.setRestorePercent(percent);
	    if (logToConsole) {
            Util.log(" - Progress: " + percent + "% (" + this.blocks + "/" + this.total_blocks + " blocks)");
        }
    }

}
