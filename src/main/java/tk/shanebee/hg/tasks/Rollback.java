package tk.shanebee.hg.tasks;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.data.ItemFrameData;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.Status;

/**
 * Rollback task for resetting blocks after a game finishes
 */
public class Rollback implements Runnable {

	private final Iterator<BlockState> session;
	private final Iterator<ItemFrameData> itemFrameDataIterator;
	private final Game game;
	private final int blocks_per_second;
	private int timerID;

	public Rollback(Game game) {
		this.game = game;
		this.blocks_per_second = Config.blocks_per_second / 10;
		game.setStatus(Status.ROLLBACK);
		this.session = game.getBlocks().iterator();
		this.itemFrameDataIterator = game.getItemFrameData().iterator();
		timerID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HG.getPlugin(), this, 2);
	}

	public void run() {
		int i = 0;
		// Rollback blocks
		while (i < blocks_per_second && session.hasNext()) {
		    BlockState state = session.next();
		    if (state != null) {
                state.update(true);
            }
			i++;
		}
		if (session.hasNext()) {
			timerID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HG.getPlugin(), this, 2);
			return;
		}

		// Rollback item frames
		while (itemFrameDataIterator.hasNext()) {
		    ItemFrameData data = itemFrameDataIterator.next();
		    if (data != null) {
		        data.resetItem();
            }
        }

        Bukkit.getServer().getScheduler().cancelTask(timerID);
        game.resetBlocks();
        game.resetItemFrames();
        game.setStatus(Status.READY);
	}

}
