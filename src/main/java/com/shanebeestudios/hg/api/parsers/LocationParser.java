package com.shanebeestudios.hg.api.parsers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for {@link Location Locations}
 */
@Deprecated(forRemoval = true, since = "INSERT VERSION")
public class LocationParser {

    /**
     * Deserialize a block location from a string
     *
     * @param stringLocation String to deserialize
     * @return Block location from string
     */
    public static Location getBlockLocFromString(String stringLocation) {
        String[] split = stringLocation.split(":");
        return new Location(Bukkit.getServer().getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    /**
     * Deserialize a location from a string
     *
     * @param stringLocation String to deserialize
     * @return Location from string
     */
    public static @Nullable Location getLocFromString(@Nullable String stringLocation) {
        if (stringLocation == null) return null;
        String[] split = stringLocation.split(":");
        if (split.length < 4) return null;

        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = 0;
        float pitch = 0;
        if (split.length >= 5) {
            yaw = Float.parseFloat(split[4]);
        }
        if (split.length == 6) {
            pitch = Float.parseFloat(split[5]);
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

}
