package tk.shanebee.hg;

import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.util.Util;

/**
 * Game status types
 */
public enum Status {

    /**
     * Game is running
     * <p>Game is active with players</p>
     */
    RUNNING,
    /**
     * Game is in free roam state
     * <p>This is the free roam period of the game where
     * players can not PvP</p>
     */
    FREE_ROAM,
    /**
     * Game is counting down to start
     * <p>This is the point at which players have been teleported
     * to the arena, they can not move, and they have a chance
     * to choose their kits</p>
     */
    COUNTDOWN,
    /**
     * Game is starting up
     * <p>This is the point players are warned before
     * teleporting them to the arena</p>
     */
    STARTING,
    /**
     * Game is waiting
     * <p>At least one player has joined, waiting for minimum</p>
     */
    WAITING,
    /**
     * Game is ready to run
     * <p>Game is ready to join</p>
     */
    READY,
    /**
     * Game is broken
     */
    BROKEN,
    /**
     * Game has stopped
     */
    STOPPED,
    /**
     * Game is currently rolling back blocks
     * <p>Post-Game fixing arena, this stage may take some time</p>
     */
    ROLLBACK,
    /**
     * Game is not ready
     */
    NOTREADY,
    /**
     * Game is loading (pre-loading chunks)
     */
    LOADING;

    Language lang = HG.getPlugin().getLang();

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
            case NOTREADY:
                return Util.getColString(lang.status_not_ready);
            case FREE_ROAM:
                return Util.getColString(lang.status_beginning);
            case STARTING:
            case COUNTDOWN:
                return Util.getColString(lang.status_countdown);
            case LOADING:
                return Util.getColString(lang.status_loading);
            default:
                return Util.getColString("&c&lERROR!");
        }
    }

}
