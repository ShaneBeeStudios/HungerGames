package com.shanebeestudios.hg.old_commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.ArenaConfig;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.managers.GameManager;
import com.shanebeestudios.hg.managers.PlayerManager;
import com.shanebeestudios.hg.api.util.Util;

public abstract class BaseCmd {

    HungerGames plugin;
    Language lang;
    PlayerManager playerManager;
    GameManager gameManager;
    ArenaConfig arenaConfig;

    public BaseCmd() {
        this.plugin = HungerGames.getPlugin();
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

	public boolean processCmd(HungerGames plugin, CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
		this.playerManager = plugin.getPlayerManager();
		this.gameManager = plugin.getGameManager();
		this.arenaConfig = plugin.getArenaConfig();
		this.lang = plugin.getLang();

		if (forcePlayer) {
			if (!(sender instanceof Player)) return false;
			else player = (Player) sender;
		}
		if (!sender.hasPermission("hg." + cmdName))
			Util.scm(this.sender, lang.cmd_base_noperm.replace("<command>", cmdName));
		else if (forceInGame && !playerManager.hasPlayerData(player) && !playerManager.hasSpectatorData(player))
			Util.scm(this.sender, lang.cmd_base_nogame);
		else if (forceInRegion && !gameManager.isInRegion(player.getLocation()))
			Util.scm(this.sender, lang.cmd_base_noregion);
		else if (argLength > args.length)
			Util.scm(sender, lang.cmd_base_wrongusage + " " + sendHelpLine());
		else return run();
		return true;
	}

	public abstract boolean run();

	public String sendHelpLine() {
		return "&3&l/hg &b" + cmdName + " &6" + usage.replaceAll("<", "&7&l<&f").replaceAll(">", "&7&l>");
	}

}
