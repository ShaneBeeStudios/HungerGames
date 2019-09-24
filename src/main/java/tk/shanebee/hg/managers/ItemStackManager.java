package tk.shanebee.hg.managers;

import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.data.KitEntry;
import tk.shanebee.hg.util.NBTApi;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemStackManager {

	private HG plugin;
	private NBTApi nbtApi;

	public ItemStackManager(HG p) {
		this.plugin = p;
		setKits();
		this.nbtApi = p.getNbtApi();
	}

	public void setKits() {
		kitCreator(plugin.getConfig(), plugin.getKitManager(), null);
		Util.log("Loaded kits");
	}

	public KitManager setGameKits(String gameName, Configuration config) {
		String gamePath = "arenas." + gameName + ".";
		KitManager kit = new KitManager();
		if (config.getConfigurationSection(gamePath + "kits") == null) return null;
		kitCreator(config, kit, gamePath);
		Util.log("Loaded custom kits for arena: " + gameName);
		return kit;
	}

	@SuppressWarnings("ConstantConditions")
	private void kitCreator(Configuration config, KitManager kit, String gameName) {
		if (gameName == null) gameName = "";
		for (String path : config.getConfigurationSection(gameName + "kits").getKeys(false)) {
			try {
				ArrayList<ItemStack> stack = new ArrayList<>();
				ArrayList<PotionEffect> potions = new ArrayList<>();
				String perm = null;

				for (String item : config.getStringList(gameName + "kits." + path + ".items"))
					stack.add(getItem(item, true));

				for (String pot : config.getStringList(gameName + "kits." + path + ".potion-effects")) {
					String[] poti = pot.split(":");
					PotionEffectType e = PotionEffectType.getByName(poti[0]);
					if (poti[2].equalsIgnoreCase("forever")) {
						assert e != null;
						potions.add(e.createEffect(2147483647, Integer.parseInt(poti[1])));
					} else {
						int dur = Integer.parseInt(poti[2]) * 20;
						assert e != null;
						potions.add(e.createEffect(dur, Integer.parseInt(poti[1])));
					}
				}

				ItemStack helm = getItem(config.getString(gameName + "kits." + path + ".helmet"), false);
				ItemStack ches = getItem(config.getString(gameName + "kits." + path + ".chestplate"), false);
				ItemStack leg = getItem(config.getString(gameName + "kits." + path + ".leggings"), false);
				ItemStack boot = getItem(config.getString(gameName + "kits." + path + ".boots"), false);

				if (config.getString(gameName + "kits." + path + ".permission") != null
						&& !config.getString(gameName + "kits." + path + ".permission").equals("none"))
					perm = config.getString(gameName + "kits." + path + ".permission");

				kit.addKit(path, new KitEntry(stack.toArray(new ItemStack[0]), helm, boot, ches, leg, perm, potions));
			} catch (Exception e) {
				Util.log("-------------------------------------------");
				Util.log("WARNING: Unable to load kit " + gameName + path + "!");
				Util.log("-------------------------------------------");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItem(String args, boolean isItem) {
		if (args == null) return null;
		int amount = 1;
		if (isItem) {
			String a = args.split(" ")[1];
			if (Util.isInt(a)) {
				amount = Integer.parseInt(a);
			}
		}
		ItemStack item = itemStringToStack(args.split(" ")[0], amount);
		String[] ags = args.split(" ");
		for (String s : ags) {
			if (s.startsWith("enchant:")) {
				s = s.replace("enchant:", "").toUpperCase();
				String[] d = s.split(":");
				int level = 1;
				if (d.length != 1 && Util.isInt(d[1])) {
					level = Integer.parseInt(d[1]);
				}
				for (Enchantment e : Enchantment.values()) {
					if (e.getKey().getKey().equalsIgnoreCase(d[0]) || e.getName().equalsIgnoreCase(d[0])) {
						assert item != null;
						item.addUnsafeEnchantment(e, level);
					}
				}
			} else if (s.startsWith("color:")) {
				try {
					s = s.replace("color:", "").toUpperCase();
					for (DyeColor c : DyeColor.values()) {
						if (c.name().equalsIgnoreCase(s)) {
							assert item != null;
							LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
							assert lam != null;
							lam.setColor(c.getColor());
							item.setItemMeta(lam);
						}
					}
				} catch (Exception ignore) {
				}
				try {
					s = s.replace("color:", "");
					assert item != null;
					PotionMeta meta = ((PotionMeta) item.getItemMeta());
					assert meta != null;
					meta.setColor(Color.fromRGB(Integer.parseInt(s)));
					item.setItemMeta(meta);
				} catch (Exception ignore) {
				}
			} else if (s.startsWith("name:")) {
				s = s.replace("name:", "").replace("_", " ");
				s = ChatColor.translateAlternateColorCodes('&', s);
				assert item != null;
				ItemMeta im = item.getItemMeta();
				assert im != null;
				im.setDisplayName(s);
				item.setItemMeta(im);
			} else if (s.startsWith("lore:")) {
				s = s.replace("lore:", "").replace("_", " ");
				s = ChatColor.translateAlternateColorCodes('&', s);
				assert item != null;
				ItemMeta meta = item.getItemMeta();
				ArrayList<String> lore = new ArrayList<>(Arrays.asList(s.split(":")));
				assert meta != null;
				meta.setLore(lore);
				item.setItemMeta(meta);
			} else if (s.startsWith("data:")) {
				s = s.replace("data:", "").replace("~", " ");
				assert item != null;
				if (nbtApi != null)
					item = nbtApi.getItemWithNBT(item, s);
			} else if (s.startsWith("ownerName:")) {
				s = s.replace("ownerName:", "");
				assert item != null;
				if (item.getType().equals(Material.PLAYER_HEAD)) {
					ItemMeta meta = item.getItemMeta();
					assert meta != null;
					((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(s));
					item.setItemMeta(meta);
				}
			}
		}
		return item;
	}

	private ItemStack itemStringToStack(String item, int amount) {
		String[] itemArr = item.split(":");
		if (itemArr[0].equalsIgnoreCase("potion") || itemArr[0].equalsIgnoreCase("splash_potion")) {
			boolean splash = itemArr[0].equalsIgnoreCase("splash_potion");
			if (PotionEffectType.getByName(itemArr[1].toUpperCase()) == null) {
				Util.warning("Potion effect type not found: " + ChatColor.RED + itemArr[1].toUpperCase());
				Util.log("  - Check your configs");
				Util.log("  - Proper example:");
				Util.log("      &bPOTION:POTION_TYPE:DURATION_IN_TICKS:LEVEL");
				Util.log("      &bPOTION:HEAL:200:1");
				return null;
			}
			if (itemArr.length != 4) {
				Util.warning("Improper setup of potion: &c" + item);
				Util.log("  - Check your configs for missing arguments");
				Util.log("  - Proper example:");
				Util.log("      &bPOTION:POTION_TYPE:DURATION_IN_TICKS:LEVEL");
				Util.log("      &bPOTION:HEAL:200:1");
				return null;
			}
			PotionEffectType potType = PotionEffectType.getByName(itemArr[1].toUpperCase());
			int duration = Integer.parseInt(itemArr[2]);
			int amplifier = Integer.parseInt(itemArr[3]);
			ItemStack potion = new ItemStack(splash ? Material.SPLASH_POTION : Material.POTION, amount);
			PotionMeta meta = ((PotionMeta) potion.getItemMeta());
			assert meta != null;
			assert potType != null;
			meta.addCustomEffect(new PotionEffect(potType, duration, amplifier), true);
			potion.setItemMeta(meta);
			return potion;
		}
		return new ItemStack(Material.valueOf(itemArr[0].toUpperCase()), amount);
	}

}
