package tk.shanebee.hg.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.ArenaConfig;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.managers.Manager;
import tk.shanebee.hg.managers.PlayerManager;
import tk.shanebee.hg.util.Util;

public abstract class BaseCmd {

    HG plugin;
    Language lang;
    PlayerManager playerManager;
    Manager gameManager;
    ArenaConfig arenaConfig;

    public BaseCmd() {
        this.plugin = HG.getPlugin();
    }

	public CommandSender sender;
	public String[] args;
	public String cmdName;
	public int argLength = 0;
	public boolean forcePlayer = true;
	public boolean forceInGame = false;
	public boolean forceInRegion = false;
	public String usage = "";
	public Player player;

	public boolean processCmd(HG plugin, CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
		this.playerManager = plugin.getPlayerManager();
		this.gameManager = plugin.getManager();
		this.arenaConfig = plugin.getArenaConfig();
		this.lang = plugin.getLang();

		if (forcePlayer) {
			if (!(sender instanceof Player)) {
			    Util.sendPrefixedMessage(sender, "&cThe command &7&l<&rhg %s&7&l> &ccan only be run in game!", cmdName);
			    return false;
            }
			else player = (Player) sender;
		}
		if (!sender.hasPermission("hg." + cmdName))
			Util.sendPrefixedMessage(this.sender, lang.cmd_base_noperm.replace("<command>", cmdName));
		else if (forceInGame && !playerManager.hasPlayerData(player) && !playerManager.hasSpectatorData(player))
			Util.sendPrefixedMessage(this.sender, lang.cmd_base_nogame);
		else if (forceInRegion && !gameManager.isInRegion(player.getLocation()))
			Util.sendPrefixedMessage(this.sender, lang.cmd_base_noregion);
		else if (argLength > args.length)
			Util.sendPrefixedMessage(sender, lang.cmd_base_wrongusage + " " + sendHelpLine());
		else return run();
		return true;
	}

	public abstract boolean run();

	public String sendHelpLine() {
		return "&3&l/hg &b" + cmdName + " &6" + usage.replaceAll("<", "&7&l<&f").replaceAll(">", "&7&l>");
	}

}
