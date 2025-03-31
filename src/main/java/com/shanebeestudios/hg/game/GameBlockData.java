package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.data.ItemFrameData;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data class for holding a {@link Game Game's} blocks
 */
public class GameBlockData extends Data {

    private final List<Location> openedChests = new ArrayList<>();
    private final List<Location> playerPlacedChests = new ArrayList<>();
    private final List<BlockState> blocks = new ArrayList<>();
    private final Map<UUID,ItemFrameData> itemFrameData = new HashMap<>();
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
        for (Location location : this.openedChests) {
            if (location.getBlock().getState() instanceof InventoryHolder inventoryHolder) {
                inventoryHolder.getInventory().clear();
            }
        }
        this.openedChests.clear();
    }

    /**
     * Log that a chest has been opened by a player
     *
     * @param location Location of the chest to log
     */
    public void logOpenedChest(Location location) {
        if (this.openedChests.contains(location)) return;
        this.openedChests.add(location);
    }

    /**
     * Remove a logged chest from the game
     *
     * @param location Location of the chest to remove
     */
    public void removeOpenedChest(Location location) {
        this.openedChests.remove(location);
    }

    /**
     * Log a chest that a player placed
     *
     * @param location Location of the chest
     */
    public void logPlayerPlacedChest(Location location) {
        this.playerPlacedChests.add(location);
    }

    /**
     * Check if chest at this location can be filled
     *
     * @param location Location of chest to check
     * @return True if this chest can be filled,
     * false if its already been opened or is player placed
     */
    public boolean canBeFilled(Location location) {
        return !this.openedChests.contains(location) && !this.playerPlacedChests.contains(location);
    }

    /**
     * Remove a player placed chest from the game
     *
     * @param location Location of the chest
     */
    public void removePlayerChest(Location location) {
        playerPlacedChests.remove(location);
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
        return !this.blocks.isEmpty() || !this.itemFrameData.isEmpty();
    }

    /**
     * Add an item frame to be restored when the game finishes
     *
     * @param itemFrame ItemFrame to be added to the list
     */
    public void recordItemFrame(ItemFrame itemFrame) {
        if (this.itemFrameData.containsKey(itemFrame.getUniqueId())) return;
        this.itemFrameData.put(itemFrame.getUniqueId(), new ItemFrameData(itemFrame));
    }

    public Collection<ItemFrameData> getItemFrameData() {
        return this.itemFrameData.values();
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
