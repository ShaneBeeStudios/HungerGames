package tk.shanebee.hg.listeners;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.data.PlayerData;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.game.Team;
import tk.shanebee.hg.managers.PlayerManager;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.commands.BaseCmd;

import java.util.*;

/**
 * Internal command listener
 */
@SuppressWarnings("NullableProblems")
public class CommandListener implements CommandExecutor, TabCompleter {

	private final HG plugin;
	private final PlayerManager playerManager;

	public CommandListener(HG plugin) {
		this.plugin = plugin;
		this.playerManager = plugin.getPlayerManager();
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
		if (args.length == 0 || !plugin.getCommands().containsKey(args[0])) {
			Util.scm(s, "&4*&c&m                         &7*( &3&lHungerGames &7)*&c&m                          &4*");
			for (BaseCmd cmd : plugin.getCommands().values().toArray(new BaseCmd[0])) {
				if (s.hasPermission("hg." + cmd.cmdName)) Util.scm(s, "  &7&l- " + cmd.sendHelpLine());
			}
			Util.scm(s, "&4*&c&m                                                                             &4*");
		} else plugin.getCommands().get(args[0]).processCmd(plugin, s, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
		if (args.length == 1) {
			ArrayList<String> matches = new ArrayList<>();
			for (String name : plugin.getCommands().keySet()) {
				if (StringUtil.startsWithIgnoreCase(name, args[0])) {
					if (sender.hasPermission("hg." + name))
						matches.add(name);
				}
			}
			return matches;
		} else if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("team") && sender instanceof Player) {
				if (args.length == 2) {
					List<String> listTeam = new ArrayList<>();
					listTeam.add("invite");
					listTeam.add("accept");
					listTeam.add("create");
					if (sender.hasPermission("hg.team.tp")) {
					    listTeam.add("tp");
					}
					ArrayList<String> matchesTeam = new ArrayList<>();
					for (String name : listTeam) {
						if (StringUtil.startsWithIgnoreCase(name, args[1])) {
							matchesTeam.add(name);
						}
					}
					return matchesTeam;
				}
				if (args.length == 3 && args[1].equalsIgnoreCase("tp")) {
                    PlayerData pd = playerManager.getPlayerData(((Player) sender).getUniqueId());
                    if (pd == null) return ImmutableList.of();
                    Team team = pd.getTeam();
                    if (team != null) {
                        List<String> teamMembers = new ArrayList<>();
                        for (UUID member : team.getPlayers()) {
                            Player player = Bukkit.getPlayer(member);
                            if (player != null) {
                                teamMembers.add(player.getName());
                            }
                        }
                        ArrayList<String> matchesTeam = new ArrayList<>();
                        for (String name : teamMembers) {
                            if (StringUtil.startsWithIgnoreCase(name, args[2])) {
                                matchesTeam.add(name);
                            }
                        }
                        return matchesTeam;
                    }
                }
				return null;
			} else if (args[0].equalsIgnoreCase("delete") ||
					args[0].equalsIgnoreCase("debug") ||
					args[0].equalsIgnoreCase("stop") ||
					(args[0].equalsIgnoreCase("forcestart")) ||
					(args[0].equalsIgnoreCase("join")) ||
					(args[0].equalsIgnoreCase("setlobbywall")) ||
					(args[0].equalsIgnoreCase("setexit")) ||
					(args[0].equalsIgnoreCase("toggle")) ||
					(args[0].equalsIgnoreCase("bordercenter")) ||
					((args[0].equalsIgnoreCase("spectate")) && Config.spectateEnabled)) {
				ArrayList<String> matchesDelete = new ArrayList<>();
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("setexit") && StringUtil.startsWithIgnoreCase("all", args[1])) {
						matchesDelete.add("all");
					}
					for (Game game : plugin.getGames()) {
						String name = game.getGameArenaData().getName();
						if (StringUtil.startsWithIgnoreCase(name, args[1])) {
							matchesDelete.add(name);
						}
					}
					return matchesDelete;
				}
			} else if (args[0].equalsIgnoreCase("chestrefill")) {
				ArrayList<String> matchesDelete = new ArrayList<>();
				if (args.length == 2) {
					for (Game game : plugin.getGames()) {
						String name = game.getGameArenaData().getName();
						if (StringUtil.startsWithIgnoreCase(name, args[1])) {
							matchesDelete.add(name);
						}
					}
					return matchesDelete;
				}
				if (args.length == 3) {
					ArrayList<String> matchesChestRefill = new ArrayList<>();
					for (int i = 30; i <= 120; i = i + 30) {
						if (StringUtil.startsWithIgnoreCase(String.valueOf(i), args[2])) {
							matchesChestRefill.add(String.valueOf(i));
						}
						if (StringUtil.startsWithIgnoreCase("<time=seconds>", args[2])) {
							matchesChestRefill.add("<time=seconds>");
						}
					}
					return matchesChestRefill;
				}
			} else if (args[0].equalsIgnoreCase("bordersize")) {
				ArrayList<String> matchesDelete = new ArrayList<>();
				if (args.length == 2) {
					for (Game game : plugin.getGames()) {
						String name = game.getGameArenaData().getName();
						if (StringUtil.startsWithIgnoreCase(name, args[1])) {
							matchesDelete.add(name);
						}
					}
					return matchesDelete;
				}
				if (args.length == 3 && args[0].equalsIgnoreCase("bordersize")) {
					return Collections.singletonList("<size=diameter>");
				}
			} else if (args[0].equalsIgnoreCase("bordertimer")) {
				ArrayList<String> matchesDelete = new ArrayList<>();
				if (args.length == 2) {
					for (Game game : plugin.getGames()) {
						String name = game.getGameArenaData().getName();
						if (StringUtil.startsWithIgnoreCase(name, args[1])) {
							matchesDelete.add(name);
						}
					}
					return matchesDelete;
				}
				if (args.length == 3 && args[0].equalsIgnoreCase("bordertimer")) {
					return Collections.singletonList("<start=remaining seconds>");
				}
				if (args.length == 4 && args[0].equalsIgnoreCase("bordertimer")) {
					return Collections.singletonList("<end=remaining seconds>");
				}
			} else if (args[0].equalsIgnoreCase("kit")) {
				if (args.length == 2) {
					ArrayList<String> matchesKit = new ArrayList<>();
					Game game = null;
					if (playerManager.hasPlayerData(((Player) sender).getUniqueId())) {
						game = playerManager.getPlayerData(((Player) sender).getUniqueId()).getGame();
					}
					if (game != null) {
						for (String name : game.getKitManager().getKits().keySet()) {
							if (StringUtil.startsWithIgnoreCase(name, args[1])) {
								matchesKit.add(name);
							}
						}
						return matchesKit;
					} else {
						return Collections.singletonList("<not-in-game>");
					}
				}
			} else if (args[0].equalsIgnoreCase("create")) {
				ArrayList<String> matchesCreate = new ArrayList<>();
				switch (args.length) {
					case 2:
						if (StringUtil.startsWithIgnoreCase("<arena-name>", args[1]))
							matchesCreate.add("<arena-name>");
						break;
					case 3:
						if (StringUtil.startsWithIgnoreCase("<min-players>", args[2]))
							matchesCreate.add("<min-players>");
						break;
					case 4:
						if (StringUtil.startsWithIgnoreCase("<max-players>", args[3]))
							matchesCreate.add("<max-players>");
						break;
					case 5:
						if (StringUtil.startsWithIgnoreCase("<time-seconds>", args[4]))
							matchesCreate.add("<time-seconds>");
						break;
					case 6:
						if (StringUtil.startsWithIgnoreCase("<cost>", args[5]))
							matchesCreate.add("<cost>");
						break;
				}
				return matchesCreate;
			}
		}
		return Collections.emptyList();
	}

}
