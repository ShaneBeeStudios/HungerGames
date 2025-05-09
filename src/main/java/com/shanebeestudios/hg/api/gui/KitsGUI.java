package com.shanebeestudios.hg.api.gui;

import com.shanebeestudios.hg.api.data.KitData;
import com.shanebeestudios.hg.api.data.KitEntry;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.registry.Registries;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public class KitsGUI implements InventoryHolder {

    private final Random random = new Random();
    private final KitData kitData;
    private final Player player;
    private final List<ItemType> shulkerBoxes = new ArrayList<>();
    private final Map<Integer, KitEntry> kitEntries = new HashMap<>();
    private final Inventory inventory;

    public KitsGUI(Game game, Player player) {
        this.player = player;
        this.kitData = game.getGameItemData().getKitData();

        // Setup shulker boxes
        Registry<ItemType> reg = Registries.ITEM_TYPE_REGISTRY;
        Tag<ItemType> tag = reg.getTag(ItemTypeTagKeys.SHULKER_BOXES);
        for (TypedKey<ItemType> typedKey : tag) {
            ItemType itemType = reg.get(typedKey);
            this.shulkerBoxes.add(itemType);
        }
        // Setup inventory
        int size = ((this.kitData.getKitEntries().size() / 9) + 1) * 9;
        Language lang = HungerGames.getPlugin().getLang();
        this.inventory = Bukkit.createInventory(this, size, Util.getMini(lang.kits_kits_gui_title));
        int slot = 0;
        for (String kitName : this.kitData.getKitEntries().keySet()) {
            KitEntry kitEntry = this.kitData.getKitEntry(kitName);
            if (!kitEntry.hasKitPermission(player)) continue;

            this.kitEntries.put(slot, kitEntry);
            ItemStack itemStack = getRandomShulkerBox();
            itemStack.setData(DataComponentTypes.ITEM_NAME, Util.getMini(kitName));
            this.inventory.setItem(slot, itemStack);
            slot++;
        }
    }

    private ItemStack getRandomShulkerBox() {
        ItemType itemType = this.shulkerBoxes.get(this.random.nextInt(this.shulkerBoxes.size()));
        return itemType.createItemStack();
    }

    public void open() {
        this.player.openInventory(this.inventory);
    }

    public void click(int slot) {
        KitEntry kitEntry = this.kitEntries.get(slot);
        if (kitEntry == null) return;

        new KitGUI(this, this.player, kitEntry).open();
    }

    public KitData getKitData() {
        return this.kitData;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}
