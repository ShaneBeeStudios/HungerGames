package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;

/**
 * General class for storing different aspects of data for {@link Game Games}
 */
public abstract class Data {

    final Game game;
    final HungerGames plugin;
    final Language lang;

    Data(Game game) {
        this.game = game;
        this.plugin = game.plugin;
        this.lang = game.lang;
    }

    /**
     * Get the {@link Game} this data belongs to
     *
     * @return Game this data belongs to
     */
    public Game getGame() {
        return game;
    }

    /**
     * Quick method to access the main plugin
     *
     * @return Instance of {@link HungerGames plugin}
     */
    public HungerGames getPlugin() {
        return plugin;
    }

}
