package com.shanebeestudios.hg.util;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generalized utility class for shortcut methods
 */
@SuppressWarnings("WeakerAccess")
public class Util {

    private static final Logger LOGGER = Bukkit.getLogger();
    public static final BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    private static final CommandSender CONSOLE = Bukkit.getConsoleSender();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Log a message to console prefixed with the plugin's name
     *
     * @param s Message to log to console
     */
    public static void log(String s) {
        scm(Bukkit.getConsoleSender(), "&7[&3&lHungerGames&7] " + s);
    }

    /**
     * Log a mini-message to console prefixed with the plugin's name
     *
     * @param format Message format
     * @param args   Arguments for message format
     */
    public static void logMini(String format, Object... args) {
        String s = String.format(format, args);
        Component mini = getMini("<grey>[<aqua>Hunger<dark_aqua>Games<grey>] " + s);
        CONSOLE.sendMessage(mini);
    }

    public static void sendPrefixedMini(CommandSender sender, String format, Object... args) {
        String s = String.format(format, args);
        Component mini = getMini("<grey>[<aqua>Hunger<dark_aqua>Games<grey>] " + s);
        sender.sendMessage(mini);
    }

    public static void sendMini(CommandSender sender, String format, Object... args) {
        if (format.isEmpty()) return;
        String s = String.format(format, args);
        Component mini = getMini(s);
        sender.sendMessage(mini);
    }

    /**
     * Log a formatted message to console prefixed with the plugin's name
     *
     * @param format  String format
     * @param objects Objects to go into format
     */
    public static void log(String format, Object... objects) {
        log(String.format(format, objects));
    }

    /**
     * Send a warning to console prefixed with the plugin's name
     *
     * @param warning Message to log to console
     */
    public static void warning(String warning) {
        if (warning.length() > 0) { // only send messages if its actually a message
            scm(Bukkit.getConsoleSender(), getColString("&7[&e&lHungerGames&7] &eWARNING: " + warning));
        }
    }

    /**
     * Send a formatted warning to console prefixed with the plugin's name
     *
     * @param format  Message format to log to console
     * @param objects Objects to go in format
     */
    public static void warning(String format, Object... objects) {
        warning(String.format(format, objects));
    }

    /**
     * Send a debug message to console
     * <p>This will only send if 'debug' is enabled in config.yml</p>
     *
     * @param debug Debug message to log
     */
    public static void debug(String debug) {
        if (Config.debug) {
            log(debug);
        }
    }

    /**
     * Send a debug exception to console
     * <p>This will only send if 'debug' is enabled in config.yml</p>
     *
     * @param exception Exception to log
     */
    public static void debug(@NotNull Exception exception) {
        if (Config.debug) {
            LOGGER.log(Level.SEVERE, getColString("&7[&e&lHungerGames&7] &cERROR: (please report to dev):"));
            LOGGER.log(Level.SEVERE, exception.toString());
            for (StackTraceElement element : exception.getStackTrace()) {
                LOGGER.log(Level.SEVERE, getColString("  &7at &c" + element));
            }
        }
    }

    /**
     * Send a colored message to a player or console
     *
     * @param sender Receiver of message
     * @param s      Message to send
     */
    public static void scm(CommandSender sender, String s) {
        sendMini(sender, s);
    }

    /**
     * Send a colored, formatted message to a player or console
     *
     * @param sender  Receiver of message
     * @param format  Formatted message to send
     * @param objects Objects in format
     */
    public static void scm(CommandSender sender, String format, Object... objects) {
        scm(sender, String.format(format, objects));
    }

    /**
     * Send a colored, prefixed message to a player or console
     *
     * @param sender  Receiver of message
     * @param message Message to send
     */
    public static void sendPrefixedMessage(CommandSender sender, String message) {
        if (message.length() > 0) { // only send messages if its actually a message
            scm(sender, HungerGames.getPlugin().getLang().prefix + message);
        }
    }

    /**
     * Send a colored, prefixed, formatted message to a player or console
     *
     * @param sender  Receiver of message
     * @param format  Formatted message to send
     * @param objects Objects in format
     */
    public static void sendPrefixedMessage(CommandSender sender, String format, Object... objects) {
        sendPrefixedMessage(sender, String.format(format, objects));
    }

    /**
     * Broadcast a message prefixed with plugin name
     *
     * @param s Message to send
     */
    public static void broadcast(String s) {
        if (s.length() > 0) { // only send messages if its actually a message
            Bukkit.broadcastMessage(getColString(HungerGames.getPlugin().getLang().prefix + " " + s));
        }
    }

