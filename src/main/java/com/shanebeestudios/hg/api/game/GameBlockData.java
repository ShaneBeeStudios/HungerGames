package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.api.data.ItemData;
import com.shanebeestudios.hg.api.data.ItemFrameData;
import com.shanebeestudios.hg.api.util.BlockUtils;
import com.shanebeestudios.hg.plugin.configs.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Data class for holding a {@link Game Game's} blocks
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class GameBlockData extends Data {

    private final Random random = new Random();
    private final Map<ItemData.ChestType, List<Location>> chests = new HashMap<>();
    private final List<BlockState> blocks = new ArrayList<>();
    private final List<Block> randomBonusChests = new ArrayList<>();
    private final Map<UUID, ItemFrameData> itemFrameData = new HashMap<>();
    private final GameLobbyWall gameLobbyWall;

    GameBlockData(Game game) {
        super(game);
        this.gameLobbyWall = new GameLobbyWall(game);
        for (ItemData.ChestType value : ItemData.ChestType.values()) {
            this.chests.put(value, new ArrayList<>());
        }
    }

    /**
     * Get the main lobby sign
     *
     * @return Main lobby sign
     */
    public Location getSignLocation() {
        return this.gameLobbyWall.getSignLocation();
    }

    /**
     * Set the location of the lobby wall
     *
     * @param location Location of far left sign
     * @return Whether the lobby wall correctly set
     */
    public boolean setLobbyBlock(Location location) {
        return this.gameLobbyWall.setLobbyBlock(location);
    }

    /**
     * Update the lobby wall
     */
    public void updateLobbyBlock() {
        this.gameLobbyWall.updateLobbyBlock();
    }

    /**
     * Check whether the lobby wall is valid
     *
     * @return Whether lobby wall is valid
     */
    public boolean isLobbyValid() {
        return this.gameLobbyWall.isLobbyValid();
    }

    private void clearChestMaps(ItemData.ChestType... type) {
        for (ItemData.ChestType chestType : type) {
            this.chests.get(chestType).clear();
        }
    }

    /**
     * Mark chests to be refilled
     */
    public void markChestForRefill() {
        this.chests.forEach((chestType, locations) -> {
            if (chestType == ItemData.ChestType.REGULAR || chestType == ItemData.ChestType.CHEST_DROP) {
                locations.forEach(location -> {
                    if (location.getBlock().getState() instanceof InventoryHolder inventoryHolder) {
                        inventoryHolder.getInventory().clear();
                    }
                });
            }
        });
        clearChestMaps(ItemData.ChestType.REGULAR);
        clearChestMaps(ItemData.ChestType.BONUS);
    }

    /**
     * Clear chests
     */
    public void clearChests() {
        this.chests.forEach((chestType, locations) ->
            locations.forEach(location -> {
                if (location.getBlock().getState() instanceof InventoryHolder inventoryHolder) {
                    inventoryHolder.getInventory().clear();
                }
            }));
        clearChestMaps(ItemData.ChestType.values());
    }

    /**
     * Log that a chest has been opened by a player
     *
     * @param chestType Type of chest to log
     * @param location  Location of the chest to log
     */
    public void logChest(ItemData.ChestType chestType, Location location) {
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
        for (ItemData.ChestType value : ItemData.ChestType.values()) {
            this.chests.get(value).remove(location);
        }
    }

    /**
     * Remove a logged chest from the game
     *
     * @param chestType Type of chest to remove
     * @param location  Location of the chest to remove
     */
    public void removeChest(@NotNull ItemData.ChestType chestType, Location location) {
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

    /**
     * Log all blocks in an arena for rollback
     */
    public void logBlocksForRollback() {
        for (Location location : this.getGame().getGameArenaData().getGameRegion().getBlocks(null)) {
            Block block = location.getBlock();
            this.blocks.add(block.getState());
            if (Config.CHESTS_BONUS_RANDOMIZE_ENABLED && BlockUtils.isBonusBlockReplacement(block)) {
                this.randomBonusChests.add(block);
                block.setType(Material.AIR);
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void setupRandomizedBonusChests() {
        Optional<BlockType> first = BlockUtils.getBonusBlockTypes().stream().findFirst();
        if (first.isEmpty()) return;
        BlockData blockData = first.get().createBlockData();

        if (Config.CHESTS_BONUS_RANDOMIZE_ENABLED) {
            this.randomBonusChests.forEach(bonusChest -> {
                if (this.random.nextInt(100) < Config.CHESTS_BONUS_RANDOMIZE_CHANCE) {
                    bonusChest.setBlockData(blockData);
                }
            });
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

    /**
     * Get all the logged item frames
     *
     * @return Logged item frames
     */
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
        this.randomBonusChests.clear();
    }

    /**
     * Clear the current item frame list
     */
    public void resetItemFrames() {
        this.itemFrameData.clear();
    }

}
