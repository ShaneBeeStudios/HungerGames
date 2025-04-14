package com.shanebeestudios.hg.api.status;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import net.kyori.adventure.text.Component;

/**
 * Game status types
 */
public enum Status {

    /**
     * Game is ready to run
     */
    READY,
    /**
     * Game is waiting for players to join
     */
    WAITING,
    /**
     * Game is counting down to start
     */
    COUNTDOWN,
    /**
     * Game is starting to run in the free roam state
     */
    FREE_ROAM,
    /**
     * Game is running
     */
    RUNNING,
    /**
     * Game has stopped
     */
    STOPPED,
    /**
     * Game is currently rolling back blocks
     */
    ROLLBACK,
    /**
     * Game is broken
     */
    BROKEN,
    /**
     * Game is not ready
     */
    NOT_READY;

    final Language lang = HungerGames.getPlugin().getLang();

    public boolean isActive() {
        return switch (this) {
            case WAITING, COUNTDOWN, FREE_ROAM, RUNNING, ROLLBACK -> true;
            default -> false;
        };
    }

    public Component getName() {
        return switch (this) {
            case READY -> Util.getMini(this.lang.game_status_ready);
            case WAITING -> Util.getMini(this.lang.game_status_waiting);
            case COUNTDOWN -> Util.getMini(this.lang.game_status_countdown);
            case FREE_ROAM -> Util.getMini(this.lang.game_status_free_roam);
            case RUNNING -> Util.getMini(this.lang.game_status_running);
            case STOPPED -> Util.getMini(this.lang.game_status_stopped);
            case ROLLBACK -> Util.getMini(this.lang.game_status_rollback);
            case BROKEN -> Util.getMini(this.lang.game_status_broken);
            case NOT_READY -> Util.getMini(this.lang.game_status_not_ready);
        };
    }

    public String getStringName() {
        return Util.unMini(this.getName());
    }

}
