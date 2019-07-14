package tk.shanebee.hg.nms;

import org.bukkit.inventory.ItemStack;

/**
 * NBT class for adding NBT to items
 * <p>(Mainly for internal use)</p>
 */
public interface NBTApi {

	/** Set the NBT of an item
	 * @param item The item to set
	 * @param string The NBT string to add to the item
	 */
	void setNBT(ItemStack item, String string);

}
