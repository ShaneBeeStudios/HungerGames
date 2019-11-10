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

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manage item stacks for kits and chests
 */
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

    /** Set the kits for a game from a config
     * @param gameName The game to set the kits for
     * @param config Config the kit is pulled from
     * @return New KitManager for a game
     */
	public KitManager setGameKits(String gameName, Configuration config) {
		String gamePath = "arenas." + gameName + ".";
		KitManager kit = new KitManager();
		if (config.getConfigurationSection(gamePath + "kits") == null) return null;
		kitCreator(config, kit, gamePath);
		Util.log("Loaded custom kits for arena: " + gameName);
		return kit;
	}

	@SuppressWarnings("ConstantConditions")
	private void kitCreator(Configuration config, KitManager kit, @Nullable String gameName) {
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
				Util.warning("Unable to load kit " + gameName + path + "!");
				Util.log("-------------------------------------------");
			}
		}
	}

    /** Get an ItemStack from a string
     * @param args String to convert to an item
     * @param isStackable Whether this is stackable or a single item (ie: armor)
     * @return New ItemStack
     */
	@SuppressWarnings("deprecation")
	public ItemStack getItem(String args, boolean isStackable) {
		if (args == null) return null;
		int amount = 1;
		if (isStackable) {
			String a = args.split(" ")[1];
			if (Util.isInt(a)) {
				amount = Integer.parseInt(a);
			}
		}
		ItemStack item = itemStringToStack(args.split(" ")[0], amount);
		if (item == null) return null;

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
						item.addUnsafeEnchantment(e, level);
					}
				}
			} else if (s.startsWith("color:")) {
				try {
					s = s.replace("color:", "").toUpperCase();
					for (DyeColor c : DyeColor.values()) {
						if (c.name().equalsIgnoreCase(s)) {
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
					PotionMeta meta = ((PotionMeta) item.getItemMeta());
					assert meta != null;
					meta.setColor(Color.fromRGB(Integer.parseInt(s)));
					item.setItemMeta(meta);
				} catch (Exception ignore) {
				}
			} else if (s.startsWith("name:")) {
				s = s.replace("name:", "").replace("_", " ");
				s = ChatColor.translateAlternateColorCodes('&', s);
				ItemMeta im = item.getItemMeta();
				assert im != null;
				im.setDisplayName(s);
				item.setItemMeta(im);
			} else if (s.startsWith("lore:")) {
                s = s.replace("lore:", "").replace("_", " ");
                s = ChatColor.translateAlternateColorCodes('&', s);
                ItemMeta meta = item.getItemMeta();
                ArrayList<String> lore = new ArrayList<>(Arrays.asList(s.split(":")));
                assert meta != null;
                meta.setLore(lore);
                item.setItemMeta(meta);
            } else if (s.startsWith("data:")) {
				s = s.replace("data:", "").replace("~", " ");
				if (nbtApi != null)
					//nbtApi.setNBT(item, s);
				    item = nbtApi.getItemWithNBT(item, s);
			} else if (s.startsWith("ownerName:")) {
				s = s.replace("ownerName:", "");
				//if (item.getType().equals(Material.PLAYER_HEAD)) {
                if (item.getItemMeta() instanceof SkullMeta) {
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
		if (itemArr[0].equalsIgnoreCase("potion") || itemArr[0].equalsIgnoreCase("splash_potion") ||
                itemArr[0].equalsIgnoreCase("lingering_potion")) {
		    return getPotion(item, amount);
		}
		Material mat = verifyMaterial(itemArr[0].toUpperCase());
		if (mat == null) {
            return null;
        }
        return new ItemStack(mat, amount);
	}

	private Material verifyMaterial(String material) {
	    Material mat;
	    try {
	        mat = Material.valueOf(material);
        } catch (IllegalArgumentException ex) {
	        Util.warning("Invalid Material: &7" + material);
	        return null;
        }
	    return mat;
    }

	// Get a potion item stack from a string
	private ItemStack getPotion(String item, int amount) {
	    String[] effects = item.split(";");
	    String potionType = item.split(":")[0];
	    ItemStack potion = new ItemStack(Material.valueOf(potionType.toUpperCase()), amount);
	    PotionMeta potionMeta = ((PotionMeta) potion.getItemMeta());

        for (String effect : effects) {
            if (!verifyPotionEffects(effect)) {
                return null;
            }

            String[] data = effect.split(":");
            int i = (data[0].contains("potion") || data[0].contains("POTION")) ? 1 : 0;
            PotionEffectType potType = PotionEffectType.getByName(data[i]);
            int duration = Integer.parseInt(data[1 + i]);
            int amplifier = Integer.parseInt(data[2 + i]);
            assert potionMeta != null;
            assert potType != null;
            potionMeta.addCustomEffect(new PotionEffect(potType, duration, amplifier), true);
        }
        potion.setItemMeta(potionMeta);
        return potion;
    }

    // Verify if the potion effects are valid (including parameters)
    private boolean verifyPotionEffects(String data) {
	    String[] potionData = data.split(":");
	    if (potionData.length == 3 || potionData.length == 4) {
	        int i = potionData.length == 3 ? 0 : 1;
	        if (PotionEffectType.getByName(potionData[i].toUpperCase()) == null) {
                Util.warning("Potion effect type not found: &c" + potionData[i].toUpperCase());
                Util.log("  - Check your configs");
                Util.log("  - Proper example:");
                Util.log("      &bPOTION:POTION_TYPE:DURATION_IN_TICKS:AMPLIFIER");
                Util.log("      &bPOTION:HEAL:200:1");
                return false;
            } else if (!Util.isInt(potionData[i + 1])) {
                Util.warning("Potion duration incorrect format: &c" + potionData[i + 1]);
                Util.log("  - Check your configs");
                Util.log("  - Proper example:");
                Util.log("      &bPOTION:POTION_TYPE:DURATION_IN_TICKS:AMPLIFIER");
                Util.log("      &bPOTION:HEAL:200:1");
                return false;
            } else if (!Util.isInt(potionData[i + 2])) {
                Util.warning("Potion amplifier incorrect format: &c" + potionData[i + 2]);
                Util.log("  - Check your configs");
                Util.log("  - Proper example:");
                Util.log("      &bPOTION:POTION_TYPE:DURATION_IN_TICKS:AMPLIFIER");
                Util.log("      &bPOTION:HEAL:200:1");
                return false;
            }
        } else {
            Util.warning("Improper setup of potion: &c" + data);
            Util.log("  - Check your configs for missing arguments");
            Util.log("  - Proper example:");
            Util.log("      &bPOTION:POTION_TYPE:DURATION_IN_TICKS:LEVEL");
            Util.log("      &bPOTION:HEAL:200:1");
            return false;
        }
	    return true;
    }

	public ItemStack getSpectatorCompass() {
	    ItemStack compass = new ItemStack(Material.COMPASS);
	    ItemMeta meta = compass.getItemMeta();
	    assert meta != null;
	    meta.setDisplayName(Util.getColString(plugin.getLang().spectator_compass));
	    compass.setItemMeta(meta);
	    return compass;
    }

}
