package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.BlockUtils;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Iterator;
import java.util.List;

/**
 * Task for preparing an arena by logging blocks for rollback
 */
public class PrepareArenaTask implements Runnable {

    private final Game game;
    private final int blocksPerTick;
    private Iterator<Location> blocksToLog;
    private final int countToLog;
    private int totalLogged = 0;
    private int taskId;

    public PrepareArenaTask(Game game) {
        this.game = game;
        this.blocksPerTick = Config.ROLLBACK_BLOCKS_PER_SECOND / 20;

        Util.debug("Starting prepare task, grabbing blocks to log...");
        List<Location> blocks;
        if (Config.ROLLBACK_ENABLED) {
            blocks = this.game.getGameArenaData().getGameRegion().getBlocks(null);
        } else if (Config.CHESTS_BONUS_RANDOMIZE_ENABLED) {
            blocks = this.game.getGameArenaData().getGameRegion().getBlocks(BlockUtils::isBonusBlockReplacement);
        } else {
            blocks = List.of();
        }
        this.countToLog = blocks.size();
        this.blocksToLog = blocks.iterator();
        Util.debug("Found %s blocks to log", this.countToLog);
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
            Block block = this.blocksToLog.next().getBlock();
            this.game.getGameBlockData().logBlockForRollback(block);
            logged++;
            this.totalLogged++;

            if (Config.CHESTS_BONUS_RANDOMIZE_ENABLED && BlockUtils.isBonusBlockReplacement(block)) {
                this.game.getGameBlockData().logRandomBonusChest(block);
                block.setType(Material.AIR);
            }
        }
        if (this.blocksToLog.hasNext()) {
            Util.debug("Logging blocks... %,d/%,d", this.totalLogged, this.countToLog);
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
