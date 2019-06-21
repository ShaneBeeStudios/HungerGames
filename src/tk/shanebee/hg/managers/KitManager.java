package tk.shanebee.hg.managers;

import java.util.HashMap;

import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;
import tk.shanebee.hg.data.KitEntry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class KitManager {

	private HashMap<String, KitEntry> kititems = new HashMap<>();

	/** Set a kit for a player
	 * @param player The player to set the kit for
	 * @param kitName The name of the kit to set
	 */
	public void setKit(Player player, String kitName) {
		if (!kititems.containsKey(kitName)) {
			Util.scm(player, ChatColor.RED + kitName + HG.plugin.lang.kit_doesnt_exist);
			Util.scm(player, "&9&lKits:&b" + getKitList());
		} else if (!kititems.get(kitName).hasKitPermission(player))
			Util.msg(player, HG.plugin.lang.kit_no_perm);
		else {
			kititems.get(kitName).setInventoryContent(player);
		}
	}

	/** Get a list of kits in this KitManager
	 * @return A string list of all kits
	 */
	public String getKitList() {
		StringBuilder kits = new StringBuilder();
		for (String s : kititems.keySet()) {
			kits.append(", ").append(s);
		}
		return kits.substring(1);
	}

	/** Get the kits for this KitManager
	 * @return The kits
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
}