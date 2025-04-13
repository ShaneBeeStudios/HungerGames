package com.shanebeestudios.hg.plugin.tasks;

import org.bukkit.Bukkit;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.Util;

public class StartingTask implements Runnable {

    private int timer;
    private final int taskId;
    private final Game game;
    private final Language lang;

    public StartingTask(Game game) {
        this.timer = 30;
        this.game = game;
        this.lang = game.getPlugin().getLang();
        String name = game.getGameArenaData().getName();
        String broadcast = this.lang.game_countdown_started
                .replace("<arena>", name)
                .replace("<seconds>", "" + this.timer);
        if (Config.SETTINGS_BROADCAST_JOIN_MESSAGES) {
            Util.broadcast(broadcast);
            Util.broadcast(this.lang.game_join.replace("<arena>", name));
        } else {
            this.game.getGamePlayerData().messageAllActivePlayers(broadcast);
        }
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.getPlugin(), this, 5 * 20L, 5 * 20L);
    }

    @Override
    public void run() {
        this.timer = (this.timer - 5);

        if (this.timer <= 0) {
            stop();
            this.game.startFreeRoam();
        } else {
            this.game.getGamePlayerData().messageAllActivePlayers(this.lang.game_countdown_timer.replace("<timer>", String.valueOf(this.timer)));
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}
