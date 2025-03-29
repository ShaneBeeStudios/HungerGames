package com.shanebeestudios.hg.api.util;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generalized utility class for shortcut methods
 */
@SuppressWarnings("WeakerAccess")
public class Util {

    public static final BlockFace[] BLOCK_FACES = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    private static final CommandSender CONSOLE = Bukkit.getConsoleSender();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static String PREFIX;

    private static String getPrefix() {
        if (PREFIX == null) {
            Language lang = HungerGames.getPlugin().getLang();
            if (lang != null) {
                PREFIX = lang.prefix;
            } else {
                return "<grey>[<aqua>Hunger<dark_aqua>Games<grey>] ";
            }
        }
        return PREFIX;
    }

    /**
     * Log a message to console prefixed with the plugin's name
     *
     * @param format Message format
     * @param args   Arguments for message format
     */
    public static void log(String format, Object... args) {
        String s = String.format(format, args);
        Component mini = getMini("<grey>[<aqua>Hunger<dark_aqua>Games<grey>] " + s);
        CONSOLE.sendMessage(mini);
    }

    /**
     * Send a prefixed message to a command sender
     *
     * @param sender Receiver of message
     * @param format Message format
     * @param args   Objects to replace in format
     */
    public static void sendPrefixedMessage(CommandSender sender, String format, Object... args) {
        String s = String.format(format, args);
        Component mini = getMini(getPrefix() + s);
        sender.sendMessage(mini);
    }

    /**
     * Send a message to a command sender
     *
     * @param sender Receiver of message
     * @param format Message format
     * @param args   Objects to replace in format
     */
    public static void sendMessage(CommandSender sender, String format, Object... args) {
        if (format.isEmpty()) return;
        String s = String.format(format, args);
        Component mini = getMini(s);
        sender.sendMessage(mini);
    }

    /**
     * Send a formatted warning to console prefixed with the plugin's name
     *
     * @param format  Message format to log to console
     * @param objects Objects to go in format
     */
    public static void warning(String format, Object... objects) {
        if (!format.isEmpty()) { // only send messages if it's actually a message
            sendMessage(CONSOLE, "<grey>[<yellow><bold>HungerGames<grey>] <yellow>WARNING: " + format, objects);
        }
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
            log("<red>ERROR: (please report to dev):");
            CONSOLE.sendMessage(getMini("<yellow>%s", exception.getMessage()));
            for (StackTraceElement element : exception.getStackTrace()) {
                CONSOLE.sendMessage(getMini("  <grey>at <red>%s", element));
            }
        }
    }

    /**
     * Broadcast a message prefixed with plugin name
     *
     * @param s Message to send
     */
    public static void broadcast(String s) {
        if (!s.isEmpty()) { // only send messages if it's actually a message
            Bukkit.broadcast(getMini(getPrefix() + " " + s));
        }
    }

    /**
     * Shortcut for adding color to a string
     *
     * @param string String including color codes
     * @return Formatted string
     */
    @Deprecated(forRemoval = true)
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
     * @param format  Text to format
     * @param objects Objects for format
     * @return Component from text
     */
    public static Component getMini(String format, Object... objects) {
        return MINI_MESSAGE.deserialize(String.format(format, objects));
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

    public static BlockFace getSignFace(BlockFace face) {
        return switch (face) {
            case WEST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.EAST;
            case EAST -> BlockFace.NORTH;
            default -> BlockFace.WEST;
        };
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
     * @return List of player names
     */
    public static List<String> convertUUIDListToStringList(List<UUID> uuid) {
        List<String> winners = new ArrayList<>();
        for (UUID id : uuid) {
            winners.add(Objects.requireNonNull(Bukkit.getPlayer(id)).getName());
        }
        return winners;
    }

    public static String translateStop(List<String> win) {
        StringBuilder builder = null;
        int count = 0;
        for (String s : win) {
            count++;
            if (count == 1) builder = new StringBuilder(s);
            else if (count == win.size()) {
                builder.append(", and ").append(s);
            } else {
                builder.append(", ").append(s);
            }
        }
        if (builder != null)
            return builder.toString();
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

    public static boolean isAttached(Block base, Block attached) {
//        if (attached.getType() == Material.AIR) return false;
//
//        MaterialData bs = attached.getState().getData();
//        //BlockData bs = attached.getBlockData();
//
//        if (!(bs instanceof Attachable)) return false;
//
//        Attachable at = (Attachable) bs;
//        BlockFace face = at.getAttachedFace();
//
//        return attached.getRelative(face).equals(base);
        // TODO figure out a replacement
        return true;
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
     * Check if a method exists
     *
     * @param c              Class that contains this method
     * @param methodName     Method to check
     * @param parameterTypes Parameter types if the method contains any
     * @return True if this method exists
     */
    @SuppressWarnings("unused")
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
