package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.plugin.managers.GameManager;

/**
 * General class for storing different aspects of data for {@link Game Games}
 */
public abstract class Data {

    final Game game;
    final HungerGames plugin;
    final GameManager gameManager;
    final Language lang;

    Data(Game game) {
        this.game = game;
        this.plugin = game.plugin;
        this.gameManager = this.plugin.getGameManager();
        this.lang = game.lang;
    }

    /**
     * Get the {@link Game} this data belongs to.
     *
     * @return Game this data belongs to
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * Quick method to access the main plugin.
     *
     * @return Instance of {@link HungerGames plugin}
     */
    public HungerGames getPlugin() {
        return this.plugin;
    }

    /**
     * Get the {@link GameManager} for the plugin.
     *
     * @return Instance of {@link GameManager}
     */
    public GameManager getGameManager() {
        return this.gameManager;
    }

    /**
     * Get the {@link Language} for the plugin.
     *
     * @return Instance of {@link Language}
     */
    public Language getLang() {
        return this.lang;
    }

}
