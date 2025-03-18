package com.shanebeestudios.hg.util;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Util for getting {@link PotionType}
 */
public class PotionTypeUtils {

    @SuppressWarnings("NullableProblems")
    private static final Registry<PotionType> POTION_TYPES = RegistryAccess.registryAccess().getRegistry(RegistryKey.POTION);

    /**
     * Get a PotionType based on a Minecraft namespace with Bukkit key fallback
     *
     * @param key Key for PotionType (can be Minecraft namespace or Bukkit key)
     * @return PotionType (null if MC or Bukkit key does not exist)
     */
    @Nullable
    public static PotionType getByKey(String key) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(key.toLowerCase(Locale.ROOT));
        if (namespacedKey != null) {
            return POTION_TYPES.get(namespacedKey);
        }
        return null;
    }

    /**
     * Get a PotionType based on a Bukkit key
     *
     * @param bukkit Key for PotionType
     * @return PotionType
     */
    public static PotionType getByBukkit(String bukkit) {
        return PotionType.valueOf(bukkit.toUpperCase());
    }

    /**
     * Get PotionData from a String
     * <p><b>Formats:</b>
     * <br>POTION-TYPE (optional start with 'LONG_' or 'STRONG_')
     * <br>POTION-TYPE:boolean(strong):boolean(extended)</p>
     *
     * @param data data string of potion type
     * @return New PotionData if checks passed
     */
    @Nullable
    public static PotionType getPotionData(String data) {
        String[] potionData = data.split(":");
        if (potionData.length == 1) {
            PotionType potionType = getByKey(potionData[0]);
            if (potionType == null) {
                potionTypeWarning("Potion base type not found: &c" + potionData[0].toUpperCase(Locale.ROOT) + " &ein: &b" + data);
                return null;
            }
            return potionType;
        } else {
            potionTypeWarning("Improper setup of potion-data: &c" + data);
            return null;
        }
    }

    private static void potionTypeWarning(@Nullable String warning) {
        if (warning != null) Util.warning(warning);
        Util.warning("&r  - Check your configs");
        Util.warning("&r  - Proper examples:");
        Util.warning("      &bpotion-base:turtle_master");
        Util.warning("      &bpotion-base:long_turtle_master");
        Util.warning("      &bpotion-base:strong_turtle_master");
    }

}
