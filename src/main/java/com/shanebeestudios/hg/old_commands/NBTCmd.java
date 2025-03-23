package com.shanebeestudios.hg.old_commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.util.NBTApi;

public class NBTCmd extends BaseCmd {

	private NBTApi api;

	public NBTCmd() {
		forcePlayer = true;
		cmdName = "nbt";
		forceInGame = false;
		argLength = 1;
		usage = "";
		api = plugin.getNbtApi();
	}


	@Override
	public boolean run() {
		Player player = (Player) sender;
		CommandSender console = Bukkit.getConsoleSender();
		if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
			ItemStack item = player.getInventory().getItemInMainHand();
			Material type = item.getType();
			Util.sendMessage(player, "&3NBT:");
			String nbtString = api.getNBT(item);
			if (nbtString == null) {
				Util.sendMessage(player, "&cNO NBT FOUND!");
			} else {
				Util.sendMessage(player, type.toString() + " " + item.getAmount() + " data:" + nbtString.replace(" ", "~"));
				Util.sendMessage(player, "&6NBT String also sent to console for easy copy/pasting");
				Util.sendMessage(console, "&3NBT string from &b" + player.getName() + "&3:");
                System.out.println(type.toString() + " " + item.getAmount() + " data:" + nbtString.replace(" ", "~"));
			}

		}
		return true;
	}

}
