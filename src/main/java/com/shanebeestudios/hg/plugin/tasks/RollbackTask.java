package com.shanebeestudios.hg.plugin.tasks;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.api.data.ItemFrameData;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.game.GameBlockData;

/**
 * Rollback task for resetting blocks after a game finishes
 */
public class RollbackTask implements Runnable {

	private final Iterator<BlockState> blockRollbackSession;
	private final Iterator<ItemFrameData> itemFrameDataIterator;
	private final Game game;
	private final GameBlockData gameBlockData;
	private final int blocks_per_tick;
	private int taskId;

	public RollbackTask(Game game) {
		this.game = game;
		this.gameBlockData = game.getGameBlockData();
		this.blocks_per_tick = Config.ROLLBACK_BLOCKS_PER_SECOND / 20;
		game.getGameArenaData().setStatus(Status.ROLLBACK);
		this.blockRollbackSession = this.gameBlockData.getBlocks().iterator();
		this.itemFrameDataIterator = this.gameBlockData.getItemFrameData().iterator();
		this.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getPlugin(), this, 20);
	}

	public void run() {
		int rolledBack = 0;
		// Rollback blocks
		while (rolledBack < this.blocks_per_tick && this.blockRollbackSession.hasNext()) {
		    BlockState state = this.blockRollbackSession.next();
		    if (state != null && state.getType() != state.getBlock().getType()) {
                state.update(true, false);
                rolledBack++;
            }
		}
		if (this.blockRollbackSession.hasNext()) {
			this.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getPlugin(), this, 1);
			return;
		}

		// Rollback item frames
		while (this.itemFrameDataIterator.hasNext()) {
		    ItemFrameData data = this.itemFrameDataIterator.next();
		    if (data != null) {
		        data.resetItem();
            }
        }

        this.gameBlockData.resetBlocks();
        this.gameBlockData.resetItemFrames();
        this.game.getGameArenaData().setStatus(Status.READY);
        Bukkit.getScheduler().cancelTask(this.taskId);
	}

}
