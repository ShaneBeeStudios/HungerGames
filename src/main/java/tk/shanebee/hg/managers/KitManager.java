package tk.shanebee.hg.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.KitEntry;
import tk.shanebee.hg.data.PlayerData;
import tk.shanebee.hg.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * General manager for kits
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class KitManager {

	private final HashMap<String, KitEntry> kititems = new HashMap<>();

	/** Set a kit for a player
	 * @param player The player to set the kit for
	 * @param kitName The name of the kit to set
	 */
	public void setKit(Player player, String kitName) {
		if (!kititems.containsKey(kitName)) {
			Util.scm(player, ChatColor.RED + kitName + HG.getPlugin().getLang().kit_doesnt_exist);
			Util.scm(player, "&9&lKits:&b" + getKitListString(player));
		} else if (!kititems.get(kitName).hasKitPermission(player))
			Util.scm(player, HG.getPlugin().getLang().kit_no_perm);
		else {
			kititems.get(kitName).setInventoryContent(player);
		}
	}

    /** Get a list of kits in this KitManager based on a player's permission
     * @param player Player to check for permissions
     * @return A string of all kits this player has access to
     */
    public String getKitListString(Player player) {
        StringBuilder kits = new StringBuilder();
        if (kititems.size() > 0) {
            for (String s : kititems.keySet()) {
                if (kititems.get(s).hasKitPermission(player)) {
                    kits.append(", ").append(s);
                }
            }
            if (kits.length() > 1) {
                return kits.substring(1);
            }
        }
        return null;
    }

	/** Get a list of kits in this KitManager
	 * @return A list of all kit's names
	 */
	public List<String> getKitList() {
		return new ArrayList<>(kititems.keySet());
	}

    /** Get a list of kits the player has permission for
     * @param player Player to check permission for
     * @return List of kits a player has permission for
     */
	public List<String> getKits(Player player) {
	    List<String> kits = new ArrayList<>();
	    for (String kit : this.kititems.keySet()) {
	        if (this.kititems.get(kit).hasKitPermission(player)) {
	            kits.add(kit);
            }
        }
	    return kits;
    }

	/** Get the kits for this KitManager
	 * @return A map of the kits
	 */
	public HashMap<String, KitEntry> getKits() {
		return this.kititems;
	}

    /** Check if this KitManager actually has kits
     * @return True if kits exist
     */
	public boolean hasKits() {
	    return this.kititems.size() > 0;
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
