package com.shanebeestudios.hg.api.util;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;

/**
 * Utility class holding constants used around the plugin
 */
public class Constants {

    /**
     * {@link NamespacedKey Key} for {@link PersistentDataContainer PDC} of Lobby Signs
     */
    public static NamespacedKey LOBBY_SIGN_KEY = Util.getPluginKey("lobby_sign");

    /**
     * {@link NamespacedKey Key} for {@link PersistentDataContainer PDC} of tracking sticks
     */
    public static NamespacedKey TRACKING_STICK_KEY = Util.getPluginKey("tracking_stick");

}
