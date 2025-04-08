package com.shanebeestudios.hg.api.events;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.data.ItemData.ChestType;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player opens an empty chest in the game
 * <p>This will initiate the chest loading its contents</p>
 */
public class ChestOpenEvent extends Event {

    private final Game game;
    private final Block block;
    private final ChestType chestType;

    /**
     * Create a new player open chest event
     *
     * @param game      The game this is happening in
     * @param block     The block that is opening
     * @param chestType Type of chest
     */
    public ChestOpenEvent(Game game, Block block, ChestType chestType) {
        this.game = game;
        this.block = block;
        this.chestType = chestType;
    }

    /**
     * Get the game in this event
     *
     * @return The game for this event
     */
    public Game getGame() {
        return game;
    }

    /**
     * Get the chest that has been opened
     *
     * @return The chest in the event
     */
    public Block getChest() {
        return block;
    }

    /**
     * Get the type of chest that was opened
     *
     * @return Type of chest
     */
    public ChestType getChestType() {
        return this.chestType;
    }

    private static final HandlerList handlers = new HandlerList();

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
