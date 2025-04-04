package com.shanebeestudios.hg.api.status;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Language;
import net.kyori.adventure.text.Component;

public enum PlayerStatus {

    IN_GAME,
    SPECTATOR,
    NOT_IN_GAME;

    private final Language lang = HungerGames.getPlugin().getLang();

    public Component getName() {
        return switch (this) {
            case IN_GAME -> Util.getMini(this.lang.player_status_in_game);
            case SPECTATOR -> Util.getMini(this.lang.player_status_spectator);
            case NOT_IN_GAME -> Util.getMini(this.lang.player_status_not_in_game);
        };
    }

    public String getStringName() {
        return Util.unMini(getName());
    }

}
