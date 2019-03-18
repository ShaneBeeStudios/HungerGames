package me.minebuilders.hg.data;

import java.util.ArrayList;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class KitEntry {

	private ItemStack helm;
	private String perm;
	private ItemStack boots;
	private ItemStack chestplate;
	private ItemStack pants;
	private ItemStack[] inventoryContents;
	private ArrayList<PotionEffect> posions;

	public KitEntry(ItemStack[] ic, ItemStack h, ItemStack b, ItemStack c, ItemStack p, String per, ArrayList<PotionEffect> po) {
		this.inventoryContents = ic;
		this.helm = h;
		this.boots = b;
		this.chestplate = c;
		this.pants = p;
		this.perm = per;
		this.posions = po;
	}

	public boolean hasKitPermission(Player p) {
		return perm == null || p.hasPermission(perm);
	}

	public void setInventoryContent(Player p) {
		Util.clearInv(p);
		p.getInventory().setContents(inventoryContents);
		p.getInventory().setHelmet(helm);
		p.getInventory().setChestplate(chestplate);
		p.getInventory().setLeggings(pants);
		p.getInventory().setBoots(boots);


		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		p.addPotionEffects(posions);
		HG.plugin.players.get(p.getUniqueId()).getGame().freeze(p);
		p.updateInventory();
	}
}
