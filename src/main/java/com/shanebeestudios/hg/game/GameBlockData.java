package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.data.ItemFrameData;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data class for holding a {@link Game Game's} blocks
 */
public class GameBlockData extends Data {

    private final List<Location> chests = new ArrayList<>();
    private final List<Location> playerChests = new ArrayList<>();
    private final List<BlockState> blocks = new ArrayList<>();
    private final List<ItemFrameData> itemFrameData = new ArrayList<>();
    private final GameLobbyWall gameLobbyWall;

    protected GameBlockData(Game game) {
        super(game);
        this.gameLobbyWall = new GameLobbyWall(game);
    }

    /**
     * Get the main lobby sign
     *
     * @return Main lobby sign
     */
    public Location getSignLocation() {
        return this.gameLobbyWall.getSignLocation();
    }

    public boolean setLobbyBlock(Location location) {
        return this.gameLobbyWall.setLobbyBlock(location);
    }

    public void updateLobbyBlock() {
        this.gameLobbyWall.updateLobbyBlock();
    }

    public boolean isLobbyValid() {
        return this.gameLobbyWall.isLobbyValid();
    }

    /**
     * Clear chests and mark them for refill
     */
    public void clearChests() {
        for (Location location : this.chests) {
            if (location.getBlock().getState() instanceof InventoryHolder inventoryHolder) {
                inventoryHolder.getInventory().clear();
                location.getBlock().getState().update();
            }
        }
        this.chests.clear();
    }

    /**
     * Add a game chest location to the game
     *
     * @param location Location of the chest to add (Needs to actually be a chest there)
     */
    public void addGameChest(Location location) {
        chests.add(location);
    }

    /**
     * Add a player placed chest to the game
     *
     * @param location Location of the chest
     */
    public void addPlayerChest(Location location) {
        playerChests.add(location);
    }

    /**
     * Check if chest at this location is logged
     *
     * @param location Location of chest to check
     * @return True if this chest was added already
     */
    public boolean isLoggedChest(Location location) {
        return chests.contains(location) || playerChests.contains(location);
    }

    /**
     * Remove a game chest from the game
     *
     * @param location Location of the chest to remove
     */
    public void removeGameChest(Location location) {
        chests.remove(location);
    }

    /**
     * Remove a player placed chest from the game
     *
     * @param location Location of the chest
     */
    public void removePlayerChest(Location location) {
        playerChests.remove(location);
    }

    public void logBlocksForRollback() {
        for (Location location : this.getGame().getGameArenaData().getGameRegion().getBlocks(null)) {
            this.blocks.add(location.getBlock().getState());
        }
    }

    /**
     * Force a rollback for this game
     * <p>This is not recommended to use as it forces all blocks to
     * rollback at once, which can cause heavy amounts of lag.</p>
     */
    public void forceRollback() {
        Collections.reverse(blocks);
        for (BlockState state : blocks) {
            state.update(true);
        }
    }

    boolean requiresRollback() {
        return !blocks.isEmpty() || !itemFrameData.isEmpty();
    }

    /**
     * Add an item frame to be restored when the game finishes
     *
     * @param itemFrame ItemFrame to be added to the list
     */
    public void recordItemFrame(ItemFrame itemFrame) {
        this.itemFrameData.add(new ItemFrameData(itemFrame));
    }

    public List<ItemFrameData> getItemFrameData() {
        return this.itemFrameData;
    }

    /**
     * Get a list of all recorded blocks
     *
     * @return List of all recorded blocks
     */
    public List<BlockState> getBlocks() {
        Collections.reverse(this.blocks);
        return this.blocks;
    }

    /**
     * Clear the current block list
     */
    public void resetBlocks() {
        this.blocks.clear();
    }

    /**
     * Clear the current item frame list
     */
    public void resetItemFrames() {
        this.itemFrameData.clear();
    }

}
