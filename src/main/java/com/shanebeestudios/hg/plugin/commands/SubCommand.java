package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.plugin.managers.GameManager;
import com.shanebeestudios.hg.plugin.managers.PlayerManager;
import dev.jorel.commandapi.arguments.Argument;

public abstract class SubCommand {

    protected final HungerGames plugin;
    protected final Language lang;
    protected final PlayerManager playerManager;
    protected final GameManager gameManager;

    public SubCommand(HungerGames plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.playerManager = plugin.getPlayerManager();
        this.gameManager = plugin.getGameManager();
    }

    protected abstract Argument<?> register();

    public void saveGame(Game game) {
        this.plugin.getArenaConfig().saveGameToConfig(game);
    }

}
