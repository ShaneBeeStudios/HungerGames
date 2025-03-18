package com.shanebeestudios.hg.game;

import org.bukkit.inventory.ItemStack;
import com.shanebeestudios.hg.HungerGames;

import java.util.Map;

/**
 * Data class for holding a {@link Game Game's} items
 */
@SuppressWarnings("unused")
public class GameItemData extends Data {

    private Map<Integer, ItemStack> items;
    private Map<Integer, ItemStack> bonusItems;

    protected GameItemData(Game game) {
        super(game);
        // Set default items from items.yml (if arenas.yml has items it will override this)
        this.items = plugin.getItems();
        this.bonusItems = plugin.getBonusItems();
    }

    /**
     * Set the items for this game
     *
     * @param items Map of items to set
     */
    public void setItems(Map<Integer, ItemStack> items) {
        this.items = items;
    }

    /**
     * Get the items map for this game
     *
     * @return Map of items
     */
    public Map<Integer, ItemStack> getItems() {
        return this.items;
    }

    /**
     * Add an item to the items map for this game
     *
     * @param item ItemStack to add
     */
    public void addToItems(ItemStack item) {
        this.items.put(this.items.size() + 1, item.clone());
    }

    /**
     * Clear the items for this game
     */
    public void clearItems() {
        this.items.clear();
    }

    /**
     * Reset the items for this game to the plugin's default items list
     */
    public void resetItemsDefault() {
        this.items = HungerGames.getPlugin().getItems();
    }

    /**
     * Set the bonus items for this game to a new map
     *
     * @param items Map of bonus items
     */
    public void setBonusItems(Map<Integer, ItemStack> items) {
        this.bonusItems = items;
    }

    /**
     * Get the bonus items map for this game
     *
     * @return Map of bonus items
     */
    public Map<Integer, ItemStack> getBonusItems() {
        return this.bonusItems;
    }

    /**
     * Add an item to this game's bonus items
     *
     * @param item ItemStack to add to bonus items
     */
    public void addToBonusItems(ItemStack item) {
        this.bonusItems.put(this.bonusItems.size() + 1, item.clone());
    }

    /**
     * Clear this game's bonus items
     */
    public void clearBonusItems() {
        this.bonusItems.clear();
    }

    /**
     * Reset the bonus items for this game to the plugin's default bonus items list
     */
    public void resetBonusItemsDefault() {
        this.bonusItems = HungerGames.getPlugin().getBonusItems();
    }

}
