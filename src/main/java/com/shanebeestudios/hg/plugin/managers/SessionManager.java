package com.shanebeestudios.hg.plugin.managers;

import com.shanebeestudios.hg.api.data.PlayerSession;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private final Map<Player, PlayerSession> playerSessions = new HashMap<>();

    public SessionManager() {
    }

    public boolean hasPlayerSession(Player player) {
        return playerSessions.containsKey(player);
    }

    public PlayerSession getPlayerSession(Player player) {
        return this.playerSessions.get(player);
    }

    public PlayerSession createPlayerSession(Player player, String name, int time, int minPlayers, int maxPlayers, int cost) {
        PlayerSession playerSession = new PlayerSession(name, time, minPlayers, maxPlayers, cost);
        this.playerSessions.put(player, playerSession);
        return playerSession;
    }

    public void endPlayerSession(Player player) {
        this.playerSessions.remove(player);
    }

}
