package com.shanebeestudios.hg.api.util;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.data.Language;
import com.shanebeestudios.hg.plugin.configs.Config;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ItemUtils {

    private static final Language LANG = HungerGames.getPlugin().getLang();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isCursed(ItemStack itemStack) {
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if (enchantment.isCursed()) return true;
        }
        return false;
    }

    public static ItemStack getTrackingStick() {
        ItemStack itemStack = ItemType.STICK.createItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(Constants.TRACKING_STICK_KEY, PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(itemMeta);

        itemStack.setData(DataComponentTypes.ITEM_NAME, Util.getMini(LANG.tracking_stick_name));
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        itemStack.setData(DataComponentTypes.MAX_DAMAGE, Config.TRACKING_STICK_USES);
        itemStack.setData(DataComponentTypes.DAMAGE, 0);
        List<Component> lore = new ArrayList<>();
        LANG.tracking_stick_lore.forEach(line -> lore.add(Util.getMini(line)));
        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        return itemStack;
    }

    public static boolean isTrackingStick(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        PersistentDataContainerView pdc = itemStack.getPersistentDataContainer();
        return pdc.has(Constants.TRACKING_STICK_KEY);
    }

}
