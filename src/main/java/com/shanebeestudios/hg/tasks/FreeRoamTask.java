package com.shanebeestudios.hg.tasks;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FreeRoamTask implements Runnable {

    private final Game game;
    private final Language lang;
    private final int id;
    private final int roamTime;

    public FreeRoamTask(Game game) {
        this.game = game;
        this.roamTime = game.getGameArenaData().getRoamTime();

        this.lang = HungerGames.getPlugin().getLang();
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
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getPlugin(), this, this.roamTime * 20L);
    }

    @Override
    public void run() {
        if (this.roamTime > 0) {
            this.game.getGamePlayerData().msgAll(this.lang.roam_finished);
        }
        this.game.startRunningGame();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }

}
