package com.shanebeestudios.hg.util;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Util for getting {@link PotionEffectType}
 */
@SuppressWarnings("unused")
public class PotionEffectUtils {

    @SuppressWarnings("NullableProblems")
    private static final Registry<PotionEffectType> POTION_EFFECT_TYPES = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);

    /**
     * Get a PotionEffectType based on a Minecraft namespace
     *
     * @param key Minecraft namespace
     * @return PotionEffectType
     */
    public static PotionEffectType getByKey(String key) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(key.toLowerCase(Locale.ROOT));
        if (namespacedKey != null) return POTION_EFFECT_TYPES.get(namespacedKey);
        return null;
    }

    /**
     * Get a PotionEffect from string
     * <p><b>Format:</b>
     * <br>POTION_EFFECT_TYPE:int(duration):int(amplifier)</p>
     *
     * @param data Data string for potion effect
     * @return New PotionEffect if checks passed
     */
    @Nullable
    public static PotionEffect getPotionEffect(String data) {
        String[] potionData = data.split(":");
        if (potionData.length == 3) {
            PotionEffectType type = getByKey(potionData[0]);
            if (type == null) {
                potionWarning("Potion effect type not found: &c" + potionData[0].toUpperCase() + " &ein: &b" + data);
                return null;
            } else if (!Util.isInt(potionData[1])) {
                potionWarning("Potion duration incorrect format: &c" + potionData[1] + " &ein: &b" + data);
                return null;
            } else if (!Util.isInt(potionData[2])) {
                potionWarning("Potion amplifier incorrect format: &c" + potionData[2] + " &ein: &b" + data);
                return null;
            }
            int duration = Integer.parseInt(potionData[1]);
            int amplifier = Integer.parseInt(potionData[2]);
            return new PotionEffect(type, duration, amplifier);
        } else {
            potionWarning("Improper setup of potion: &c" + data);
            return null;
        }
    }

    private static void potionWarning(@Nullable String warning) {
        if (warning != null) Util.warning(warning);
        Util.warning("&r  - Check your configs");
        Util.warning("&r  - Proper example:");
        Util.warning("      &bpotion:POTION_EFFECT_TYPE:DURATION_IN_TICKS:AMPLIFIER");
        Util.warning("      &bpotion:instant_health:200:1");
    }

}
