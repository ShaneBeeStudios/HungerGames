package tk.shanebee.hg.util;

import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Util for getting {@link PotionEffectType}
 */
@SuppressWarnings("unused")
public enum PotionEffectUtils {

    SPEED("SPEED"),
    SLOWNESS("SLOW"),
    HASTE("FAST_DIGGING"),
    MINING_FATIGUE("SLOW_DIGGING"),
    STRENGTH("INCREASE_DAMAGE"),
    INSTANT_HEALTH("HEAL"),
    INSTANT_DAMAGE("HARM"),
    JUMP_BOOST("JUMP"),
    NAUSEA("CONFUSION"),
    REGENERATION("REGENERATION"),
    RESISTANCE("DAMAGE_RESISTANCE"),
    FIRE_RESISTANCE("FIRE_RESISTANCE"),
    WATER_BREATHING("WATER_BREATHING"),
    INVISIBILITY("INVISIBILITY"),
    BLINDNESS("BLINDNESS"),
    NIGHT_VISION("NIGHT_VISION"),
    HUNGER("HUNGER"),
    WEAKNESS("WEAKNESS"),
    POISON("POISON"),
    WITHER("WITHER"),
    HEALTH_BOOST("HEALTH_BOOST"),
    ABSORPTION("ABSORPTION"),
    SATURATION("SATURATION"),
    GLOWING("GLOWING"),
    LEVITATION("LEVITATION"),
    LUCK("LUCK"),
    UNLUCK("UNLUCK"),
    // 1.13
    SLOW_FALLING("SLOW_FALLING"),
    CONDUIT_POWER("CONDUIT_POWER"),
    DOLPHINS_GRACE("DOLPHINS_GRACE"),
    // 1.14
    BAD_OMEN("BAD_OMEN"),
    HERO_OF_THE_VILLAGE("HERO_OF_THE_VILLAGE");

    private final String bukkit;
    private static final Map<String, String> BY_NAME = new HashMap<>();

    PotionEffectUtils(String bukkit) {
        this.bukkit = bukkit;
    }

    static {
        for (PotionEffectUtils p : values()) {
            BY_NAME.put(p.name(), p.bukkit);
        }
    }

    /**
     * Get a PotionEffectType based on a Minecraft namespace with Bukkit key fallback
     *
     * @param key Key for PotionEffectType (can be Minecraft namespace or Bukkit key)
     * @return PotionEffectType (null if MC or Bukkit key does not exist)
     */
    public static PotionEffectType get(String key) {
        String upper = key.toUpperCase();
        if (BY_NAME.containsKey(upper)) {
            return getByKey(upper);
        } else if (BY_NAME.containsValue(upper)) {
            return getByBukkit(upper);
        }
        Util.warning("Invalid potion effect type: &7" + upper);
        return null;
    }

    /**
     * Get a PotionEffectType based on a Minecraft namespace
     *
     * @param key Minecraft namespace
     * @return PotionEffectType
     */
    public static PotionEffectType getByKey(String key) {
        return getByBukkit(valueOf(key).bukkit);
    }

    /**
     * Get a PotionEffectType based on a Bukkit key
     *
     * @param bukkit Key for PotionEffectType
     * @return PotionEffectType
     */
    public static PotionEffectType getByBukkit(String bukkit) {
        return PotionEffectType.getByName(bukkit.toUpperCase());
    }

    /**
     * Get Bukkit key for PotionEffectType
     *
     * @return Bukkit key
     */
    public String getBukkitKey() {
        return bukkit;
    }

}
