package com.shanebeestudios.hg.api.data;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.WeightedList;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder of {@link ItemStack Items} for a {@link Game}
 */
public class ItemData {

    private final Map<ChestType, WeightedList<ItemStack>> weightedItems = new HashMap<>();

    public ItemData() {
        for (ChestType chestType : ChestType.values()) {
            this.weightedItems.put(chestType, new WeightedList<>());
        }
    }

    /**
     * Add a weighted item to the item data.
     *
     * @param type   Chest type
     * @param item   Item to add
     * @param weight Weight of item
     */
    public void addEntry(ChestType type, ItemStack item, int weight) {
        this.weightedItems.get(type).add(item, weight);
    }

    /**
     * Get a random item.
     *
     * @param type Type of chest
     * @return Random item
     */
    public ItemStack getRandomItem(ChestType type) {
        return this.weightedItems.get(type).nextEntry();
    }

    public void setWeightedItems(ChestType type, WeightedList<ItemStack> weightedItems) {
        this.weightedItems.put(type, weightedItems);
    }

    public WeightedList<ItemStack> getWeightedItems(ChestType type) {
        return this.weightedItems.get(type);
    }

    /**
     * Get the total item count for all chest types.
     *
     * @return Total item count
     */
    public int getTotalItemCount() {
        int count = 0;
        for (WeightedList<ItemStack> value : this.weightedItems.values()) {
            count += value.size();
        }
        return count;
    }

    /**
     * Represents the type of chests in a game.
     * <p>Used for logging and refilling</p>
     */
    public enum ChestType {
        /**
         * A chest holding regular items
         */
        REGULAR("regular"),
        /**
         * A chest holding bonus items
         */
        BONUS("bonus"),
        /**
         * A player placed chest which cannot be refilled
         */
        PLAYER_PLACED("player-placed"),
        /**
         * A chest that has dropped
         */
        CHEST_DROP("chest-drop"),
        ;

        private final String name;

        ChestType(String name) {
            this.name = name;
        }

        /**
         * Get the name of this chest type
         *
         * @return Name of chest type
         */
        public String getName() {
            return this.name;
        }
    }

}
