package com.shanebeestudios.hg.plugin.tasks;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.game.GameBlockData;
import com.shanebeestudios.hg.game.GamePlayerData;
import org.bukkit.Bukkit;

public class GameTimerTask implements Runnable {

    private int remainingTime;
    private final int teleportTimer;
    private final int taskId;
    private final Game game;
    private final Language lang;
    private final GameArenaData gameArenaData;
    private final GameBlockData gameBlockData;
    private final GamePlayerData gamePlayerData;
    private final String end_min;
    private final String end_min_sec;
    private final String end_sec;

    public GameTimerTask(Game game, int time) {
        this.remainingTime = time;
        this.game = game;
        HungerGames plugin = game.getPlugin();
        this.lang = plugin.getLang();
        this.gameArenaData = game.getGameArenaData();
        this.gameBlockData = game.getGameBlockData();
        this.gamePlayerData = game.getGamePlayerData();

        this.teleportTimer = Config.SETTINGS_TELEPORT_AT_END_TIME;
        game.getGamePlayerData().getPlayers().forEach(player -> player.setInvulnerable(false));

        this.end_min = this.lang.game_ending_min;
        this.end_min_sec = this.lang.game_ending_minsec;
        this.end_sec = this.lang.game_ending_sec;

        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20L);
    }

    @Override
    public void run() {
        if (this.gameArenaData.getStatus() != Status.RUNNING) stop(); // Safety exit

        if (Config.bossbar) this.game.getGameBarData().bossBarUpdate(this.remainingTime);

        // Refill chests
        if (this.gameArenaData.getChestRefillTime() > 0 && this.remainingTime == this.gameArenaData.getChestRefillTime()) {
            this.gameBlockData.markChestForRefill();
            this.gamePlayerData.msgAll(this.lang.game_chest_refill);
        }

        // Final teleport to center
        if (this.teleportTimer > 0 && this.remainingTime == this.teleportTimer) {
            this.gamePlayerData.msgAll(this.lang.game_almost_over);
            this.gamePlayerData.respawnAll();
        } else if (this.remainingTime <= 0) {
            stop();
            this.game.stop(false);
        } else if (!Config.bossbar && this.remainingTime % 30 == 0) {
            int minutes = this.remainingTime / 60;
            int seconds = this.remainingTime % 60;
            if (minutes != 0) {
                if (seconds == 0) {
                    if (this.end_min.isEmpty()) return;
                    this.gamePlayerData.msgAll(this.end_min.replace("<minutes>", "" + minutes));
                } else {
                    if (this.end_min_sec.isEmpty()) return;
                    this.gamePlayerData.msgAll(this.end_min_sec.replace("<minutes>", "" + minutes)
                        .replace("<seconds>", "" + seconds));
                }
            } else {
                if (this.end_sec.isEmpty()) return;
                this.gamePlayerData.msgAll(this.end_sec.replace("<seconds>", "" + this.remainingTime));
            }
        }
        this.remainingTime -= 1;
    }

    public int getRemainingTime() {
        return this.remainingTime;
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

}
