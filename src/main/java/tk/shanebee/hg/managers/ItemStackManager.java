package tk.shanebee.hg.managers;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.KitEntry;
import tk.shanebee.hg.util.NBTApi;
import tk.shanebee.hg.util.PotionEffectUtils;
import tk.shanebee.hg.util.Util;

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
        kitCreator(plugin.getHGConfig().getConfig(), plugin.getKitManager(), null);
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
        if (config.getConfigurationSection(gameName + "kits") == null) return;
        for (String path : config.getConfigurationSection(gameName + "kits").getKeys(false)) {
            try {
                ArrayList<ItemStack> stack = new ArrayList<>();
                ArrayList<PotionEffect> potions = new ArrayList<>();
                String perm = null;

                for (String item : config.getStringList(gameName + "kits." + path + ".items"))
                    stack.add(getItem(item, true));

                for (String pot : config.getStringList(gameName + "kits." + path + ".potion-effects")) {
                    String[] poti = pot.split(":");
                    PotionEffectType type = PotionEffectUtils.get(poti[0]);
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
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
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
                if (itemMeta instanceof LeatherArmorMeta) {
                    ((LeatherArmorMeta) itemMeta).setColor(getColor(s));
                } else if (itemMeta instanceof PotionMeta) {
                    ((PotionMeta) itemMeta).setColor(getColor(s));
                }
            } else if (s.startsWith("name:")) {
                s = s.replace("name:", "").replace("_", " ");
                s = Util.getColString(s);
                itemMeta.setDisplayName(s);
            } else if (s.startsWith("lore:")) {
                s = s.replace("lore:", "").replace("_", " ");
                s = Util.getColString(s);
                ArrayList<String> lore = new ArrayList<>(Arrays.asList(s.split(":")));
                itemMeta.setLore(lore);
            } else if (s.startsWith("potion:") && itemMeta instanceof PotionMeta) {
                s = s.replace("potion:", "");
                String[] effects = s.split(";");
                for (String effect : effects) {
                    if (verifyPotionEffects(effect)) {
                        String[] data = effect.split(":");
                        PotionEffectType potType = PotionEffectUtils.get(data[0]);
                        int duration = Integer.parseInt(data[1]);
                        int amplifier = Integer.parseInt(data[2]);
                        assert potType != null;
                        PotionEffect potionEffect = new PotionEffect(potType, duration, amplifier);
                        ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
                    }
                }
            } else if (s.startsWith("data:")) {
                s = s.replace("data:", "").replace("~", " ");
                if (nbtApi != null)
                    item = nbtApi.getItemWithNBT(item, s);
            } else if (s.startsWith("ownerName:") && itemMeta instanceof SkullMeta) {
                s = s.replace("ownerName:", "");
                ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(s));
            }
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    private ItemStack itemStringToStack(String item, int amount) {
        String[] itemArr = item.split(":");
        String materialString = itemArr[0].toUpperCase();
        Material material;
        try {
            material = Material.valueOf(materialString);
        } catch (IllegalArgumentException ex) {
            Util.warning("Invalid Material: &7" + materialString);
            return null;
        }
        return new ItemStack(material, amount);
    }

    // Verify if the potion effects are valid (including parameters)
    private boolean verifyPotionEffects(String data) {
        String[] potionData = data.split(":");
        if (potionData.length == 3 || potionData.length == 4) {
            int i = potionData.length == 3 ? 0 : 1;
            if (PotionEffectUtils.get(potionData[i]) == null) {
                Util.warning("Potion effect type not found: &c" + potionData[i].toUpperCase() + " &ein: &b" + data);
                potionWarning();
                return false;
            } else if (!Util.isInt(potionData[i + 1])) {
                Util.warning("Potion duration incorrect format: &c" + potionData[i + 1] + " &ein: &b" + data);
                potionWarning();
                return false;
            } else if (!Util.isInt(potionData[i + 2])) {
                Util.warning("Potion amplifier incorrect format: &c" + potionData[i + 2] + " &ein: &b" + data);
                potionWarning();
                return false;
            }
        } else {
            Util.warning("Improper setup of potion: &c" + data);
            potionWarning();
            return false;
        }
        return true;
    }

    private void potionWarning() {
        Util.warning("&r  - Check your configs");
        Util.warning("&r  - Proper example:");
        Util.warning("      &bpotion:POTION_TYPE:DURATION_IN_TICKS:LEVEL");
        Util.warning("      &bpotion:HEAL:200:1");
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
