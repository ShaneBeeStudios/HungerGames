package tk.shanebee.hg.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

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
			Util.scm(sender, HG.plugin.lang.cmd_base_noperm.replace("<command>", cmdName));
		else if (forceInGame && !HG.plugin.players.containsKey(player.getUniqueId()) && !HG.plugin.getSpectators().containsKey(player.getUniqueId()))
			Util.scm(sender, HG.plugin.lang.cmd_base_nogame);
		else if (forceInRegion && !HG.plugin.getManager().isInRegion(player.getLocation()))
			Util.scm(sender, HG.plugin.lang.cmd_base_noregion);
		else if (argLength > arg.length)
			Util.scm(s, HG.plugin.lang.cmd_base_wrongusage + " " + sendHelpLine());
		else return run();
		return true;
	}

	public abstract boolean run();

	public String sendHelpLine() {
		return "&3&l/hg &b" + cmdName + " &6" + usage.replaceAll("<", "&7&l<&f").replaceAll(">", "&7&l>");
	}
}
