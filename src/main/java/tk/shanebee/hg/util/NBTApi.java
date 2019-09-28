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
     * @return Returns the ItemStack with the new NBT
     */
    public ItemStack getItemWithNBT(ItemStack item, String value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.mergeCompound(new NBTContainer(value));
        return NBTItem.convertNBTtoItem(nbtItem);
    }

    /** Get the NBT string from an item
     * @param item Item to grab NBT from
     * @return NBT string from item
     */
    public String getNBT(ItemStack item) {
        return NBTItem.convertItemtoNBT(item).asNBTString();
    }

}
