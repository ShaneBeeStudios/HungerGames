package com.shanebeestudios.hg.api.util;

import com.google.common.collect.ImmutableSet;
import com.shanebeestudios.hg.api.registry.Registries;
import com.shanebeestudios.hg.data.Config;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("UnstableApiUsage")
public class BlockUtils {

    // Preset to empty lists, just in case
    private static ImmutableSet<BlockType> BONUS_BLOCK_TYPES = ImmutableSet.of();
    private static ImmutableSet<BlockType> BREAKABLE_BLOCK_TYPES = ImmutableSet.of();
    private static boolean BREAKABLE_BLOCK_ALL = false;

    /**
     * Setup block sets.
     * <p>Should only be used internally</p>
     */
    public static void setupBuilder() {
        BONUS_BLOCK_TYPES = setup(Config.SETTINGS_BONUS_BLOCK_TYPES).build();

        if (Config.ROLLBACK_EDITABLE_BLOCKS.contains("all")) {
            BREAKABLE_BLOCK_ALL = true;
        } else {
            BREAKABLE_BLOCK_TYPES = setup(Config.ROLLBACK_EDITABLE_BLOCKS).build();
        }
    }

    /**
     * Check if a block counts as a bonus chest
     *
     * @param block Block to check
     * @return True if block is a bonus chest
     */
    public static boolean isBonusBlock(Block block) {
        Material blockMaterial = block.getType();
        // No mater what is put in the config, a chest will never be a bonus block
        return blockMaterial != Material.CHEST && BONUS_BLOCK_TYPES.contains(blockMaterial.asBlockType());
    }

    /**
     * Check if a block material is breakable/placeable
     *
     * @param blockMaterial Block material to check
     * @return True if material can be broken/placed
     */
    public static boolean isEditableBlock(Material blockMaterial) {
        if (!blockMaterial.isBlock()) return false;
        return BREAKABLE_BLOCK_ALL || BREAKABLE_BLOCK_TYPES.contains(blockMaterial.asBlockType());
    }

    private static ImmutableSet.Builder<BlockType> setup(List<String> blockTypeStrings) {
        ImmutableSet.Builder<BlockType> builder = ImmutableSet.builder();

        for (String blockTypeString : blockTypeStrings) {
            blockTypeString = blockTypeString.toLowerCase(Locale.ROOT);

            if (blockTypeString.startsWith("#")) {
                blockTypeString = blockTypeString.substring("#".length());

                NamespacedKey key = NamespacedKey.fromString(blockTypeString);
                if (key != null) {
                    TagKey<BlockType> blockTypeTagKey = TagKey.create(RegistryKey.BLOCK, key);

                    if (Registries.BLOCK_TYPE_REGISTRY.hasTag(blockTypeTagKey)) {
                        for (TypedKey<BlockType> blockTypedKey : Registries.BLOCK_TYPE_REGISTRY.getTag(blockTypeTagKey)) {
                            BlockType blockType = Registries.BLOCK_TYPE_REGISTRY.get(blockTypedKey);
                            if (blockType != null) builder.add(blockType);
                        }
                    } else {
                        Util.warning("Unknown block tag: <red>" + blockTypeString);
                    }
                } else {
                    Util.warning("Unknown block tag: <red>" + blockTypeString);
                }

            } else {
                NamespacedKey key = NamespacedKey.fromString(blockTypeString);
                if (key != null) {
                    BlockType blockType = Registries.BLOCK_TYPE_REGISTRY.get(key);
                    if (blockType != null) {
                        builder.add(blockType);
                    } else {
                        Util.warning("Unknown block type: <red>" + blockTypeString);
                    }
                } else {
                    Util.warning("Unknown block type: <red>" + blockTypeString);
                }
            }
        }
        return builder;
    }

}
