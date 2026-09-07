package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.BlockUtils;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Iterator;

/**
 * Task for preparing an arena by logging blocks for rollback
 */
public class PrepareArenaTask implements Runnable {

    private final Game game;
    private final int blocksPerTick;
    private Iterator<Block> blocksToLog;
    private int totalLogged = 0;
    private int taskId;

    public PrepareArenaTask(Game game) {
        this.game = game;
        this.blocksPerTick = Config.ROLLBACK_BLOCKS_PER_SECOND / 20;

        Util.debug("Starting prepare task, grabbing blocks to log...");
        if (Config.ROLLBACK_ENABLED) {
            this.blocksToLog = this.game.getGameArenaData().getGameRegion().getBlockIterator(null);
        } else if (Config.CHESTS_BONUS_RANDOMIZE_ENABLED) {
            this.blocksToLog = this.game.getGameArenaData().getGameRegion().getBlockIterator(BlockUtils::isBonusBlockReplacement);
        } else {
            this.blocksToLog = java.util.Collections.emptyIterator();
        }
        this.taskId = schedule();
    }

    @Override
    public void run() {
        int logged = 0;

        while (logged < this.blocksPerTick && this.blocksToLog.hasNext()) {
            if (this.taskId == -1) {
                // Task has been stopped
                return;
            }
            Block block = this.blocksToLog.next();
            this.game.getGameBlockData().logBlockForRollback(block);
            logged++;
            this.totalLogged++;

            if (Config.CHESTS_BONUS_RANDOMIZE_ENABLED && BlockUtils.isBonusBlockReplacement(block)) {
                this.game.getGameBlockData().logRandomBonusChest(block);
                block.setType(Material.AIR);
            }
        }
        if (this.blocksToLog.hasNext()) {
            Util.debug("Logging blocks... %,d", this.totalLogged);
            this.taskId = this.schedule();
            return;
        }

        Util.debug("Finished logging blocks and setting up bonus chests");
        this.blocksToLog = null;
        this.game.getGameBlockData().setupRandomizedBonusChests();
        Bukkit.getScheduler().cancelTask(this.taskId);
        this.game.startWaitingPeriod();
    }

    public void stop() {
        Util.debug("Cancelling prepare task");
        Bukkit.getScheduler().cancelTask(this.taskId);
        this.taskId = -1;
    }

    private int schedule() {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getPlugin(), this, 1);
    }

}
