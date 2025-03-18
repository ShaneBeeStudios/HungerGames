package com.shanebeestudios.hg.events;

import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.shanebeestudios.hg.game.Game;

import java.util.Collections;

/**
 * Called when a player dies in a HungerGames arena
 */
@SuppressWarnings("unused")
public class PlayerDeathGameEvent extends PlayerDeathEvent {

    // TODO this class needs some love
    private static final HandlerList handlers = new HandlerList();
    private static final DamageSource DAMAGE_SOURCE = DamageSource.builder(DamageType.GENERIC).build(); // TODO
    private final Game game;

    public PlayerDeathGameEvent(@NotNull Player player, @Nullable String deathMessage, @NotNull Game game) {
        super(player, DAMAGE_SOURCE, Collections.emptyList(), 0, deathMessage);
        this.game = game;
    }

    /**
     * Get the game the player died in
     *
     * @return Game player died in
     */
    public Game getGame() {
        return game;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
