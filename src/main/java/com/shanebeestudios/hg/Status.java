package com.shanebeestudios.hg;

import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.util.Util;

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
	NOT_READY
    ;

	Language lang = HungerGames.getPlugin().getLang();

	public String getName() {
        switch (this) {
            case RUNNING:
                return Util.getColString(lang.status_running);
            case STOPPED:
                return Util.getColString(lang.status_stopped);
            case READY:
                return Util.getColString(lang.status_ready);
            case WAITING:
                return Util.getColString(lang.status_waiting);
            case BROKEN:
                return Util.getColString(lang.status_broken);
            case ROLLBACK:
                return Util.getColString(lang.status_rollback);
            case NOT_READY:
                return Util.getColString(lang.status_not_ready);
            case FREE_ROAM:
                return Util.getColString(lang.status_beginning);
            case COUNTDOWN:
                return Util.getColString(lang.status_countdown);
            default:
                return Util.getColString("&cERROR!");
        }
	}

}
