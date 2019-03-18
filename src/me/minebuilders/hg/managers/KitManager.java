package me.minebuilders.hg.managers;

import java.util.HashMap;

import me.minebuilders.hg.Util;
import me.minebuilders.hg.data.KitEntry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitManager {

	public HashMap<String, KitEntry> kititems = new HashMap<>();
	
	public void setkit(Player p, String path) {
		if (!kititems.containsKey(path)) {
			Util.scm(p, ChatColor.RED + path + " Doesn't exist!");
			Util.scm(p, "&9&lKits:&b" + getKitList());
		} else if (!kititems.get(path).hasKitPermission(p))
			Util.msg(p, ChatColor.RED + "You don't have permission to use this kit!");
		else {
			kititems.get(path).setInventoryContent(p);
		}
	}
	
	public String getKitList() {
		String kits = "";
		for (String s : kititems.keySet()) {
			kits = kits + ", " + s;
		}
		return kits.substring(1);
	}
}