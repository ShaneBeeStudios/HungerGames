package tk.shanebee.hg.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Util class for managing {@link Enchantment Enchantments}
 */
@SuppressWarnings("deprecation")
public class EnchantmentUtils {

    private static final Map<String, Enchantment> ENCHANTMENT_MAP = new HashMap<>();

    static {
        for (Enchantment enchantment : Enchantment.values()) {
            String name = enchantment.getName();
            // Some plugins add custom enchants and dont include a name
            //noinspection ConstantConditions
            if (name != null) {
                name = name.toLowerCase(Locale.ROOT);
                if (!ENCHANTMENT_MAP.containsKey(name)) {
                    ENCHANTMENT_MAP.put(name, enchantment);
                }
            }
            String key = enchantment.getKey().getKey();
            if (!ENCHANTMENT_MAP.containsKey(key)) {
                ENCHANTMENT_MAP.put(key, enchantment);
            }
        }
    }

    /**
     * Apply an enchantment to an {@link ItemMeta}
     *
     * @param itemMeta Meta to apply enchant to
     * @param enchName Enchant format = 'enchantment_name:level'
     * @return True if enchant is available and applied, else false
     */
    public static boolean addEnchant(@NotNull ItemMeta itemMeta, @NotNull String enchName) {
        String ench = enchName.toLowerCase(Locale.ROOT);
        String[] d = ench.split(":");
        int level = 1;
        if (d.length != 1 && Util.isInt(d[1])) {
            level = Integer.parseInt(d[1]);
        }
        String name = d[0];
        if (ENCHANTMENT_MAP.containsKey(name)) {
            Enchantment enchantment = ENCHANTMENT_MAP.get(name);
            itemMeta.addEnchant(enchantment, level, true);
            return true;
        }
        return false;
    }

}
