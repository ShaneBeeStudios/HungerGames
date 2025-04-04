package com.shanebeestudios.hg.api.parsers;

import com.shanebeestudios.hg.api.registry.Registries;
import com.shanebeestudios.hg.api.util.NBTApi;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
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

    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    public static @Nullable ItemStack parseItem(@Nullable ConfigurationSection config) {
        if (config == null) return null;

        // ID
        String stringId = config.getString("id");
        if (stringId == null) return null;

        NamespacedKey key = NamespacedKey.fromString(stringId.toLowerCase(Locale.ROOT));
        if (key == null) return null;

        ItemType itemType = Registries.ITEM_TYPE_REGISTRY.get(key);
        if (itemType == null) return null;

        // COUNT
        int count = 1;
        if (config.contains("count")) {
            count = config.getInt("count");
        }
        ItemStack itemStack = itemType.createItemStack(count);

        // MAX_STACK_SIZE
        if (config.contains("max_stack_size")) {
            int maxStackSize = config.getInt("max_stack_size");
            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, Math.clamp(maxStackSize, 1, 99));
        }

        // NAME
        if (config.contains("custom_name")) {
            String name = config.getString("custom_name");
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, Util.getMini(name));
        } else if (config.contains("item_name")) {
            String name = config.getString("item_name");
            itemStack.setData(DataComponentTypes.ITEM_NAME, Util.getMini(name));
        }

        // MODEL
        if (config.contains("item_model")) {
            String model = config.getString("item_model");
            assert model != null;
            NamespacedKey modelKey = NamespacedKey.fromString(model);
            if (modelKey == null) {
                Util.warning("Invalid item model: " + model);
            } else {
                itemStack.setData(DataComponentTypes.ITEM_MODEL, modelKey);
            }
        }

        // LORE
        if (config.contains("lore")) {
            List<String> lore = config.getStringList("lore");
            List<Component> loreComponents = new ArrayList<>();
            lore.forEach(l -> loreComponents.add(Util.getMini(l)));
            itemStack.lore(loreComponents);
        }

        // ENCHANTMENTS
        if (config.contains("enchantments")) {
            List<String> enchantmentList = config.getStringList("enchantments");
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
        if (config.contains("max_damage")) {
            int maxDamage = config.getInt("max_damage");
            itemStack.setData(DataComponentTypes.MAX_DAMAGE, maxDamage);
        }

        // DAMAGE
        if (config.contains("damage")) {
            int damage = config.getInt("damage");
            itemStack.setData(DataComponentTypes.DAMAGE, damage);
        }

        // POTION
        if (config.contains("potion")) {
            String string = config.getString("potion");
            assert string != null;
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
        if (config.contains("potion_effects")) {
            List<Map<String, Object>> potionEffectsList = (List<Map<String, Object>>) config.getList("potion_effects");
            assert potionEffectsList != null;
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
        if (config.contains("dyed_color")) {
            int color = config.getInt("dyed_color");
            itemStack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(color), false));
        }

        // NBT
        if (config.contains("nbt")) {
            String nbtString = config.getString("nbt");
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
