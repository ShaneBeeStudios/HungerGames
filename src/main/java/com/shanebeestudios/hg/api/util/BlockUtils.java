package com.shanebeestudios.hg.api.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import com.shanebeestudios.hg.data.Config;

import java.util.List;

public class BlockUtils {

    // Preset to empty lists, just in case
    private static ImmutableSet<Material> BONUS_BLOCK_MATERIALS = ImmutableSet.of();
    private static ImmutableSet<Material> BREAKABLE_BLOCK_MATERIALS = ImmutableSet.of();
    private static boolean BREAKABLE_BLOCK_ALL = false;

    /**
     * Setup the block sets.
     * <p>Should only be used internally</p>
     */
    public static void setupBuilder() {
        BONUS_BLOCK_MATERIALS = setup(Config.bonusBlockTypes).build();

        if (Config.blocks.contains("ALL")) {
            BREAKABLE_BLOCK_ALL = true;
        } else {
            BREAKABLE_BLOCK_MATERIALS = setup(Config.blocks).build();
        }
    }

    /**
     * Check if a block counts as a bonus chest
     *
     * @param block Block to check
     * @return True if block is a bonus chest
     */
    public static boolean isBonusBlock(Block block) {
        Material mat = block.getType();
        // No mater what is put in the config, a chest will never be a bonus block
        return mat != Material.CHEST && BONUS_BLOCK_MATERIALS.contains(mat);
    }

    /**
     * Check if a block is breakable/placeable
     *
     * @param block Block to check
     * @return True if block can be broken/placed
     */
    public static boolean isBreakableBlock(Block block) {
        return BREAKABLE_BLOCK_ALL || BREAKABLE_BLOCK_MATERIALS.contains(block.getType());
    }

    private static ImmutableSet.Builder<Material> setup(List<String> materialStrings) {
        ImmutableSet.Builder<Material> materialBuilder = ImmutableSet.builder();

        boolean warnShulker = false;
        boolean hasShulkerTag = false;
        for (String materialString : materialStrings) {
            if (!materialString.contains("tag")) {
                for (Material material : Material.values()) {
                    if (materialString.equalsIgnoreCase("ALL") || material.toString().equalsIgnoreCase(materialString)) {
                        materialBuilder.add(material);
                    }
                    // This is deprecated (on Nov 10/2020) and scheduled for removal in the future (use tags instead)
                    else if (materialString.equalsIgnoreCase("SHULKER_BOX") && material.toString().contains("SHULKER_BOX") && !materialString.contains("tag")) {
                        materialBuilder.add(material);
                        warnShulker = true;
                    }
                }
            } else {
                boolean tagFound = false;
                for (Tag<Material> blocks : Bukkit.getTags("blocks", Material.class)) {
                    String tag = blocks.getKey().getKey();
                    if (materialString.equalsIgnoreCase("tag:" + tag)) {
                        materialBuilder.addAll(blocks.getValues());
                        tagFound = true;
                    }
                    // Apparently this was added in 1.15
                    // Will remove 1.14.4- support in the future
                    if (tag.equalsIgnoreCase("shulker_boxes")) {
                        hasShulkerTag = true;
                    }
                }
                if (!tagFound) {
                    Util.warning("Unknown tag: &c" + materialString);
                }
            }
        }
        if (warnShulker && hasShulkerTag) {
            Util.warning("It appears you are using 'SHULKER_BOX' in your config (for either bonus blocks or breakable blocks)");
            Util.warning("This method is now deprecated and will be removed in the future");
            Util.warning("Instead use: 'tag:shulker_boxes'");
        }
        return materialBuilder;
    }

}
