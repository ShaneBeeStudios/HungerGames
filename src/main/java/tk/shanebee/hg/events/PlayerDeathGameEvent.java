package tk.shanebee.hg.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.hg.game.Game;

import java.util.Collections;

/**
 * Called when a player dies in a HungerGames arena
 */
@SuppressWarnings("unused")
public class PlayerDeathGameEvent extends PlayerDeathEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Game game;

    public PlayerDeathGameEvent(@NotNull Player player, @Nullable String deathMessage, @NotNull Game game) {
        super(player, Collections.emptyList(), 0, deathMessage);
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
