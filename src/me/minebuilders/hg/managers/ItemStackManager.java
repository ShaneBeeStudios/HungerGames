package me.minebuilders.hg.managers;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import me.minebuilders.hg.data.KitEntry;

public class ItemStackManager {

	private HG plugin;

	public ItemStackManager(HG p) {
		this.plugin = p;
		setkits();
	}

	public void setkits() {
		Configuration config = plugin.getConfig();
		for (String path : config.getConfigurationSection("kits").getKeys(false)) {
			try {
				ArrayList<ItemStack> stack = new ArrayList<ItemStack>();
				ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>();
				String perm = null;

				for(String item : config.getStringList("kits." + path + ".items"))
					stack.add(getItem(item, true));

				for (String pot : plugin.getConfig().getStringList("kits." + path + ".potion-effects")) {
					String[] poti = pot.split("\\.");
					PotionEffectType e = PotionEffectType.getByName(poti[0]);
					if (poti[2].equalsIgnoreCase("forever")) {
						potions.add(e.createEffect(2147483647, Integer.parseInt(poti[1])));
					} else {
						Integer dur = Integer.valueOf(Integer.parseInt(poti[2]) * 20);
						potions.add(e.createEffect(dur.intValue(), Integer.parseInt(poti[1])));
					}
				}

				ItemStack helm = getItem(config.getString("kits." + path + ".helmet"), false);
				ItemStack ches = getItem(config.getString("kits." + path + ".chestplate"), false);
				ItemStack leg = getItem(config.getString("kits." + path + ".leggings"), false);
				ItemStack boot = getItem(config.getString("kits." + path + ".boots"), false);

				if (plugin.getConfig().getString("kits." + path + ".permission") != null && !plugin.getConfig().getString("kits." + path + ".permission").equals("none"))
					perm = plugin.getConfig().getString("kits." + path + ".permission");

				plugin.kit.kititems.put(path, new KitEntry(stack.toArray(new ItemStack[0]), helm, boot, ches, leg, perm, potions));
			} catch (Exception e) {
				Util.log("-------------------------------------------");
				Util.log("WARNING: Unable to load kit " + path + "!");
				Util.log("-------------------------------------------");
			}
		}
	}

	public ItemStack getItem(String args, boolean isItem) {
		int amount = 1;
		if (isItem) {
			String a = args.split(" ")[1];
			if (Util.isInt(a)) {
				amount = Integer.parseInt(a);
			}
		}
		ItemStack item = itemStringToStack(args.split(" ")[0], amount);
		String[] ags = args.split(" ");
		for (String s :ags) {
			if (s.startsWith("enchant:")) {
				s = s.replace("enchant:", "").toUpperCase();
				String[] d = s.split(":");
				int level = 1;
				if (d.length != 1 && Util.isInt(d[1])) {
					level = Integer.parseInt(d[1]);
				}
				for (Enchantment e : Enchantment.values()) {
					if (e.getName().equalsIgnoreCase(d[0]))
						item.addUnsafeEnchantment(e, level);
				}
			} else if (s.startsWith("color:")) {
				try {
					s = s.replace("color:", "").toUpperCase();
					for (DyeColor c : DyeColor.values()) {
						if (c.name().equalsIgnoreCase(s)) {
							LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
							lam.setColor(c.getColor());
							item.setItemMeta(lam);
						}
					}
				} catch (Exception ignore) {}
			} else if (s.startsWith("name:")) {
				s = s.replace("name:", "").replace("_", " ");
				s = ChatColor.translateAlternateColorCodes('&', s);
				ItemMeta im = item.getItemMeta();
				im.setDisplayName(s);
				item.setItemMeta(im);
			} else if (s.startsWith("lore:")) {
				s = s.replace("lore:", "").replace("_", " ");
				s = ChatColor.translateAlternateColorCodes('&', s);
				ItemMeta meta = item.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				for (String w : s.split(":")) 
					lore.add(w);
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
		}
		return item;
	}

	private ItemStack itemStringToStack(String item, int amount) {
		String[] itemArr = item.split(":");
		return new ItemStack(Material.valueOf(itemArr[0].toUpperCase()), amount);
	}
}
