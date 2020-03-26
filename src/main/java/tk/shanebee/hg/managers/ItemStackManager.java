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
import tk.shanebee.hg.util.PotionEffectUtils;
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

    private final HG plugin;
    private final NBTApi nbtApi;

    public ItemStackManager(HG p) {
        this.plugin = p;
        setKits();
        this.nbtApi = p.getNbtApi();
    }

    public void setKits() {
        kitCreator(plugin.getConfig(), plugin.getKitManager(), null);
        Util.log("Loaded kits");
    }

    /**
     * Set the kits for a game from a config
     *
     * @param gameName The game to set the kits for
     * @param config   Config the kit is pulled from
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
                    PotionEffectType type = PotionEffectUtils.getByKey(poti[0]);
                    if (poti[2].equalsIgnoreCase("forever")) {
                        assert type != null;
                        potions.add(type.createEffect(2147483647, Integer.parseInt(poti[1])));
                    } else {
                        int dur = Integer.parseInt(poti[2]) * 20;
                        assert type != null;
                        potions.add(type.createEffect(dur, Integer.parseInt(poti[1])));
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

    /**
     * Get an ItemStack from a string
     *
     * @param args        String to convert to an item
     * @param isStackable Whether this is stackable or a single item (ie: armor)
     * @return New ItemStack
     */
    @SuppressWarnings("deprecation")
    public ItemStack getItem(String args, boolean isStackable) {
        if (args == null) return null;
        int amount = 1;
        String[] split = args.split(" ");
        if (isStackable) {
            if (split.length > 1 && Util.isInt(split[1])) {
                amount = Integer.parseInt(split[1]);
            }
        }
        ItemStack item = itemStringToStack(split[0], amount);
        if (item == null) return null;

        for (String s : split) {
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
                if (item.getItemMeta() instanceof LeatherArmorMeta) {
                    LeatherArmorMeta meta = ((LeatherArmorMeta) item.getItemMeta());
                    meta.setColor(getColor(s));
                    item.setItemMeta(meta);
                } else if (item.getItemMeta() instanceof PotionMeta) {
                    PotionMeta meta = ((PotionMeta) item.getItemMeta());
                    meta.setColor(getColor(s));
                    item.setItemMeta(meta);
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
            } else if (s.startsWith("potion:")) {
                if (item.getItemMeta() instanceof PotionMeta) {
                    s = s.replace("potion:", "");
                    PotionMeta potionMeta = ((PotionMeta) item.getItemMeta());
                    String[] effects = s.split(";");
                    for (String effect : effects) {
                        if (verifyPotionEffects(effect, false)) {
                            String[] data = effect.split(":");
                            PotionEffectType potType = PotionEffectUtils.get(data[0]);
                            int duration = Integer.parseInt(data[1]);
                            int amplifier = Integer.parseInt(data[2]);
                            assert potionMeta != null;
                            assert potType != null;
                            PotionEffect potionEffect = new PotionEffect(potType, duration, amplifier);
                            potionMeta.addCustomEffect(potionEffect, true);
                        }
                        item.setItemMeta(potionMeta);
                    }
                }

            } else if (s.startsWith("data:")) {
                s = s.replace("data:", "").replace("~", " ");
                if (nbtApi != null)
                    item = nbtApi.getItemWithNBT(item, s);
            } else if (s.startsWith("ownerName:")) {
                s = s.replace("ownerName:", "");
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
        String oldPotion = item.toUpperCase();
        String[] itemArr = item.split(":");
        if (oldPotion.startsWith("POTION:") || oldPotion.startsWith("SPLASH_POTION:") || oldPotion.startsWith("LINGERING_POTION:")) {
            Util.warning("Using old potion item format: &b" + oldPotion);
            Util.warning("  - This format is deprecated and will be removed in the future, please use new format.");
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
    // DEPRECATED - will remove in future
    private ItemStack getPotion(String item, int amount) {
        String[] effects = item.split(";");
        String potionType = item.split(":")[0];
        ItemStack potion = new ItemStack(Material.valueOf(potionType.toUpperCase()), amount);
        PotionMeta potionMeta = ((PotionMeta) potion.getItemMeta());

        for (String effect : effects) {
            if (!verifyPotionEffects(effect, true)) {
                return null;
            }

            String[] data = effect.split(":");
            int i = (data[0].contains("potion") || data[0].contains("POTION")) ? 1 : 0;
            PotionEffectType potType = PotionEffectUtils.get(data[i]);
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
    private boolean verifyPotionEffects(String data, boolean potionItem) {
        String pot = potionItem ? "POTION:" : "potion:";
        String[] potionData = data.split(":");
        if (potionData.length == 3 || potionData.length == 4) {
            int i = potionData.length == 3 ? 0 : 1;
            if (PotionEffectUtils.get(potionData[i]) == null) {
                Util.warning("Potion effect type not found: &c" + potionData[i].toUpperCase());
                Util.log("  - Check your configs");
                Util.log("  - Proper example:");
                Util.log("      &b" + pot + ":POTION_TYPE:DURATION_IN_TICKS:AMPLIFIER");
                Util.log("      &b" + pot + ":HEAL:200:1");
                return false;
            } else if (!Util.isInt(potionData[i + 1])) {
                Util.warning("Potion duration incorrect format: &c" + potionData[i + 1]);
                Util.log("  - Check your configs");
                Util.log("  - Proper example:");
                Util.log("      &b" + pot + ":POTION_TYPE:DURATION_IN_TICKS:AMPLIFIER");
                Util.log("      &b" + pot + ":HEAL:200:1");
                return false;
            } else if (!Util.isInt(potionData[i + 2])) {
                Util.warning("Potion amplifier incorrect format: &c" + potionData[i + 2]);
                Util.log("  - Check your configs");
                Util.log("  - Proper example:");
                Util.log("      &b" + pot + ":POTION_TYPE:DURATION_IN_TICKS:AMPLIFIER");
                Util.log("      &b" + pot + ":HEAL:200:1");
                return false;
            }
        } else {
            Util.warning("Improper setup of potion: &c" + data);
            Util.log("  - Check your configs for missing arguments");
            Util.log("  - Proper example:");
            Util.log("      &b" + pot + ":POTION_TYPE:DURATION_IN_TICKS:LEVEL");
            Util.log("      &b" + pot + ":HEAL:200:1");
            return false;
        }
        return true;
    }

    private Color getColor(String colorString) {
        String dyeString = colorString.replace("color:", "");
        try {
            DyeColor dc = DyeColor.valueOf(dyeString.toUpperCase());
            return dc.getColor();
        } catch (Exception ignore) {
        }

        try {
            return Color.fromRGB(Integer.parseInt(dyeString));
        } catch (Exception ignore) {
        }
        return null;
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
