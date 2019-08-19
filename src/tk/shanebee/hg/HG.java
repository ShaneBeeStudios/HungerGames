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
import tk.shanebee.hg.managers.*;
import tk.shanebee.hg.metrics.Metrics;
import tk.shanebee.hg.metrics.MetricsHandler;
import tk.shanebee.hg.nms.NBTApi;

import java.util.*;

public class HG extends JavaPlugin {

	//Maps
	private Map<String, BaseCmd> cmds = new HashMap<>();
	private Map<UUID, PlayerData> players = new HashMap<>();
	private Map<UUID, PlayerData> spectators = new HashMap<>();
	private Map<UUID, PlayerSession> playerSession = new HashMap<>();
	private Map<Integer, ItemStack> items = new HashMap<>();
	private Map<Integer, ItemStack> bonusItems = new HashMap<>();

	//Lists
	private List<Game> games = new ArrayList<>();

	//Instances
	public static HG plugin;
	private Manager manager;
	private Data arenaconfig;
	private KillManager killManager;
	private RandomItems randomItems;
	private Language lang;
	private KitManager kitManager;
	private ItemStackManager itemStackManager;
	private Leaderboard leaderboard;
	private Metrics metrics;

	//NMS Nbt
	private NBTApi nbtApi;

	@Override
	public void onEnable() {
		plugin = this;
		new Config(this);
		metrics = new Metrics(this);
		if (metrics.isEnabled()) {
			Util.log("&7Metrics have been &aenabled");
			new MetricsHandler(false);
		}
		else
			Util.log("&7Metrics have been &cdisabled");
		String nms = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			nbtApi = (NBTApi) Class.forName(HG.class.getPackage().getName() + ".nms.NMS_" + nms).newInstance();
			Util.log("&7Compatible NMS version: &b" + nms);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			Util.warning("NMS version " + nms + " not supported, item/kit \"data\" will not be supported");
			nbtApi = null;
		}
		lang = new Language(this);
		kitManager = new KitManager();
		itemStackManager = new ItemStackManager(this);
		randomItems = new RandomItems(this);
		arenaconfig = new Data(this);
		killManager = new KillManager();

		manager = new Manager(this);
		leaderboard = new Leaderboard(this);
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new Placeholders(this).register();
			Util.log("&7PAPI found, Placeholders have been &aenabled");
		} else {
			Util.log("&7PAPI not found, Placeholders have been &cdisabled");
		}
		//noinspection ConstantConditions
		getCommand("hg").setExecutor(new CommandListener(this));
		getServer().getPluginManager().registerEvents(new WandListener(this), this);
		getServer().getPluginManager().registerEvents(new CancelListener(this), this);
		getServer().getPluginManager().registerEvents(new GameListener(this), this);
		loadCmds();

		if (this.getDescription().getVersion().contains("Beta")) {
			Util.log("&eYOU ARE RUNNING A BETA VERSION, please use with caution");
			Util.log("&eReport any issues to: &bhttps://bitbucket.org/ShaneBeeStudios/hungergames/issues");
		}

		Util.log("HungerGames has been &benabled!");
	}

	@Override
	public void onDisable() {
		stopAll();
		plugin = null;
		manager = null;
		arenaconfig = null;
		killManager = null;
		kitManager = null;
		itemStackManager = null;
		randomItems = null;
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
		cmds.put("chestrefill", new ChestRefillCmd());
		cmds.put("bordersize", new BorderSizeCmd());
		cmds.put("bordercenter", new BorderCenterCmd());
		cmds.put("bordertimer", new BorderTimerCmd());
		if (Config.spectateEnabled) {
			cmds.put("spectate", new SpectateCmd());
		}
		if (nbtApi != null) {
			cmds.put("nbt", new NBTCmd());
		}

		ArrayList<String> cArray = new ArrayList<>();
		cArray.add("join");
		cArray.add("leave");
		cArray.add("kit");
		cArray.add("listgames");
		cArray.add("list");

		for (String bc : cmds.keySet()) {
			getServer().getPluginManager().addPermission(new Permission("hg." + bc));
			if (cArray.contains(bc))
				//noinspection ConstantConditions
				getServer().getPluginManager().getPermission("hg." + bc).setDefault(PermissionDefault.TRUE);

		}
	}

	/**
	 * Stop all games
	 */
	public void stopAll() {
		ArrayList<UUID> ps = new ArrayList<>();
		for (Game g : games) {
			g.cancelTasks();
			g.forceRollback();
			ps.addAll(g.getPlayers());
			ps.addAll(g.getSpectators());
		}
		for (UUID u : ps) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				if (players.containsKey(u))
					players.get(u).getGame().leave(p, false);
				if (spectators.containsKey(u))
					spectators.get(u).getGame().leaveSpectate(p);
			}
		}
		players.clear();
		spectators.clear();
		games.clear();
	}

	/** Get the instance of this plugin
	 * @return This plugin
	 */
	public static HG getPlugin() {
		return plugin;
	}

	/** Get an instance of the RandomItems manager
	 * @return RandomItems manager
	 */
	public RandomItems getRandomItems() {
		return this.randomItems;
	}

	/** Get an instance of the KillManager
	 * @return KillManager
	 */
	public KillManager getKillManager() {
		return this.killManager;
	}

	/** Get an instance of the plugins main kit manager
	 * @return The kit manager
	 */
	public KitManager getKitManager() {
		return this.kitManager;
	}

	/** Get an instance of the ItemStackManager
	 * @return ItemStackManager
	 */
	public ItemStackManager getItemStackManager() {
		return this.itemStackManager;
	}

	/** Get the instance of the manager
	 * @return The manager
	 */
	public Manager getManager() {
		return this.manager;
	}

	/** Get an instance of the ArenaConfig
	 * @return ArenaConfig
	 */
	public Data getArenaConfig() {
		return this.arenaconfig;
	}

	/** Get an instance of HG's leaderboards
	 * @return Leaderboard
	 */
	public Leaderboard getLeaderboard() {
		return this.leaderboard;
	}

	/** Get a list of all loaded games
	 * @return A list of games
	 */
	public List<Game> getGames() {
		return this.games;
	}

	/** Get the players currently in games
	 * @return Map of player data
	 */
	public Map<UUID, PlayerData> getPlayers() {
		return this.players;
	}

	/** Get a map of players currently spectating games
	 * @return Map of spectators
	 */
	public Map<UUID, PlayerData> getSpectators() {
		return this.spectators;
	}

	/** Get player sessions map
	 * @return Player Sessions map
	 */
	public Map<UUID, PlayerSession> getPlayerSessions() {
		return this.playerSession;
	}

	/** Get general items map
	 * @return Items map
	 */
	public Map<Integer, ItemStack> getItems() {
		return this.items;
	}

	/** Get general bonus items map
	 * @return Bonus items map
	 */
	public Map<Integer, ItemStack> getBonusItems() {
		return this.bonusItems;
	}

	/** Get a map of commands
	 * @return Map of commands
	 */
	public Map<String, BaseCmd> getCommands() {
		return this.cmds;
	}

	/** Get an instance of the language file
	 * @return Language file
	 */
	public Language getLang() {
		return this.lang;
	}

	/** Get the NBT API
	 * @return NBT API
	 */
	public NBTApi getNbtApi() {
		return this.nbtApi;
	}

	public static boolean isRunningMinecraft(int major, int minor) {
		int maj = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[0].replace("v", ""));
		int min = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]);
		return maj >= major && min >= minor;
	}

	public Metrics getMetrics() {
		return this.metrics;
	}

}
