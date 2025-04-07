package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.api.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FreeRoamTask implements Runnable {

    private final Game game;
    private final Language lang;
    private final int roamTime;
    private final int taskId;

    public FreeRoamTask(Game game) {
        this.game = game;
        this.lang = game.getPlugin().getLang();
        int roamTime = game.getGameArenaData().getFreeRoamTime();
        if (roamTime < 0) {
            // Use default if less than 0
            roamTime = Config.SETTINGS_FREE_ROAM_TIME;
        }
        this.roamTime = Math.max(roamTime, 0);

        String gameStarted = this.lang.roam_game_started;
        String roamTimeString = this.lang.roam_time.replace("<roam>", "" + this.roamTime);

        for (Player player : game.getGamePlayerData().getPlayers()) {
            Util.sendMessage(player, gameStarted);
            if (this.roamTime > 0) {
                Util.sendMessage(player, roamTimeString);
            }
            player.setHealth(20);
            player.setFoodLevel(20);
            game.getGamePlayerData().unFreeze(player);
        }
        this.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getPlugin(), this, this.roamTime * 20L);
    }

    @Override
    public void run() {
        if (this.roamTime > 0) {
            this.game.getGamePlayerData().messageAllActivePlayers(this.lang.roam_finished);
        }
        this.game.startRunningGame();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}
