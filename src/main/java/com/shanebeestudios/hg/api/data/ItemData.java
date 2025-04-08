package com.shanebeestudios.hg.api.data;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemData {

    private final Map<ChestType, List<ItemStack>> items = new HashMap<>();
    private final Map<ChestType, Integer> count = new HashMap<>();

    public ItemData() {
        for (ChestType chestType : ChestType.values()) {
            this.items.put(chestType, new ArrayList<>());
        }
    }

    public void setItems(ChestType type, List<ItemStack> items) {
        this.items.put(type, items);
    }

    public List<ItemStack> getItems(ChestType type) {
        return this.items.get(type);
    }

    /**
     * Set item count
     *
     * @param chestType ChestType to count
     * @param itemCount Amount of items
     */
    public void setItemCount(ChestType chestType, int itemCount) {
        this.count.put(chestType, itemCount);
    }

    /**
     * Get item count by ChestType
     *
     * @param chestType ChestType to get count from
     * @return AMount of items by ChestType
     */
    public int getItemCount(ChestType chestType) {
        return this.count.get(chestType);
    }

    /**
     * Get total item count for all chest types
     *
     * @return Total item count
     */
    public int getTotalItemCount() {
        int count = 0;
        for (int value : this.count.values()) {
            count += value;
        }
        return count;
    }

    /**
     * Represents the type of chests in game
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
