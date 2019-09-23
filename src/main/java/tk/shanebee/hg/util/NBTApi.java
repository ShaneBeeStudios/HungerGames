package tk.shanebee.hg.util;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

/**
 * NBT class for adding NBT to items
 * <p>(Mainly for internal use)</p>
 */
public class NBTApi {

	/** Set the NBT of an item
	 * @param item The item to set
	 * @param value The NBT string to add to the item
	 */
    public ItemStack getItemWithNBT(ItemStack item, String value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.mergeCompound(new NBTContainer(value));
        return NBTItem.convertNBTtoItem(nbtItem);
    }

    public String getNBT(org.bukkit.inventory.ItemStack i) {
        return NBTItem.convertItemtoNBT(i).asNBTString();
    }

}
