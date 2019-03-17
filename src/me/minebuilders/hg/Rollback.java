package me.minebuilders.hg;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;

public class Rollback implements Runnable {

	private final Iterator<BlockState> session;
	private Game g;
	private int timerID;

	public Rollback(Game g) {
		this.g = g;
		g.setStatus(Status.ROLLBACK);
		this.session = g.getBlocks().iterator();
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, 1, 2);
	}

	public void run() {
		int i = 0;
		while (i < 50 && session.hasNext()) {
			i++;
			session.next().update(true);
		}
		if (!session.hasNext()) {
			Bukkit.getServer().getScheduler().cancelTask(timerID);
			g.resetBlocks();
			g.setStatus(Status.STOPPED);
		}
	}
}
