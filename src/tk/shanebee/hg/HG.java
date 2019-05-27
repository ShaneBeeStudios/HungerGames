package tk.shanebee.hg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import tk.shanebee.hg.commands.*;
import tk.shanebee.hg.data.Data;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.data.Leaderboard;
import tk.shanebee.hg.data.RandomItems;
import tk.shanebee.hg.listeners.CancelListener;
import tk.shanebee.hg.listeners.CommandListener;
import tk.shanebee.hg.listeners.GameListener;
import tk.shanebee.hg.listeners.WandListener;
import tk.shanebee.hg.managers.ItemStackManager;
import tk.shanebee.hg.managers.KillManager;
import tk.shanebee.hg.managers.KitManager;
import tk.shanebee.hg.managers.Manager;
import tk.shanebee.hg.metrics.Metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HG extends JavaPlugin {

	//Maps
	public HashMap<String, BaseCmd> cmds = new HashMap<>();
	public HashMap<UUID, PlayerData> players = new HashMap<>();
	public HashMap<UUID, PlayerSession> playerses = new HashMap<>();
	public HashMap<Integer, ItemStack> items = new HashMap<>();

	//Lists
	public List<Game> games = new ArrayList<>();

	//Instances
	public static HG plugin;
	public static Manager manager;
	public static Data arenaconfig;
	public static KillManager killmanager;
	public static RandomItems ri;
	public static Language lang;
	public KitManager kit;
	public ItemStackManager ism;
	public Leaderboard leaderboard;

	@Override
	public void onEnable() {
		new Config(this);
		Metrics metrics = new Metrics(this);
		if (metrics.isEnabled())
			Util.log("&7Metrics has been &aenabled");
		else
			Util.log("&7Metrics has been &cdisabled");
		plugin = this;
		lang = new Language(this);
		arenaconfig = new Data(this);
		killmanager = new KillManager();
		kit = new KitManager();
		ism = new ItemStackManager(this);
		ri = new RandomItems(this);
		manager = new Manager(this);
		leaderboard = new Leaderboard(this);

		getCommand("hg").setExecutor(new CommandListener(this));
		getServer().getPluginManager().registerEvents(new WandListener(this), this);
		getServer().getPluginManager().registerEvents(new CancelListener(this), this);
		getServer().getPluginManager().registerEvents(new GameListener(this), this);
		loadCmds();
		Util.log("HungerGames has been enabled!");
		if (this.getDescription().getVersion().contains("Beta")) {
			Util.log("&eYOU ARE RUNNING A BETA VERSION, please use with caution");
			Util.log("&eReport any issues to: &bhttps://github.com/ShaneBeeTK/HungerGames/issues");
		}
	}

	@Override
	public void onDisable() {
		stopAll();
		plugin = null;
		manager = null;
		arenaconfig = null;
		killmanager = null;
		kit = null;
		ism = null;
		ri = null;
		Util.log("HungerGames has been disabled!");
	}

	private void loadCmds() {
		cmds.put("team", new TeamCmd());
		cmds.put("addspawn", new AddSpawnCmd());
		cmds.put("create", new CreateCmd());
		cmds.put("join", new JoinCmd());
		cmds.put("leave", new LeaveCmd());
		cmds.put("reload", new ReloadCmd());
		cmds.put("setlobbywall", new SetLobbyWallCmd());
		cmds.put("wand", new WandCmd());
		cmds.put("kit", new KitCmd());
		cmds.put("debug", new DebugCmd());
		cmds.put("list", new ListCmd());
		cmds.put("listgames", new ListGamesCmd());
		cmds.put("forcestart", new StartCmd());
		cmds.put("stop", new StopCmd());
		cmds.put("toggle", new ToggleCmd());
		cmds.put("setexit", new SetExitCmd());
		cmds.put("delete", new DeleteCmd());

		ArrayList<String> cArray = new ArrayList<>();
		cArray.add("join");
		cArray.add("leave");
		cArray.add("kit");
		cArray.add("listgames");
		cArray.add("list");

		for (String bc : cmds.keySet()) {
			getServer().getPluginManager().addPermission(new Permission("hg." + bc));
			if (cArray.contains(bc))
				getServer().getPluginManager().getPermission("hg." + bc).setDefault(PermissionDefault.TRUE);

		}
	}

	public void stopAll() {
		ArrayList<UUID> ps = new ArrayList<>();
		for (Game g : games) {
			g.cancelTasks();
			g.forceRollback();
			ps.addAll(g.getPlayers());
		}
		for (UUID u : ps) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				players.get(u).getGame().leave(p, false);
			}
		}
		players.clear();
		games.clear();
	}

}
