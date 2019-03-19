package me.minebuilders.hg.commands;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCmd {

	public CommandSender sender;
	public String[] args;
	public String cmdName;
	public int argLength = 0;
	public boolean forcePlayer = true;
	public boolean forceInGame = false;
	public boolean forceInRegion = false;
	public String usage = "";
	public Player player;

	public boolean processCmd(HG p, CommandSender s, String[] arg) {
		sender = s;
		args = arg;

		if (forcePlayer) {
			if (!(s instanceof Player)) return false;
			else player = (Player) s;
		}
		if (!s.hasPermission("hg." + cmdName))
			sender.sendMessage(ChatColor.RED + "You do not have permission to use: " + ChatColor.RED + "/hg " + cmdName);
		else if (forceInGame && !HG.plugin.players.containsKey(player.getUniqueId()))
			sender.sendMessage(ChatColor.RED + "Your not in a valid game!");
		else if (forceInRegion && !HG.manager.isInRegion(player.getLocation()))
			sender.sendMessage(ChatColor.RED + "Your not in a valid HungerGames region!");
		else if (argLength > arg.length)
		Util.scm(s, "&4Wrong usage: " + sendHelpLine());
		else return run();
		return true;
	}

	public abstract boolean run();

	public String sendHelpLine() {
		return "&3&l/hg &b" + cmdName + " &6" + usage.replaceAll("<", "&7&l<&f").replaceAll(">", "&7&l>");
	}
}
