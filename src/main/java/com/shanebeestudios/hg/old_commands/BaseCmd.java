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

		return true;
	}

	public abstract boolean run();

	public String sendHelpLine() {
		return "&3&l/hg &b" + cmdName + " &6" + usage.replaceAll("<", "&7&l<&f").replaceAll(">", "&7&l>");
	}

}
