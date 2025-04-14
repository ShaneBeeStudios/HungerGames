package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameArenaData;
import com.shanebeestudios.hg.api.game.GameBlockData;
import com.shanebeestudios.hg.api.game.GamePlayerData;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.plugin.configs.Language;
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
        this.gamePlayerData.messageAllActivePlayers(this.lang.game_chest_refill);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}
