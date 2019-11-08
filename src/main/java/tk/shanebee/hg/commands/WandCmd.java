package tk.shanebee.hg.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.PlayerSession;
import tk.shanebee.hg.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class WandCmd extends BaseCmd {

	public WandCmd() {
		forcePlayer = true;
		cmdName = "wand";
		argLength = 1;
	}

	@Override
	public boolean run() {
		if (plugin.getPlayerSessions().containsKey(player.getUniqueId())) {
			plugin.getPlayerSessions().remove(player.getUniqueId());
			Util.scm(player, "Wand disabled!");
		} else {
			ItemStack wand = new ItemStack(Material.BLAZE_ROD, 1);
			ItemMeta meta = wand.getItemMeta();
            assert meta != null;
            meta.setDisplayName(Util.getColString("&3HungerGames Wand"));
			meta.setLore(new ArrayList<>(Arrays.asList(
					Util.getColString("&7Left-Click to set position 1"),
                    Util.getColString("&7Right-Click to set position 2")
			)));
			wand.setItemMeta(meta);
			player.getInventory().addItem(wand);
			plugin.getPlayerSessions().put(player.getUniqueId(), new PlayerSession(null, null));
			Util.scm(player, "Wand enabled!");
		}
		return true;
	}

}