package com.shanebeestudios.hg.api.data;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import com.shanebeestudios.hg.plugin.permission.Permissions.Permission;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Handler for creating individual kit entries
 */
@SuppressWarnings("unused")
public class KitEntry {

    private final String name;
    private Permission permission = null;
    private ItemStack helmet = null;
    private ItemStack chestplate = null;
    private ItemStack leggings = null;
    private ItemStack boots = null;
    private List<ItemStack> inventoryContents = null;
    private List<PotionEffect> potionEffects = null;

    /**
     * Create a new, empty kit entry
     */
    public KitEntry(String name) {
        this.name = name;
    }

    /**
     * Create new kit entry
     *
     * @param inventoryContents ItemStacks to add
     * @param helmet            Helmet to add
     * @param chestplate        Chestplate to add
     * @param leggings          Leggings to add
     * @param boots             Boots to add
     * @param permission        Permission for this kit
     * @param potionEffects     Potion effects to add
     */
    public KitEntry(@Nullable Game game, String name, List<ItemStack> inventoryContents,
                    ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots,
                    @Nullable String permission, List<PotionEffect> potionEffects) {
        this.name = name;
        this.inventoryContents = inventoryContents;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.potionEffects = potionEffects;
        if (permission != null && !permission.isEmpty()) {
            String arenaName = game != null ? game.getGameArenaData().getName() : null;
            this.permission = Permissions.registerKitPermission(arenaName, name, permission);
        }
    }

    public String getName() {
        return this.name;
    }

    /**
     * Check if a player has permission for this kit
     *
     * @param player Player to check
     * @return True if player has permission for this kit
     */
    public boolean hasKitPermission(Player player) {
        return this.permission == null || this.permission.has(player);
    }

    /**
     * Set the helmet for this kit entry
     *
     * @param helmet The helmet
     */
    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }

    /**
     * Get the helmet for this kit entry
     *
     * @return The helmet
     */
    public ItemStack getHelmet() {
        return this.helmet;
    }

    /**
     * Set the chestplate for this kit entry
     *
     * @param chestplate The chestplate
     */
    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    /**
     * Get the chestplate for this kit entry
     *
     * @return The chestplate
     */
    public ItemStack getChestplate() {
        return this.chestplate;
    }

    /**
     * Set the leggings for this kit entry
     *
     * @param leggings The leggings
     */
    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
    }

    /**
     * Get the leggings for this kit entry
     *
     * @return The leggings
     */
    public ItemStack getLeggings() {
        return this.leggings;
    }

    /**
     * Set the boots for this kit entry
     *
     * @param boots The boots
     */
    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }

    /**
     * Get the boots for this kit entry
     *
     * @return The boots
     */
    public ItemStack getBoots() {
        return this.boots;
    }

    /**
     * Set the potion effects for this kit entry
     *
     * @param potionEffects List of potion effects
     */
    public void setPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    /**
     * Add a potion effect to this kit entry
     *
     * @param potion The potion effect to add
     */
    public void addPotionEffect(PotionEffect potion) {
        this.potionEffects.add(potion);
    }

    /**
     * Get the potion effects for this kit entry
     *
     * @return List of potion effects
     */
    public List<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    /**
     * Set the permission for this kit entry
     *
     * @param arenaName  Name of arena to link this kit to, null to use default file
     * @param permission The permission (Will be prefixed with 'hungergames.kit.')
     */
    public void setPermission(@Nullable String arenaName, String permission) {
        this.permission = Permissions.registerKitPermission(arenaName, this.name, permission);
    }

    /**
     * Get the permission for this kit entry
     *
     * @return The permission
     */
    public Permission getPermission() {
        return this.permission;
    }

    /**
     * Set the inventory contents for this kit entry
     *
     * @param items The inventory contents
     */
    public void setInventoryContents(List<ItemStack> items) {
        this.inventoryContents = items;
    }

    /**
     * Get the inventory contents for this kit entry
     *
     * @return The inventory contents
     */
    public List<ItemStack> getInventoryContents() {
        return this.inventoryContents;
    }

    /**
     * Apply this kit to a player
     *
     * @param player Player to apply kit to
     */
    public void setInventoryContent(Player player) {
        Util.clearInv(player);
        player.getInventory().setContents(this.inventoryContents.toArray(new ItemStack[0]));
        player.getInventory().setHelmet(this.helmet);
        player.getInventory().setChestplate(this.chestplate);
        player.getInventory().setLeggings(this.leggings);
        player.getInventory().setBoots(this.boots);
        player.clearActivePotionEffects();
        player.addPotionEffects(this.potionEffects);
        player.updateInventory();
    }

    @Override
    public String toString() {
        return "KitEntry{" +
            "perm='" + this.permission + '\'' +
            ", helmet=" + this.helmet +
            ", chestplate=" + this.chestplate +
            ", leggings=" + this.leggings +
            ", boots=" + this.boots +
            ", inventoryContents=" + this.inventoryContents +
            ", potions=" + this.potionEffects +
            '}';
    }

}
