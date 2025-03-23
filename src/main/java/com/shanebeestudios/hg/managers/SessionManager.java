package com.shanebeestudios.hg.managers;

import com.shanebeestudios.hg.data.PlayerSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private final Map<UUID, PlayerSession> playerSessions = new HashMap<>();

    public SessionManager() {
    }

    public boolean hasPlayerSession(UUID uuid) {
        return playerSessions.containsKey(uuid);
    }

    public PlayerSession getPlayerSession(UUID uuid) {
        return this.playerSessions.get(uuid);
    }

    public PlayerSession createPlayerSession(UUID uuid, String name, int time, int minPlayers, int maxPlayers, int cost) {
        PlayerSession playerSession = new PlayerSession(name, time, minPlayers, maxPlayers, cost);
        this.playerSessions.put(uuid, playerSession);
        return playerSession;
    }

}
