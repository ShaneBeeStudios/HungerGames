package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.game.GameBlockData;
import com.shanebeestudios.hg.game.GamePlayerData;
import org.bukkit.Bukkit;

public class ChestRefillRepeatTask implements Runnable {

    private final Language lang;
    private final GameArenaData gameArenaData;
    private final GameBlockData gameBlockData;
    private final GamePlayerData gamePlayerData;
    private final int taskId;

    public ChestRefillRepeatTask(Game game) {
        this.lang = game.getPlugin().getLang();
        this.gameArenaData = game.getGameArenaData();
        this.gameBlockData = game.getGameBlockData();
        this.gamePlayerData = game.getGamePlayerData();

        int chestRefillRepeat = game.getGameArenaData().getChestRefillRepeat() * 20;
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.getPlugin(), this,
            chestRefillRepeat, chestRefillRepeat);
    }

    @Override
    public void run() {
        if (this.gameArenaData.getStatus() != Status.RUNNING) {
            stop();
            return;
        }
        this.gameBlockData.markChestForRefill();
        this.gamePlayerData.msgAll(this.lang.game_chest_refill);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}
