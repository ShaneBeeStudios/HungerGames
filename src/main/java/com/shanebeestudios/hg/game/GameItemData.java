package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.data.RandomItems;
import com.shanebeestudios.hg.game.GameBlockData.ChestType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Data class for holding a {@link Game Game's} items
 */
@SuppressWarnings("unused")
public class GameItemData extends Data {

    private final RandomItems randomItems;
    private final Map<ChestType, Map<Integer, ItemStack>> chestItems = new HashMap<>();

    protected GameItemData(Game game) {
        super(game);
        this.randomItems = getPlugin().getRandomItems();
        for (ChestType value : ChestType.values()) {
            this.chestItems.put(value, new HashMap<>());
        }
        // Set default items from items.yml (if arenas.yml has items it will override this)
        resetItemsDefault(ChestType.REGULAR);
        resetItemsDefault(ChestType.BONUS);
        resetItemsDefault(ChestType.CHEST_DROP);
    }

    /**
     * Set the items for this game
     *
     * @param items Map of items to set
     */
    public void setItems(ChestType chestType, Map<Integer, ItemStack> items) {
        this.chestItems.put(chestType, items);
    }

    /**
     * Get the items map for this game
     *
     * @return Map of items
     */
    public Map<Integer, ItemStack> getItems(ChestType chestType) {
        return this.chestItems.get(chestType);
    }

    /**
     * Add an item to the items map for this game
     *
     * @param item ItemStack to add
     */
    public void addToItems(ChestType chestType, ItemStack item) {
        Map<Integer, ItemStack> map = this.chestItems.get(chestType);
        map.put(map.size() + 1, item.clone());
    }

    /**
     * Clear the items for this game
     */
    public void clearItems(ChestType chestType) {
        this.chestItems.get(chestType).clear();
    }

    /**
     * Reset the items for this game to the plugin's default items list
     */
    public void resetItemsDefault(ChestType chestType) {
        this.chestItems.put(chestType, this.randomItems.getItems(chestType));
    }

}
