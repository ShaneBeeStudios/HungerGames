package tk.shanebee.hg.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.data.KitEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class KitManager {

	private HashMap<String, KitEntry> kititems = new HashMap<>();

	/** Set a kit for a player
	 * @param player The player to set the kit for
	 * @param kitName The name of the kit to set
	 */
	public void setKit(Player player, String kitName) {
		if (!kititems.containsKey(kitName)) {
			Util.scm(player, ChatColor.RED + kitName + HG.plugin.getLang().kit_doesnt_exist);
			Util.scm(player, "&9&lKits:&b" + getKitListString());
		} else if (!kititems.get(kitName).hasKitPermission(player))
			Util.scm(player, HG.plugin.getLang().kit_no_perm);
		else {
			kititems.get(kitName).setInventoryContent(player);
		}
	}

	/** Get a list of kits in this KitManager
	 * @return A string of all kits
	 */
	public String getKitListString() {
		StringBuilder kits = new StringBuilder();
		for (String s : kititems.keySet()) {
			kits.append(", ").append(s);
		}
		return kits.substring(1);
	}

	/** Get a list of kits in this KitManager
	 * @return A list of all kit's names
	 */
	public List<String> getKitList() {
		return new ArrayList<>(kititems.keySet());
	}

	/** Get the kits for this KitManager
	 * @return A map of the kits
	 */
	public HashMap<String, KitEntry> getKits() {
		return this.kititems;
	}

	/** Add a kit to this KitManager
	 * @param name The name of the kit
	 * @param kit The KitEntry to add
	 */
	public void addKit(String name, KitEntry kit) {
		kititems.put(name, kit);
	}

	/** Remove a kit entry from this KitManager
	 * @param name The kit entry to remove
	 */
	public void removeKit(String name) {
		kititems.remove(name);
	}

	/**
	 * Clear the kit entries in this KitManager
	 */
	public void clearKits() {
		kititems.clear();
	}

}