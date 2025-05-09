package com.shanebeestudios.hg.api.events;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Called when a player dies in a HungerGames arena
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public class PlayerDeathGameEvent extends PlayerDeathEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Game game;

    public PlayerDeathGameEvent(@NotNull Player player, DamageSource damageSource,
                                @NotNull List<ItemStack> drops, @Nullable String deathMessage, @NotNull Game game) {
        super(player, damageSource, drops, 0, Util.getMini(deathMessage), true);
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
