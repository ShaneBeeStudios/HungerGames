package tk.shanebee.hg.data;

import java.util.ArrayList;

import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class KitEntry {

	private ItemStack helm;
	private String perm;
	private ItemStack boots;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack[] inventoryContents;
	private ArrayList<PotionEffect> potions;

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
					String permission, ArrayList<PotionEffect> potions) {
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
		HG.plugin.players.get(player.getUniqueId()).getGame().freeze(player);
		player.updateInventory();
	}
}
