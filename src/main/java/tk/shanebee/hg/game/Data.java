package tk.shanebee.hg.game;

import tk.shanebee.hg.HG;

/**
 * General class for storing different aspects of data for {@link Game games}
 */
public abstract class Data {

    private final Game game;
    private final HG plugin;

    protected Data(Game game) {
        this.game = game;
        this.plugin = HG.getPlugin();
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
     * @return Instance of {@link HG plugin}
     */
    public HG getPlugin() {
        return plugin;
    }

}
