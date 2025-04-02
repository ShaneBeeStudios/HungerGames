package com.shanebeestudios.hg.api.parsers;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.registry.Registries;
import com.shanebeestudios.hg.api.util.NBTApi;
import com.shanebeestudios.hg.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.PotionContents;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemParser {

    private static final NBTApi NBT_API = HungerGames.getPlugin().getNbtApi();

    public static abstract class General<T> {
        T object;

        public General(T object) {
            this.object = object;
        }

        public abstract boolean containsKey(String key);

        public abstract List<?> getList(String key);

        public abstract String getString(String key);

        public abstract List<String> getStringList(String key);

        public abstract int getInt(String key);

        public abstract double getDouble(String key);
    }

    @SuppressWarnings("unchecked")
    public static class GeneralMap extends General<Map<String, Object>> {

        public GeneralMap(Map<String, Object> object) {
            super(object);
        }

        @Override
        public boolean containsKey(String key) {
            return this.object.containsKey(key);
        }

        @Override
        public List<?> getList(String key) {
            return (List<?>) this.object.get(key);
        }

        @Override
        public String getString(String key) {
            return (String) this.object.get(key);
        }

        @Override
        public List<String> getStringList(String key) {
            return (List<String>) this.object.get(key);
        }

        @Override
        public int getInt(String key) {
            return (int) this.object.get(key);
        }

        @Override
        public double getDouble(String key) {
            return (double) this.object.get(key);
        }
    }

    public static class GeneralConfigSection extends General<ConfigurationSection> {

        public GeneralConfigSection(ConfigurationSection object) {
            super(object);
        }

        @Override
        public boolean containsKey(String key) {
            return this.object.contains(key);
        }

        @Override
        public List<?> getList(String key) {
            return this.object.getList(key);
        }

        @Override
        public String getString(String key) {
            return this.object.getString(key);
        }

        @Override
        public List<String> getStringList(String key) {
            return this.object.getStringList(key);
        }

        @Override
        public int getInt(String key) {
            return this.object.getInt(key);
        }

        @Override
        public double getDouble(String key) {
            return this.object.getDouble(key);
        }
    }

    public static @Nullable ItemStack parseItem(@Nullable Map<String, Object> map) {
        if (map == null) return null;
        return parseItem(new GeneralMap(map));
    }

    public static @Nullable ItemStack parseItem(@Nullable ConfigurationSection section) {
        if (section == null) return null;
        return parseItem(new GeneralConfigSection(section));
    }

    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    private static ItemStack parseItem(General<?> map) {
        // ID
        NamespacedKey key = NamespacedKey.fromString(map.getString("id").toLowerCase(Locale.ROOT));
        if (key == null) return null;

        ItemType itemType = Registries.ITEM_TYPE_REGISTRY.get(key);
        if (itemType == null) return null;

        // COUNT
        int count = 1;
        if (map.containsKey("count")) {
            count = map.getInt("count");
        }
        ItemStack itemStack = itemType.createItemStack(count);

        // MAX_STACK_SIZE
        if (map.containsKey("max_stack_size")) {
            int maxStackSize = map.getInt("max_stack_size");
            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, Math.clamp(maxStackSize, 1, 99));
        }

        // NAME
        if (map.containsKey("custom_name")) {
            String name = map.getString("custom_name");
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, Util.getMini(name));
        } else if (map.containsKey("item_name")) {
            String name = map.getString("item_name");
            itemStack.setData(DataComponentTypes.ITEM_NAME, Util.getMini(name));
        }

        // MODEL
        if (map.containsKey("item_model")) {
            String model = map.getString("item_model");
            NamespacedKey modelKey = NamespacedKey.fromString(model);
            if (modelKey == null) {
                Util.warning("Invalid item model: " + model);
            } else {
                itemStack.setData(DataComponentTypes.ITEM_MODEL, modelKey);
            }
        }

        // LORE
        if (map.containsKey("lore")) {
            List<String> lore = map.getStringList("lore");
            List<Component> loreComponents = new ArrayList<>();
            lore.forEach(l -> loreComponents.add(Util.getMini(l)));
            itemStack.lore(loreComponents);
        }

        // ENCHANTMENTS
        if (map.containsKey("enchantments")) {
            List<String> enchantmentList = map.getStringList("enchantments");
            for (String string : enchantmentList) {
                String[] split = string.split("=");
                NamespacedKey namespacedKey = NamespacedKey.fromString(split[0]);
                ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
                int level = Integer.parseInt(split[1]);
                if (namespacedKey != null) {
                    Enchantment enchantment = Registries.ENCHANTMENT_REGISTRY.get(namespacedKey);
                    if (enchantment != null) {
                        builder.add(enchantment, level);
                    }
                }
                itemStack.setData(DataComponentTypes.ENCHANTMENTS, builder.build());
            }
        }

        // MAX_DAMAGE
        if (map.containsKey("max_damage")) {
            int maxDamage = map.getInt("max_damage");
            itemStack.setData(DataComponentTypes.MAX_DAMAGE, maxDamage);
        }

        // DAMAGE
        if (map.containsKey("damage")) {
            int damage = map.getInt("damage");
            itemStack.setData(DataComponentTypes.DAMAGE, damage);
        }

        // POTION
        if (map.containsKey("potion")) {
            String string = map.getString("potion");
            NamespacedKey namespacedKey = NamespacedKey.fromString(string);
            if (namespacedKey != null) {
                PotionType potionType = Registries.POTION_TYPE_REGISTRY.get(namespacedKey);
                if (potionType != null) {
                    PotionContents.Builder builder = PotionContents.potionContents();
                    builder.potion(potionType);
                    itemStack.setData(DataComponentTypes.POTION_CONTENTS, builder.build());
                }
            }
        }

        // POTION_EFFECT
        if (map.containsKey("potion_effects")) {
            List<Map<String, Object>> potionEffectsList = (List<Map<String, Object>>) map.getList("potion_effects");
            PotionContents.Builder builder = PotionContents.potionContents();
            AtomicInteger color = new AtomicInteger(-1);
            potionEffectsList.forEach(entry -> {
                PotionEffect potionEffect = parsePotionEffect(entry);
                if (potionEffect != null) {
                    builder.addCustomEffect(potionEffect);
                    if (entry.containsKey("custom_color")) {
                        color.set((int) entry.get("custom_color"));
                    }
                }
            });
            if (color.get() != -1) builder.customColor(Color.fromRGB(color.get()));
            itemStack.setData(DataComponentTypes.POTION_CONTENTS, builder.build());
        }

        // DYED_COLOR
        if (map.containsKey("dyed_color")) {
            int color = map.getInt("dyed_color");
            itemStack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(color), false));
        }

        // NBT
        if (map.containsKey("nbt")) {
            String nbtString = map.getString("nbt");
            NBT_API.applyNBTToItem(itemStack, nbtString);
        }

        return itemStack;
    }

    public static PotionEffect parsePotionEffect(Map<String, Object> entry) {
        String string = (String) entry.get("type");
        NamespacedKey namespacedKey = NamespacedKey.fromString(string);
        if (namespacedKey != null) {
            PotionEffectType potionEffectType = Registries.POTION_EFFECT_TYPE_REGISTRY.get(namespacedKey);
            if (potionEffectType != null) {
                int duration = (int) entry.getOrDefault("duration", 300); // Default to 15 seconds
                int amplifier = (int) entry.getOrDefault("amplifier", 0);
                boolean ambient = (boolean) entry.getOrDefault("ambient", false);
                boolean show_icon = (boolean) entry.getOrDefault("show_icon", true);
                boolean show_particles = (boolean) entry.getOrDefault("show_particles", true);
                return new PotionEffect(potionEffectType, duration, amplifier, ambient, show_particles, show_icon);
            }
        }
        return null;
    }


}
