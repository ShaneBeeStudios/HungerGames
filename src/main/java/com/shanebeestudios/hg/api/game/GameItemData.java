package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.api.data.ItemData;
import com.shanebeestudios.hg.api.data.KitData;

/**
 * Data holder for a {@link Game Game's} items
 */
@SuppressWarnings("unused")
public class GameItemData extends Data {

    private ItemData itemData;
    private KitData kitData;

    protected GameItemData(Game game) {
        super(game);
        // Set default items from items.yml (if arenas.yml has items it will override this)
        this.itemData = getPlugin().getItemManager().getDefaultItemData();
        // Set default kits from kits.yml (if arenas.yml has kits it will override this)
        this.kitData = getPlugin().getKitManager().getDefaultKitData();
    }

    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

    public ItemData getItemData() {
        return this.itemData;
    }

    public void setKitData(KitData kitData) {
        this.kitData = kitData;
    }

    public KitData getKitData() {
        return this.kitData;
    }

    /**
     * Clear preselected kits after game stops
     */
    public void postGameReset() {
        this.kitData.clearPreselectedKits();
    }

}
