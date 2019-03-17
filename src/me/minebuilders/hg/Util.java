package me.minebuilders.hg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

public class Util {

	public static final BlockFace[] faces = new BlockFace[] {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
	
	private static final Logger log = Logger.getLogger("Minecraft");

	public static void log(String s) { log.info("[HungerGames] " + s); }
	
	public static void warning(String s) { log.warning("[HungerGames] " + s); }

	public static boolean hp(Player p, String s) {
		if (p.hasPermission("hg." + s)) {
			return true;
		}
		return false;
	}

	public static void msg(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "HungerGames" +ChatColor.DARK_RED +"] " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', s)); 
	}
	
	public static void scm(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)); 
	}

	public static void broadcast(String s) { 
		Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "HungerGames" +ChatColor.DARK_RED +"] " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', s)); 
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) { return false; }
		return true;
	}

	public static BlockFace getSignFace(int face) {
		switch(face) {
		case 2: return BlockFace.WEST;
		case 4: return BlockFace.SOUTH;
		case 3: return BlockFace.EAST;
		case 5: return BlockFace.NORTH;
		default:
			return BlockFace.WEST;
		}
	}

	public static void clearInv(Player p) {
		p.getInventory().clear();
		p.getEquipment().clear();
		p.getInventory().setHelmet((ItemStack)null);
		p.getInventory().setChestplate((ItemStack)null);
		p.getInventory().setLeggings((ItemStack)null);
		p.getInventory().setBoots((ItemStack)null);
		p.updateInventory();
	}

	public static List<String> convertUUIDListToStringList(List<UUID> uuid) {
		List<String> winners = new ArrayList<String>();
		for (UUID id: uuid) {
			winners.add(getOnlinePlayer(id).getName());
		} return winners;
	}
	
	public static Player getOnlinePlayer(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			return player;
		} return null;
	}
	
	public static String translateStop(List<String> win) {
		String bc = null;
		int count = 0;
		for (String s : win) {
			count++;
			if (count == 1) bc = s;
			else if (count == win.size()) bc = bc + ", and " + s;
			else bc = bc + ", " + s;
		}
		return bc;
	}

	public static void shootFirework(Location l) {
		Firework fw = (Firework) l.getWorld().spawn(l, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		List<Color> c = new ArrayList<Color>();
		c.add(Color.ORANGE);
		c.add(Color.RED);
		FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(Type.BALL_LARGE).trail(true).build();
		fm.addEffect(e);
		fm.setPower(2);
		fw.setFireworkMeta(fm);
	}
	
	public static boolean isAttached(Block base, Block attached) {
		MaterialData bs = attached.getState().getData();

		if (!(bs instanceof Attachable)) return false;

		Attachable at = (Attachable) bs;
		
		if (attached.getRelative(at.getAttachedFace()).equals(base))
			return true;
		
		return false;
	}
}
