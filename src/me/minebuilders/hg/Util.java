package me.minebuilders.hg;

import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Attachable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Util {

	private static String prefix = "&7[&3&lHungerGames&7] ";

	static final BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

	public static void log(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + s));
	}

	public static void warning(String s) {
		String warnPrefix = "&7[&e&lHungerGames&7] ";
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
				warnPrefix + "&eWARNING: " + s));
	}

	public static void msg(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	}

	public static void scm(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	}

	public static void broadcast(String s) {
		Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + s));
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	static BlockFace getSignFace(int face) {
		switch (face) {
			case 2:
				return BlockFace.WEST;
			case 4:
				return BlockFace.SOUTH;
			case 3:
				return BlockFace.EAST;
			case 5:
				return BlockFace.NORTH;
			default:
				return BlockFace.WEST;
		}
	}

	public static void clearInv(Player p) {
		p.getInventory().clear();
		p.getEquipment().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.updateInventory();
	}

	public static List<String> convertUUIDListToStringList(List<UUID> uuid) {
		List<String> winners = new ArrayList<>();
		for (UUID id : uuid) {
			winners.add(Objects.requireNonNull(getOnlinePlayer(id)).getName());
		}
		return winners;
	}

	private static Player getOnlinePlayer(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			return player;
		}
		return null;
	}

	static String translateStop(List<String> win) {
		StringBuilder bc = null;
		int count = 0;
		for (String s : win) {
			count++;
			if (count == 1) bc = new StringBuilder(s);
			else if (count == win.size()) {
				assert bc != null;
				bc.append(", and ").append(s);
			}
			else {
				assert bc != null;
				bc.append(", ").append(s);
			}
		}
		assert bc != null;
		return bc.toString();
	}

	static void shootFirework(Location l) {
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

	static boolean isAttached(Block base, Block attached) {
		//MaterialData bs = attached.getState().getData();
		BlockData bs = attached.getBlockData();

		if (!(bs instanceof Attachable)) return false;

		Attachable at = (Attachable) bs;

		return attached.getRelative(at.getAttachedFace()).equals(base);

	}
}
