package com.shanebeestudios.hg.data;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemData {

    private int itemCount;
    private final Map<ChestType, List<ItemStack>> items = new HashMap<>();

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

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemCount() {
        return this.itemCount;
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
