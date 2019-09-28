package tk.shanebee.hg.util;

import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import tk.shanebee.hg.HG;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Generalized utility class for shortcut methods
 */
@SuppressWarnings("WeakerAccess")
public class Util {

	public static final BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

	/** Log a message to console prefixed with the plugin's name
	 * @param s Message to log to console
	 */
	public static void log(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3&lHungerGames&7] " + s));
	}

	/** Send a warning to console prefixed with the plugin's name
	 * @param s Message to log to console
	 */
	public static void warning(String s) {
		String warnPrefix = "&7[&e&lHungerGames&7] ";
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
				warnPrefix + "&eWARNING: " + s));
	}

	/** Send a colored message to a player or console
	 * @param sender Receiver of message
	 * @param s Message to send
	 * @deprecated Use {@link #scm(CommandSender, String)} instead
	 */
	@Deprecated
	public static void msg(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	}

	/** Send a colored message to a player or console
	 * @param sender Receiver of message
	 * @param s Message to send
	 */
	public static void scm(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	}

	/** Broadcast a message prefixed with plugin name
	 * @param s Message to send
	 */
	public static void broadcast(String s) {
		Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', HG.getPlugin().getLang().prefix + " " + s));
	}

	/** Shortcut for adding color to a string
	 * @param string String including color codes
	 * @return Formatted string
	 */
	public static String getColString(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

    /** Check if a string is an Integer
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

	/** Clear the inventory of a player including equipment
	 * @param player Player to clear inventory
	 */
	public static void clearInv(Player player) {
		player.getInventory().clear();
		player.getEquipment().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.updateInventory();
	}

	/** Convert a list of UUIDs to a string of player names
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
		MaterialData bs = attached.getState().getData();
		//BlockData bs = attached.getBlockData();

		if (!(bs instanceof Attachable)) return false;

		Attachable at = (Attachable) bs;

		return attached.getRelative(at.getAttachedFace()).equals(base);
	}

    /** Check if running a specific version of Minecraft or higher.
     * @param major Major version of Minecraft to check (Will most likely always be 1)
     * @param minor Minor version of Minecraft to check
     * @return True if the server is running this version or higher
     */
	@SuppressWarnings("SameParameterValue")
	public static boolean isRunningMinecraft(int major, int minor) {
		int maj = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[0].replace("v", ""));
		int min = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]);
		return maj >= major && min >= minor;
	}

	/** Check if a material is a wall sign
	 * <p>Due to sign material changes in 1.14 this method checks for both 1.13 and 1.14+</p>
	 * @param item Material to check
	 * @return True if material is a wall sign
	 */
	public static boolean isWallSign(Material item) {
		if (isRunningMinecraft(1, 14)) {
			switch (item) {
				case ACACIA_WALL_SIGN:
				case BIRCH_WALL_SIGN:
				case DARK_OAK_WALL_SIGN:
				case JUNGLE_WALL_SIGN:
				case OAK_WALL_SIGN:
				case SPRUCE_WALL_SIGN:
					return true;
			}
		} else {
			return item == Material.getMaterial("WALL_SIGN");
		}
		return false;
	}

    /** Check if a material is a wall sign
     * <p>Due to sign material changes in 1.14 this method checks for both 1.13 and 1.14+</p>
     * @param block Block to check
     * @return True if block is a wall sign
     */
	public static boolean isWallSign(Block block) {
	    return isWallSign(block.getType());
    }

}
