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
        NBTContainer container = NBTItem.convertItemtoNBT(item);
        container.mergeCompound(new NBTContainer(value));
        return NBTItem.convertNBTtoItem(container);
    }

    /** Get the NBT from an item
     * @param item ItemStack to get NBT from
     * @return NBT string
     */
    public String getNBT(org.bukkit.inventory.ItemStack item) {
        return NBTItem.convertItemtoNBT(item).asNBTString();
    }

}
