package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.data.Language;
import com.shanebeestudios.hg.api.data.Leaderboard;
import com.shanebeestudios.hg.plugin.managers.GameManager;
import com.shanebeestudios.hg.plugin.managers.KillManager;
import com.shanebeestudios.hg.plugin.managers.PlayerManager;
import org.bukkit.event.Listener;

public abstract class GameListenerBase implements Listener {

    protected final HungerGames plugin;
    protected final Language lang;
    protected final KillManager killManager;
    protected final GameManager gameManager;
    protected final PlayerManager playerManager;
    protected final Leaderboard leaderboard;

    public GameListenerBase(HungerGames plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.gameManager = plugin.getGameManager();
        this.playerManager = plugin.getPlayerManager();
        this.leaderboard = plugin.getLeaderboard();
        this.killManager = plugin.getKillManager();
    }

}