    /**
     * Shortcut for adding color to a string
     *
     * @param string String including color codes
     * @return Formatted string
     */
    public static String getColString(String string) {
        if (isRunningMinecraft(1, 16)) {
            Matcher matcher = HEX_PATTERN.matcher(string);
            while (matcher.find()) {
                final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
                final String before = string.substring(0, matcher.start());
                final String after = string.substring(matcher.end());
                string = before + hexColor + after;
                matcher = HEX_PATTERN.matcher(string);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Convert text to MiniMessage
     *
     * @param text Text to convert
     * @return Component from text
     */
    public static Component getMini(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    /**
     * Convert a MiniMessage/Component to text
     *
     * @param component Component to convert
     * @return Text from component
     */
    public static String unMini(Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Check if a string is an Integer
     *
     * @param string String to get
     * @return True if string is an Integer
     */
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isBool(String string) {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
    }

    public static BlockFace getSignFace(BlockFace face) {
        switch (face) {
            case WEST:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.NORTH;
            default:
                return BlockFace.WEST;
        }
    }

    /**
     * Clear the inventory of a player including equipment
     *
     * @param player Player to clear inventory
     */
    public static void clearInv(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.updateInventory();
    }

    /**
     * Convert a list of UUIDs to a string of player names
     *
     * @param uuid UUID list to convert
     * @return String of player names
     */
    public static List<String> convertUUIDListToStringList(List<UUID> uuid) {
        List<String> winners = new ArrayList<>();
        for (UUID id : uuid) {
            winners.add(Objects.requireNonNull(Bukkit.getPlayer(id)).getName());
        }
        return winners;
    }

    public static String translateStop(List<String> win) {
        StringBuilder bc = null;
        int count = 0;
        for (String s : win) {
            count++;
            if (count == 1) bc = new StringBuilder(s);
            else if (count == win.size()) {
                assert bc != null;
                bc.append(", and ").append(s);
            } else {
                assert bc != null;
                bc.append(", ").append(s);
            }
        }
        if (bc != null)
            return bc.toString();
        else
            return "No one";
    }

    public static void shootFirework(Location l) {
        assert l.getWorld() != null;
        Firework fw = l.getWorld().spawn(l, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        List<Color> c = new ArrayList<>();
        c.add(Color.GREEN);
        c.add(Color.BLUE);
        FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(Type.BALL_LARGE).trail(true).build();
        fm.addEffect(e);
        fm.setPower(2);
        fw.setFireworkMeta(fm);
    }

    @SuppressWarnings("deprecation")
    public static boolean isAttached(Block base, Block attached) {
        if (attached.getType() == Material.AIR) return false;

        MaterialData bs = attached.getState().getData();
        //BlockData bs = attached.getBlockData();

        if (!(bs instanceof Attachable)) return false;

        Attachable at = (Attachable) bs;
        BlockFace face = at.getAttachedFace();

        return attached.getRelative(face).equals(base);
    }

    /**
     * Check if server is running a minimum Minecraft version
     *
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor) {
        return isRunningMinecraft(major, minor, 0);
    }

    /**
     * Check if server is running a minimum Minecraft version
     *
     * @param major    Major version to check (Most likely just going to be 1)
     * @param minor    Minor version to check
     * @param revision Revision to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor, int revision) {
        String[] version = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int maj = Integer.parseInt(version[0]);
        int min = Integer.parseInt(version[1]);
        int rev;
        try {
            rev = Integer.parseInt(version[2]);
        } catch (Exception ignore) {
            rev = 0;
        }
        return maj > major || min > minor || (min == minor && rev >= revision);
    }

    /**
     * Check if a material is a wall sign
     * <p>Due to sign material changes in 1.14 this method checks for both 1.13 and 1.14+</p>
     *
     * @param item Material to check
     * @return True if material is a wall sign
     */
    public static boolean isWallSign(Material item) {
        if (isRunningMinecraft(1, 14)) {
            return Tag.WALL_SIGNS.isTagged(item);
        } else {
            return item == Material.getMaterial("WALL_SIGN");
        }
    }

    /**
     * Check if a method exists
     *
     * @param c              Class that contains this method
     * @param methodName     Method to check
     * @param parameterTypes Parameter types if the method contains any
     * @return True if this method exists
     */
    public static boolean methodExists(final Class<?> c, final String methodName, final Class<?>... parameterTypes) {
        try {
            c.getDeclaredMethod(methodName, parameterTypes);
            return true;
        } catch (final NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

    /**
     * Check if a class exists
     *
     * @param className Class to check for existence
     * @return True if this class exists
     */
    public static boolean classExists(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

}
