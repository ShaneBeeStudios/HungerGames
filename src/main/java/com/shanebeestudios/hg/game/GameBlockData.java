package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.data.ItemFrameData;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

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

    private final Map<ChestType, List<Location>> chests = new HashMap<>();
    private final List<BlockState> blocks = new ArrayList<>();
    private final Map<UUID, ItemFrameData> itemFrameData = new HashMap<>();
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

    private void resetChestMap() {
        for (ChestType value : ChestType.values()) {
            this.chests.put(value, new ArrayList<>());
        }
    }

    public void markChestForRefill() {
        this.chests.forEach((chestType, locations) -> {
            if (chestType == ChestType.REGULAR || chestType == ChestType.DROP) {
                locations.forEach(location -> {
                    if (location.getBlock().getState() instanceof InventoryHolder inventoryHolder) {
                        inventoryHolder.getInventory().clear();
                    }
                });
            }
        });
    }

    /**
     * Clear chests and mark them for refill
     */
    public void clearChests() {
        this.chests.forEach((chestType, locations) -> {
            locations.forEach(location -> {
                if (location.getBlock().getState() instanceof InventoryHolder inventoryHolder) {
                    inventoryHolder.getInventory().clear();
                }
            });
        });
        resetChestMap();
    }

    /**
     * Log that a chest has been opened by a player
     *
     * @param chestType Type of chest to log
     * @param location  Location of the chest to log
     */
    public void logChest(ChestType chestType, Location location) {
        if (this.chests.get(chestType).contains(location)) return;
        this.chests.get(chestType).add(location);
    }

    /**
     * Remove a logged chest from the game
     * <p>Will remove from all lists</p>
     *
     * @param location Location of the chest to remove
     */
    public void removeChest(Location location) {
        for (ChestType value : ChestType.values()) {
            this.chests.get(value).remove(location);
        }
    }

    /**
     * Remove a logged chest from the game
     *
     * @param chestType Type of chest to remove
     * @param location  Location of the chest to remove
     */
    public void removeChest(@NotNull ChestType chestType, Location location) {
        this.chests.get(chestType).remove(location);
    }


    /**
     * Check if chest at this location can be filled
     *
     * @param location Location of chest to check
     * @return True if this chest can be filled,
     * false if its already been opened or is player placed
     */
    public boolean canBeFilled(Location location) {
        for (List<Location> value : this.chests.values()) {
            if (value.contains(location)) return false;
        }
        return true;
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

    public enum ChestType {
        REGULAR,
        BONUS,
        PLAYER_PLACED,
        DROP
    }

}
