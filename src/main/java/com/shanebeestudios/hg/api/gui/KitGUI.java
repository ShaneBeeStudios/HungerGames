package com.shanebeestudios.hg.api.gui;

import com.shanebeestudios.hg.api.data.KitEntry;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class KitGUI implements InventoryHolder {

    private final Language lang;
    private final KitsGUI kitsGUI;
    private final KitEntry kitEntry;
    private final Player player;
    private final Inventory inventory;

    public KitGUI(KitsGUI kitsGUI, Player player, KitEntry kitEntry) {
        this.lang = HungerGames.getPlugin().getLang();
        this.kitsGUI = kitsGUI;
        this.kitEntry = kitEntry;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54,
            Util.getMini(this.lang.kits_kit_gui_title.replace("<name>", kitEntry.getName())));

        // SETUP INVENTORY
        // divider
        ItemStack divider = ItemType.BLACK_STAINED_GLASS_PANE.createItemStack();
        if (Util.RUNNING_1_21_5) {
            divider.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
                .hideTooltip(true));
        } else {
            divider.setData(DataComponentTypes.CUSTOM_NAME, Component.text(" "));
        }
        for (int i = 9; i < 18; i++) {
            this.inventory.setItem(i, divider);
        }

        // exit button
        ItemStack exit = ItemType.RED_CONCRETE.createItemStack();
        exit.setData(DataComponentTypes.ITEM_NAME, Util.getMini(this.lang.kits_kit_gui_exit));
        this.inventory.setItem(8, exit);

        // apply kit button
        ItemStack apply = ItemType.GREEN_CONCRETE.createItemStack();
        apply.setData(DataComponentTypes.ITEM_NAME, Util.getMini(this.lang.kits_kit_gui_apply));
        this.inventory.setItem(7, apply);

        // Helmet
        ItemStack helmet = kitEntry.getHelmet();
        if (helmet == null) {
            helmet = ItemType.BARRIER.createItemStack();
            helmet.setData(DataComponentTypes.ITEM_NAME, Util.getMini(this.lang.kits_kit_gui_no_helmet));
        }
        this.inventory.setItem(0, helmet);

        // Chestplate
        ItemStack chestplate = kitEntry.getChestplate();
        if (chestplate == null) {
            chestplate = ItemType.BARRIER.createItemStack();
            chestplate.setData(DataComponentTypes.ITEM_NAME, Util.getMini(this.lang.kits_kit_gui_no_chestplate));
        }
        this.inventory.setItem(1, chestplate);

        // Leggings
        ItemStack leggings = kitEntry.getLeggings();
        if (leggings == null) {
            leggings = ItemType.BARRIER.createItemStack();
            leggings.setData(DataComponentTypes.ITEM_NAME, Util.getMini(this.lang.kits_kit_gui_no_leggings));
        }
        this.inventory.setItem(2, leggings);

        // Boots
        ItemStack boots = kitEntry.getBoots();
        if (boots == null) {
            boots = ItemType.BARRIER.createItemStack();
            boots.setData(DataComponentTypes.ITEM_NAME, Util.getMini(this.lang.kits_kit_gui_no_boots));
        }
        this.inventory.setItem(3, boots);

        // Potions
        ItemStack potion = ItemType.POTION.createItemStack();
        potion.setData(DataComponentTypes.CUSTOM_NAME, Util.getMini(this.lang.kits_kit_gui_potion_effects));
        if (Util.RUNNING_1_21_5) {
            potion.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
                .hideTooltip(true));
        } else {
            //noinspection deprecation
            potion.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        }

        List<PotionEffect> potionEffects = kitEntry.getPotionEffects();
        List<Component> lore = new ArrayList<>();
        if (potionEffects.isEmpty()) {
            lore.add(Util.getMini(this.lang.kits_kit_gui_potion_effect_none));
        } else {
            potionEffects.forEach(effect -> lore.add(Util.getMini(this.lang.kits_kit_gui_potion_effect_lore
                .replace("<type>", effect.getType().translationKey()))));
        }
        potion.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        this.inventory.setItem(5, potion);

        // Items
        int slot = 18;
        for (ItemStack item : kitEntry.getInventoryContents()) {
            this.inventory.setItem(slot, item);
            slot++;
        }
    }

    public void open() {
        this.player.openInventory(this.inventory);
    }

    public void click(int slot) {
        // APPLY
        if (slot == 7) {
            this.kitsGUI.getKitData().setKit(this.player, this.kitEntry.getName());
            this.player.closeInventory();
        }
        // EXIT
        else if (slot == 8) {
            this.kitsGUI.open();
        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

}
