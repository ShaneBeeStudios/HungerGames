package tk.shanebee.hg.nms;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.MojangsonParser;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import tk.shanebee.hg.Util;

/**
 * Internal use only
 */
public class NMS_v1_14_R1 implements NBTApi {

	public void setNBT(org.bukkit.inventory.ItemStack item, String value) {
		ItemStack nms = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbt = new NBTTagCompound();
		if (nms.getTag() != null) {
			nbt = nms.getTag();
		}
		try {
			NBTTagCompound nbtv = MojangsonParser.parse(value);
			nbt.a(nbtv);
			nms.setTag(nbt);
		} catch (CommandSyntaxException ex) {
			Util.warning("Invalid NBT tag:");
			Util.warning("  -" + value);
		}
		item.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
	}

	public String getNBT(org.bukkit.inventory.ItemStack i) {
		NBTTagCompound nbt = CraftItemStack.asNMSCopy(i).getTag();
		if (nbt == null) return null;
		return nbt.toString();
	}

}
