package com.shanebeestudios.hg.api.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isCursed(ItemStack itemStack) {
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if (enchantment.isCursed()) return true;
        }
        return false;
    }

}
