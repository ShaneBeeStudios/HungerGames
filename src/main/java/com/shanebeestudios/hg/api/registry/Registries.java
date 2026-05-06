package com.shanebeestudios.hg.api.registry;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockType;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * {@link Registry} shortcuts
 */
@SuppressWarnings({"NullableProblems", "UnstableApiUsage"})
public class Registries {

    private static final RegistryAccess ACCESS = RegistryAccess.registryAccess();
    public static final Registry<Attribute> ATTRIBUTE_REGISTRY = ACCESS.getRegistry(RegistryKey.ATTRIBUTE);
    public static final Registry<BlockType> BLOCK_TYPE_REGISTRY = ACCESS.getRegistry(RegistryKey.BLOCK);
    public static final Registry<DamageType> DAMAGE_TYPE_REGISTRY = ACCESS.getRegistry(RegistryKey.DAMAGE_TYPE);
    public static final Registry<DataComponentType> DATA_COMPONENT_TYPE_REGISTRY = ACCESS.getRegistry(RegistryKey.DATA_COMPONENT_TYPE);
    public static final Registry<Enchantment> ENCHANTMENT_REGISTRY = ACCESS.getRegistry(RegistryKey.ENCHANTMENT);
    public static final Registry<EntityType> ENTITY_TYPE_REGISTRY = ACCESS.getRegistry(RegistryKey.ENTITY_TYPE);
    public static final Registry<ItemType> ITEM_TYPE_REGISTRY = ACCESS.getRegistry(RegistryKey.ITEM);
    public static final Registry<PotionEffectType> POTION_EFFECT_TYPE_REGISTRY = ACCESS.getRegistry(RegistryKey.MOB_EFFECT);
    public static final Registry<PotionType> POTION_TYPE_REGISTRY = ACCESS.getRegistry(RegistryKey.POTION);

}
