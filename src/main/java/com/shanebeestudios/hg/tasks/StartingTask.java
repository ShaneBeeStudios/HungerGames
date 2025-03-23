package com.shanebeestudios.hg.tasks;

import org.bukkit.Bukkit;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.api.util.Util;

public class StartingTask implements Runnable {

    private int timer;
    private final int id;
    private final Game game;
    private final Language lang;

    public StartingTask(Game g) {
        this.timer = 30;
        this.game = g;
        this.lang = HungerGames.getPlugin().getLang();
        String name = g.getGameArenaData().getName();
        String broadcast = lang.game_started
                .replace("<arena>", name)
                .replace("<seconds>", "" + timer);
        if (Config.broadcastJoinMessages) {
            Util.broadcast(broadcast);
            Util.broadcast(lang.game_join.replace("<arena>", name));
        } else {
            game.getGamePlayerData().msgAll(broadcast);
        }
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.getPlugin(), this, 5 * 20L, 5 * 20L);
    }

    @Override
    public void run() {
        timer = (timer - 5);

        if (timer <= 0) {
            stop();
            game.startFreeRoam();
        } else {
            game.getGamePlayerData().msgAll(lang.game_countdown.replace("<timer>", String.valueOf(timer)));
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }

}
