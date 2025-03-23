package com.shanebeestudios.hg.managers;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.KitEntry;
import com.shanebeestudios.hg.api.parsers.ItemParser;
import com.shanebeestudios.hg.api.util.NBTApi;
import com.shanebeestudios.hg.api.util.PotionEffectUtils;
import com.shanebeestudios.hg.api.util.PotionTypeUtils;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage item stacks for kits and chests
 */
public class ItemStackManager {

    private final HungerGames plugin;
    private final NBTApi nbtApi;

    public ItemStackManager(HungerGames plugin) {
        this.plugin = plugin;
        this.nbtApi = plugin.getNbtApi();
        loadGeneralKits();
    }

    public void loadGeneralKits() {
        File kitFile = new File(this.plugin.getDataFolder(), "kits.yml");

        if (!kitFile.exists()) {
            this.plugin.saveResource("kits.yml", false);
            Util.logMini("- New kits.yml file has been <green>successfully generated!");
        }
        YamlConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
        Util.logMini("Loading kits:");
        kitCreator(kitConfig, plugin.getKitManager(), null);
        Util.logMini("- Kits have been <green>successfully loaded!");
    }

    /**
     * Set the kits for a game from a config
     *
     * @param gameName The game to set the kits for
     * @param arenaSection   Config the kit is pulled from
     * @return New KitManager for a game
     */
    public KitManager loadGameKits(String gameName, ConfigurationSection arenaSection) {
        KitManager kit = new KitManager();
        if (arenaSection.getConfigurationSection("kits") == null) return null;

        kitCreator(arenaSection, kit, null);
        Util.logMini("- Loaded custom kits for arena: <aqua>" + gameName);
        return kit;
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    private void kitCreator(ConfigurationSection config, KitManager kit, @Nullable String gameName) {
        if (gameName == null) gameName = "";
        if (config.getConfigurationSection(gameName + "kits") == null) return;

        for (String path : config.getConfigurationSection(gameName + "kits").getKeys(false)) {
            ConfigurationSection kitsSection = config.getConfigurationSection(gameName + "kits." + path);

            try {
                Map<Integer, ItemStack> items = new HashMap<>();
                loadItems(kitsSection.getMapList("items"), items);

                ItemStack helmet = ItemParser.parseItem(kitsSection.getConfigurationSection("helmet"));
                ItemStack chestplate = ItemParser.parseItem(kitsSection.getConfigurationSection("chestplate"));
                ItemStack leggings = ItemParser.parseItem(kitsSection.getConfigurationSection("leggings"));
                ItemStack boots = ItemParser.parseItem(kitsSection.getConfigurationSection("boots"));

                List<PotionEffect> potionEffects = new ArrayList<>();
                List<Map<?, ?>> mapList = kitsSection.getMapList("potion-effects");
                mapList.forEach(map -> potionEffects.add(ItemParser.parsePotionEffect((Map<String, Object>) map)));

                String permission = null;
                if (kitsSection.contains("permission") && !kitsSection.getString("permission").equalsIgnoreCase("none"))
                    permission = kitsSection.getString("permission");

                KitEntry kitEntry = new KitEntry(items.values().toArray(new ItemStack[0]), helmet, boots, chestplate, leggings, permission, potionEffects);
                kit.addKit(path, kitEntry);
                Util.logMini("- Loaded kit <white>'<aqua>%s<white>'", gameName + path);
            } catch (Exception e) {
                Util.logMini("-------------------------------------------");
                Util.logMini("<yellow>Unable to load kit " + gameName + path + "! (for a more detailed message enable 'debug' in config and reload)");
                Util.logMini("-------------------------------------------");
                Util.debug(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadItems(List<Map<?, ?>> configListMap, Map<Integer, ItemStack> map) {
        for (Map<?, ?> itemMapUnchecked : configListMap) {
            Map<String, Object> itemMap = (Map<String, Object>) itemMapUnchecked;

            ItemStack itemStack = ItemParser.parseItem(itemMap);
            int chance = (int) itemMap.getOrDefault("chance", 1);
            for (int i = 0; i < chance; i++) {
                map.put(map.size() + 1, itemStack);
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
                enchant(itemMeta, args, s);
            } else if (s.startsWith("color:")) {
                if (itemMeta instanceof LeatherArmorMeta) {
                    ((LeatherArmorMeta) itemMeta).setColor(getColor(s));
                    try {
                        itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    } catch (NoSuchFieldError ignore) {
                    }
                } else if (itemMeta instanceof PotionMeta) {
                    ((PotionMeta) itemMeta).setColor(getColor(s));
                } else {
                    Util.log("<yellow>Item cannot be colored: <red>%s <yellow>line: <aqua>%s", split[0], args);
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
            } else if ((s.startsWith("potion:") || s.startsWith("potion-type:")) && itemMeta instanceof PotionMeta) {
                s = s.replace("potion:", "");
                s = s.replace("potion-type:", "");
                String[] effects = s.split(";");
                for (String effect : effects) {
                    PotionEffect potionEffect = PotionEffectUtils.getPotionEffect(effect);
                    if (potionEffect != null) {
                        ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
                    }
                }
            } else if (s.startsWith("potion-base:") && itemMeta instanceof PotionMeta) {
                s = s.replace("potion-base:", "");
                PotionType potionData = PotionTypeUtils.getPotionData(s);
                if (potionData != null) {
                    ((PotionMeta) itemMeta).setBasePotionType(potionData);
                }
            } else if (s.startsWith("data:")) {
                s = s.replace("data:", "").replace("~", " ");
                if (nbtApi != null)
                    itemMeta = nbtApi.getItemWithNBT(item, s).getItemMeta();
            } else if (s.startsWith("ownerName:") && itemMeta instanceof SkullMeta) {
                s = s.replace("ownerName:", "");
                ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(s));
            }
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    @SuppressWarnings("deprecation") //Enchantment#getName
    private void enchant(ItemMeta itemMeta, String line, String enchantString) {
        enchantString = enchantString.replace("enchant:", "").toUpperCase();
        String[] d = enchantString.split(":");
        int level = 1;
        if (d.length != 1 && Util.isInt(d[1])) {
            level = Integer.parseInt(d[1]);
        }
        for (Enchantment e : Enchantment.values()) {
            if (e.getKey().getKey().equalsIgnoreCase(d[0]) || e.getName().equalsIgnoreCase(d[0])) {
                itemMeta.addEnchant(e, level, true);
                return;
            }
        }
        Util.logMini("<yellow>Invalid enchantment: <red>%s <yellow>line: <aqua>%s", enchantString, line);
    }

    private ItemStack itemStringToStack(String item, int amount) {
        Material material;
        try {
            material = Material.valueOf(item);
        } catch (IllegalArgumentException ex) {
            Util.logMini("<yellow>Invalid Material: <grey>" + item);
            return null;
        }
        return new ItemStack(material, amount);
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
        meta.displayName(Util.getMini(plugin.getLang().spectator_compass));
        compass.setItemMeta(meta);
        return compass;
    }

}
