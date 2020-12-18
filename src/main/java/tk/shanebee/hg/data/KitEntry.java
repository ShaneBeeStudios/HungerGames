package tk.shanebee.hg.data;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.hg.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Handler for creating individual kit entries
 */
@SuppressWarnings("unused")
public class KitEntry {

    private final String name;
    private ItemStack helm;
    private String perm;
    private ItemStack boots;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack[] inventoryContents;
    private ArrayList<PotionEffect> potions;

    /**
     * Create new kit entry
     *
     * @param inventoryContents ItemStacks to add
     * @param helmet            Helmet to add
     * @param boots             Boots to add
     * @param chestplate        Chestplate to add
     * @param leggings          Leggings to add
     * @param permission        Permission for this kit
     * @param potions           Potion effects to add
     */
    public KitEntry(@NotNull String name, ItemStack[] inventoryContents, ItemStack helmet, ItemStack boots, ItemStack chestplate, ItemStack leggings,
                    String permission, ArrayList<PotionEffect> potions) {
        this.name = name;
        this.inventoryContents = inventoryContents;
        this.helm = helmet;
        this.boots = boots;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.perm = permission;
        this.potions = potions;
    }

    /**
     * Get the name of this kit
     *
     * @return Name of kit
     */
    public String getName() {
        return name;
    }

    /**
     * Check if a player has permission for this kit
     *
     * @param player Player to check
     * @return True if player has permission for this kit
     */
    public boolean hasKitPermission(Player player) {
        return perm == null || player.hasPermission(perm);
    }

    /**
     * Set the helmet for this kit entry
     *
     * @param helmet The helmet
     */
    public void setHelmet(ItemStack helmet) {
        this.helm = helmet;
    }

    /**
     * Get the helmet for this kit entry
     *
     * @return The helmet
     */
    public ItemStack getHelmet() {
        return this.helm;
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
     * @param potions List of potion effects
     */
    public void setPotions(ArrayList<PotionEffect> potions) {
        this.potions = potions;
    }

    /**
     * Add a potion effect to this kit entry
     *
     * @param potion The potion effect to add
     */
    public void addPotion(PotionEffect potion) {
        this.potions.add(potion);
    }

    /**
     * Get the potion effects for this kit entry
     *
     * @return List of potion effects
     */
    public ArrayList<PotionEffect> getPotions() {
        return this.potions;
    }

    /**
     * Set the permission for this kit entry
     *
     * @param permission The permission
     */
    public void setPermission(String permission) {
        this.perm = permission;
    }

    /**
     * Get the permission for this kit entry
     *
     * @return The permission
     */
    public String getPermission() {
        return this.perm;
    }

    /**
     * Set the inventory contents for this kit entry
     *
     * @param items The inventory contents
     */
    public void setInventoryContents(ItemStack[] items) {
        this.inventoryContents = items;
    }

    /**
     * Get the inventory contents for this kit entry
     *
     * @return The inventory contents
     */
    public ItemStack[] getInventoryContents() {
        return this.inventoryContents;
    }

    /**
     * Apply this kit to a player
     *
     * @param player Player to apply kit to
     */
    public void setInventoryContent(Player player) {
        Util.clearInv(player);
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setHelmet(helm);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.addPotionEffects(potions);
        player.updateInventory();
    }

    @Override
    public String toString() {
        return "KitEntry{" + "name='" + name + '\'' +
                ", helm=" + helm +
                ", perm='" + perm + '\'' +
                ", boots=" + boots +
                ", chestplate=" + chestplate +
                ", leggings=" + leggings +
                ", inventoryContents=" + (inventoryContents == null ? "null" : Arrays.asList(inventoryContents).toString()) +
                ", potions=" + potions +
                '}';
    }

}
