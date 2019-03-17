package me.minebuilders.hg.commands;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.PlayerSession;
import me.minebuilders.hg.Util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WandCmd extends BaseCmd {

	public WandCmd() {
		forcePlayer = true;
		cmdName = "wand";
		argLength = 1;
	}

	@Override
	public boolean run() {
		if (HG.plugin.playerses.containsKey(player.getUniqueId())) {
			HG.plugin.playerses.remove(player.getUniqueId());
			Util.msg(player, "Wand disabled!");
		} else {
			player.getInventory().addItem(new ItemStack(Material.BLAZE_ROD, 1));
			HG.plugin.playerses.put(player.getUniqueId(), new PlayerSession(null, null));
			Util.msg(player, "Wand enabled!");
		}
        return true;
	}
}