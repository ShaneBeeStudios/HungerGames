package com.shanebeestudios.hg.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Handler for creating individual kit entries
 */
@SuppressWarnings("unused")
public class KitEntry {

	private ItemStack helm = null;
	private String perm = null;
	private ItemStack boots = null;
	private ItemStack chestplate = null;
	private ItemStack leggings = null;
	private ItemStack[] inventoryContents = null;
	private List<PotionEffect> potions = null;

	/**
	 * Create a new, empty kit entry
	 */
	public KitEntry() {}

	/** Create new kit entry
	 * @param ic ItemStacks to add
	 * @param helmet Helmet to add
	 * @param boots Boots to add
	 * @param chestplate Chestplate to add
	 * @param leggings Leggings to add
	 * @param permission Permission for this kit
	 * @param potions Potion effects to add
	 */
	public KitEntry(ItemStack[] ic, ItemStack helmet, ItemStack boots, ItemStack chestplate, ItemStack leggings,
					String permission, List<PotionEffect> potions) {
		this.inventoryContents = ic;
		this.helm = helmet;
		this.boots = boots;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.perm = permission;
		this.potions = potions;
	}

	/** Check if a player has permission for this kit
	 * @param player Player to check
	 * @return True if player has permission for this kit
	 */
	public boolean hasKitPermission(Player player) {
		return perm == null || player.hasPermission(perm);
	}

	/** Set the helmet for this kit entry
	 * @param helmet The helmet
	 */
	public void setHelmet(ItemStack helmet) {
		this.helm = helmet;
	}

	/** Get the helmet for this kit entry
	 * @return The helmet
	 */
	public ItemStack getHelmet() {
		return this.helm;
	}

	/** Set the chestplate for this kit entry
	 * @param chestplate The chestplate
	 */
	public void setChestplate(ItemStack chestplate) {
		this.chestplate = chestplate;
	}

	/** Get the chestplate for this kit entry
	 * @return The chestplate
	 */
	public ItemStack getChestplate() {
		return this.chestplate;
	}

	/** Set the leggings for this kit entry
	 * @param leggings The leggings
	 */
	public void setLeggings(ItemStack leggings) {
		this.leggings = leggings;
	}

	/** Get the leggings for this kit entry
	 * @return The leggings
	 */
	public ItemStack getLeggings() {
		return this.leggings;
	}

	/** Set the boots for this kit entry
	 * @param boots The boots
	 */
	public void setBoots(ItemStack boots) {
		this.boots = boots;
	}

	/** Get the boots for this kit entry
	 * @return The boots
	 */
	public ItemStack getBoots() {
		return this.boots;
	}

	/** Set the potion effects for this kit entry
	 * @param potions List of potion effects
	 */
	public void setPotions(ArrayList<PotionEffect> potions) {
		this.potions = potions;
	}

	/** Add a potion effect to this kit entry
	 * @param potion The potion effect to add
	 */
	public void addPotion(PotionEffect potion) {
		this.potions.add(potion);
	}

	/** Get the potion effects for this kit entry
	 * @return List of potion effects
	 */
	public List<PotionEffect> getPotions() {
		return this.potions;
	}

	/** Set the permission for this kit entry
	 * @param permission The permission
	 */
	public void setPermission(String permission) {
		this.perm = permission;
	}

	/** Get the permission for this kit entry
	 * @return The permission
	 */
	public String getPermission() {
		return this.perm;
	}

	/** Set the inventory contents for this kit entry
	 * @param items The inventory contents
	 */
	public void setInventoryContents(ItemStack[] items) {
		this.inventoryContents = items;
	}

	/** Get the inventory contents for this kit entry
	 * @return The inventory contents
	 */
	public ItemStack[] getInventoryContents() {
		return this.inventoryContents;
	}

	/** Apply this kit to a player
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
		HungerGames.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()).getGame().getGamePlayerData().freeze(player);
		player.updateInventory();
	}

    @Override
    public String toString() {
        return "KitEntry{" +
                "helm=" + helm +
                ", perm='" + perm + '\'' +
                ", boots=" + boots +
                ", chestplate=" + chestplate +
                ", leggings=" + leggings +
                ", inventoryContents=" + Arrays.toString(inventoryContents) +
                ", potions=" + potions +
                '}';
    }

}
