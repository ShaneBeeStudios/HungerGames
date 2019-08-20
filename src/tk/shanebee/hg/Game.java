package tk.shanebee.hg;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tk.shanebee.hg.data.Leaderboard;
import tk.shanebee.hg.events.GameStartEvent;
import tk.shanebee.hg.events.PlayerJoinGameEvent;
import tk.shanebee.hg.events.PlayerLeaveGameEvent;
import tk.shanebee.hg.managers.KitManager;
import tk.shanebee.hg.mobhandler.Spawner;
import tk.shanebee.hg.tasks.ChestDropTask;
import tk.shanebee.hg.tasks.FreeRoamTask;
import tk.shanebee.hg.tasks.StartingTask;
import tk.shanebee.hg.tasks.TimerTask;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("unused")
public class Game {

	private HG plugin;
	private String name;
	private List<Location> spawns;
	private Bound bound;
	private List<UUID> players = new ArrayList<>();
	private List<UUID> spectators = new ArrayList<>();
	private List<Location> chests = new ArrayList<>();
	private List<Location> playerChests = new ArrayList<>();
	private Map<Integer, ItemStack> items;
	private Map<Integer, ItemStack> bonusItems;
	private KitManager kit;
	private Map<Player, Integer> kills = new HashMap<>();

	private List<BlockState> blocks = new ArrayList<>();
	private List<String> commands = null;
	private Location exit;
	private Status status;
	private int minPlayers;
	private int maxPlayers;
	private int time;
	private Sign s;
	private Sign s1;
	private Sign s2;
	private int roamTime;
	private SBDisplay sb;
	private int chestRefillTime = 0;

	// Task ID's here!
	private Spawner spawner;
	private FreeRoamTask freeRoam;
	private StartingTask starting;
	private TimerTask timer;
	private ChestDropTask chestDrop;

	private BossBar bar;

	// Border stuff here
	private Location borderCenter = null;
	private int borderSize;
	private int borderCountdownStart;
	private int borderCountdownEnd;

	private boolean spectate = Config.spectateEnabled;
	private boolean spectateOnDeath = Config.spectateOnDeath;

	/** Create a new game
	 * <p>Internally used when loading from config on server start</p>
	 * @param name Name of this game
	 * @param bound Bounding region of this game
	 * @param spawns List of spawns for this game
	 * @param lobbySign Lobby sign block
	 * @param timer Length of the game (in seconds)
	 * @param minPlayers Minimum players to be able to start the game
	 * @param maxPlayers Maximum players that can join this game
	 * @param roam Roam time for this game
	 * @param isReady If the game is ready to start
	 */
	public Game(String name, Bound bound, List<Location> spawns, Sign lobbySign, int timer, int minPlayers, int maxPlayers, int roam, boolean isReady) {
		this.plugin = HG.getPlugin();
		this.name = name;
		this.bound = bound;
		this.spawns = spawns;
		this.s = lobbySign;
		this.time = timer;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.roamTime = roam;
		if (isReady) this.status = Status.READY;
		else this.status = Status.BROKEN;
		this.borderSize = Config.borderFinalSize;
		this.borderCountdownStart = Config.borderCountdownStart;
		this.borderCountdownEnd = Config.borderCountdownEnd;

		setLobbyBlock(lobbySign);

		this.sb = new SBDisplay(this);
		this.kit = plugin.getKitManager();
		this.items = plugin.getItems();
		this.bonusItems = plugin.getBonusItems();
	}

	/** Create a new game
	 * <p>Internally used when creating a game with the <b>/hg create</b> command</p>
	 * @param name Name of this game
	 * @param bound Bounding region of this game
	 * @param timer Length of the game (in seconds)
	 * @param minPlayers Minimum players to be able to start the game
	 * @param maxPlayers Maximum players that can join this game
	 * @param roam Roam time for this game
	 */
	public Game(String name, Bound bound, int timer, int minPlayers, int maxPlayers, int roam) {
		this.plugin = HG.getPlugin();
		this.name = name;
		this.time = timer;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.roamTime = roam;
		this.spawns = new ArrayList<>();
		this.bound = bound;
		this.status = Status.NOTREADY;
		this.sb = new SBDisplay(this);
		this.kit = plugin.getKitManager();
		this.items = plugin.getItems();
		this.bonusItems = plugin.getBonusItems();
		this.borderSize = Config.borderFinalSize;
		this.borderCountdownStart = Config.borderCountdownStart;
		this.borderCountdownEnd = Config.borderCountdownEnd;
	}

	/** Get the bounding region of this game
	 * @return Region of this game
	 */
	public Bound getRegion() {
		return bound;
	}

	/**
	 * Force a rollback for this game
	 */
	public void forceRollback() {
		Collections.reverse(blocks);
		for (BlockState st : blocks) {
			st.update(true);
		}
	}

	public void setItems(HashMap<Integer, ItemStack> items) {
		this.items = items;
	}

	public Map<Integer, ItemStack> getItems() {
		return this.items;
	}

	public void addToItems(ItemStack item) {
		this.items.put(this.items.size() + 1, item);
	}

	public void clearItems() {
		this.items.clear();
	}

	public void resetItemsDefault() {
		this.items = HG.getPlugin().getItems();
	}

	public void setBonusItems(Map<Integer, ItemStack> items) {
		this.bonusItems = items;
	}

	public Map<Integer, ItemStack> getBonusItems() {
		return this.bonusItems;
	}

	public void addToBonusItems(ItemStack item) {
		this.bonusItems.put(this.bonusItems.size() + 1, item);
	}

	public void clearBonusItems() {
		this.bonusItems.clear();
	}

	public void resetBonusItemsDefault() {
		this.bonusItems = HG.getPlugin().getBonusItems();
	}

	/** Set the list of a commands to run for this game
	 * <p><b>format = </b> "type:command"</p>
	 * <p><b>types = </b> start, stop, death, join</p>
	 * @param commands List of commands
	 */
	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	/** Add a command to the list of commands for this game
	 * @param command The command to add
	 * @param type The type of the command
	 */
	public void addCommand(String command, CommandType type) {
		this.commands.add(type.getType() + ":" + command);
	}

	/** Add a kill to a player
	 * @param player The player to add a kill to
	 */
	public void addKill(Player player) {
		this.kills.put(player, this.kills.get(player) + 1);
	}

	public StartingTask getStartingTask() {
		return this.starting;
	}

	/** Set the chest refill time for this game
	 * @param refill Remaining time in game (seconds : 30 second intervals)
	 */
	public void setChestRefill(int refill) {
		this.chestRefillTime = refill;
	}

	/** Set the status of the game
	 * @param status Status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
		updateLobbyBlock();
	}

	/** Set the chest refill time
	 * @param time The remaining time in the game for the chests to refill
	 */
	public void setChestRefillTime(int time) {
		this.chestRefillTime = time;
	}

	/** Get the chest refill time
	 * @return The remaining time in the game which the chests will refill
	 */
	public int getChestRefillTime() {
		return this.chestRefillTime;
	}

	/**
	 * Refill chests in this game
	 */
	public void refillChests() {
		this.chests.clear();
	}

	private void addState(BlockState s) {
		if (s.getType() != Material.AIR) {
			blocks.add(s);
		}
	}

	/** Add a game chest location to the game
	 * @param location Location of the chest to add (Needs to actually be a chest there)
	 */
	public void addGameChest(Location location) {
		chests.add(location);
	}

	/** Add a player placed chest to the game
	 * @param location Location of the chest
	 */
	public void addPlayerChest(Location location) {
		playerChests.add(location);
	}

	/** Check if chest at this location is logged
	 * @param location Location of chest to check
	 * @return True if this chest was added already
	 */
	public boolean isLoggedChest(Location location) {
		return chests.contains(location) || playerChests.contains(location);
	}

	/** Remove a game chest from the game
	 * @param location Location of the chest to remove
	 */
	public void removeGameChest(Location location) {
		chests.remove(location);
	}

	/** Remove a player placed chest from the game
	 * @param location Location of the chest
	 */
	public void removePlayerChest(Location location) {
		playerChests.remove(location);
	}

	/** Record a block as broken in the arena to be restored when the game finishes
	 * @param block The block that was broken
	 */
	public void recordBlockBreak(Block block) {
		Block top = block.getRelative(BlockFace.UP);

		if (!top.getType().isSolid() || !top.getType().isBlock()) {
			addState(block.getRelative(BlockFace.UP).getState());
		}

		for (BlockFace bf : Util.faces) {
			Block rel = block.getRelative(bf);

			if (Util.isAttached(block, rel)) {
				addState(rel.getState());
			}
		}
		addState(block.getState());
	}

	/** Add a block to be restored when the game finishes
	 * @param blockState BlockState to be added to the list
	 */
	public void recordBlockPlace(BlockState blockState) {
		blocks.add(blockState);
	}

	/** Get the status of the game
	 * @return Status of the game
	 */
	public Status getStatus() {
		return this.status;
	}

	List<BlockState> getBlocks() {
		Collections.reverse(blocks);
		return blocks;
	}

	void resetBlocks() {
		this.blocks.clear();
	}

	/** Get a list of all players in the game
	 * @return UUID list of all players in game
	 */
	public List<UUID> getPlayers() {
		return players;
	}

	/** Get the bounding box of this game
	 * @return Bound of this game
	 */
	public Bound getBound() {
		return this.bound;
	}

	public List<UUID> getSpectators() {
		return this.spectators;
	}

	/** Get the name of this game
	 * @return Name of this game
	 */
	public String getName() {
		return this.name;
	}

	/** Check if a location is within the games arena
	 * @param location Location to be checked
	 * @return True if location is within the arena bounds
	 */
	public boolean isInRegion(Location location) {
		return bound.isInRegion(location);
	}

	/** Get a list of all spawn locations
	 * @return All spawn locations
	 */
	public List<Location> getSpawns() {
		return spawns;
	}

	/** Get the roam time of the game
	 * @return The roam time
	 */
	public int getRoamTime() {
		return this.roamTime;
	}

	/** Get the exit location associated with this game
	 * @return Exit location
	 */
	public Location getExit() {
		return this.exit;
	}

	/** Get the location of the lobby for this game
	 * @return Location of the lobby sign
	 */
	public Location getLobbyLocation() {
		return this.s.getLocation();
	}

	/** Get max players for a game
	 * @return Max amount of players for this game
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/** Join a player to the game
	 * @param player Player to join the game
	 */
	public void join(Player player) {
		if (status != Status.WAITING && status != Status.STOPPED && status != Status.COUNTDOWN && status != Status.READY) {
			Util.scm(player, HG.plugin.getLang().arena_not_ready);
			if ((status == Status.RUNNING || status == Status.BEGINNING) && Config.spectateEnabled) {
				Util.scm(player, plugin.getLang().arena_spectate.replace("<arena>", this.getName()));
			}
		} else if (maxPlayers <= players.size()) {
			player.sendMessage(ChatColor.RED + name + " is currently full!");
			Util.scm(player, "&c" + name + " " + HG.plugin.getLang().game_full);
		} else if (!players.contains(player.getUniqueId())) {
			// Call PlayerJoinGameEvent
			PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player);
			Bukkit.getPluginManager().callEvent(event);
			// If cancelled, stop the player from joining the game
			if (event.isCancelled()) return;

			if (player.isInsideVehicle()) {
				player.leaveVehicle();
			}

			players.add(player.getUniqueId());
			Bukkit.getScheduler().scheduleSyncDelayedTask(HG.plugin, () -> {
				Location loc = pickSpawn();
				player.teleport(loc);

				if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
					while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
						loc.setY(loc.getY() - 1);
					}
				}
				HG.plugin.getPlayers().put(player.getUniqueId(), new PlayerData(player, this));
				heal(player);
				freeze(player);
				kills.put(player, 0);

				if (players.size() == 1 && status == Status.READY) {
					status = Status.WAITING;
				}
				if (players.size() >= minPlayers && (status == Status.WAITING || status == Status.READY)) {
					startPreGame();
				} else if (status == Status.WAITING) {
					msgAll(HG.plugin.getLang().player_joined_game.replace("<player>",
							player.getName()) + (minPlayers - players.size() <= 0 ? "!" : ":" +
							HG.plugin.getLang().players_to_start.replace("<amount>", String.valueOf((minPlayers - players.size())))));
				}
				kitHelp(player);

				updateLobbyBlock();
				sb.setSB(player);
				sb.setAlive();
				runCommands(CommandType.JOIN, player);
			}, 5);
		}
	}

	/** Get the kits for this game
	 * @return The KitManager kit for this game
	 */
	public KitManager getKitManager() {
		return this.kit;
	}

	/** Set the kits for this game
	 * @param kit The KitManager kit to set
	 */
	@SuppressWarnings("unused")
	public void setKitManager(KitManager kit) {
		this.kit = kit;
	}

	private void kitHelp(Player player) {
		// Clear the chat a little bit, making this message easier to see
		for(int i = 0; i < 20; ++i)
			Util.scm(player, " ");
		String kit = this.kit.getKitListString();
		Util.scm(player, " ");
		Util.scm(player, HG.plugin.getLang().kit_join_header);
		Util.scm(player, " ");
		if (player.hasPermission("hg.kit")) {
			Util.scm(player, HG.plugin.getLang().kit_join_msg);
			Util.scm(player, " ");
			Util.scm(player, HG.plugin.getLang().kit_join_avail + kit);
			Util.scm(player, " ");
		}
		Util.scm(player, HG.plugin.getLang().kit_join_footer);
		Util.scm(player, " ");
	}

	/**
	 * Respawn all players in the game back to spawn points
	 */
	public void respawnAll() {
		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			if (p != null)
				p.teleport(pickSpawn());
		}
	}

	/**
	 * Start the pregame countdown
	 */
	public void startPreGame() {
		// Call the GameStartEvent
		GameStartEvent event = new GameStartEvent(this);
		Bukkit.getPluginManager().callEvent(event);

        status = Status.COUNTDOWN;
		starting = new StartingTask(this);
		updateLobbyBlock();
	}

	/**
	 * Start the free roam state of the game
	 */
	public void startFreeRoam() {
		status = Status.BEGINNING;
		updateLobbyBlock();
		bound.removeEntities();
		freeRoam = new FreeRoamTask(this);
		runCommands(CommandType.START, null);
	}

	/**
	 * Start the game
	 */
	public void startGame() {
		status = Status.RUNNING;
		if (Config.spawnmobs) spawner = new Spawner(this, Config.spawnmobsinterval);
		if (Config.randomChest) chestDrop = new ChestDropTask(this);
		timer = new TimerTask(this, time);
		updateLobbyBlock();
		createBossbar(time);
		if (Config.borderEnabled && Config.borderOnStart) {
			setBorder(time);
		}
	}


	/** Add a spawn location to the game
	 * @param location The location to add
	 */
	public void addSpawn(Location location) {
		this.spawns.add(location);
	}

	private Location pickSpawn() {
		double spawn = getRandomIntegerBetweenRange(maxPlayers - 1);
		if (containsPlayer(spawns.get(((int) spawn)))) {
			Collections.shuffle(spawns);
			for (Location l : spawns) {
				if (!containsPlayer(l)) {
					return l;
				}
			}
		}
		return spawns.get((int) spawn);
	}

	private boolean containsPlayer(Location location) {
		if (location == null) return false;

		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			assert p != null;
			if (!isInRegion(p.getLocation())) continue;
			if (p.getLocation().distance(location) <= 1.5)
				return true;
		}
		return false;
	}

	/** Send a message to all players in the game
	 * @param message Message to send
	 */
	public void msgAll(String message) {
		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			if (p != null)
				Util.scm(p, message);
		}
	}

	private void updateLobbyBlock() {
		s1.setLine(1, status.getName());
		s2.setLine(1, ChatColor.BOLD + "" + players.size() + "/" + maxPlayers);
		s1.update(true);
		s2.update(true);
	}

	private void heal(Player player) {
		for (PotionEffect ef : player.getActivePotionEffects()) {
			player.removePotionEffect(ef.getType());
		}
		player.setHealth(20);
		player.setFoodLevel(20);
		try {
			Bukkit.getScheduler().scheduleSyncDelayedTask(HG.plugin, () -> player.setFireTicks(0), 1);
		} catch (IllegalPluginAccessException ignore) {}

	}

	/** Freeze a player
	 * @param player Player to freeze
	 */
	public void freeze(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10, false, false));
		player.setWalkSpeed(0.0001F);
		player.setFoodLevel(1);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setInvulnerable(true);
	}

	/** Unfreeze a player
	 * @param player Player to unfreeze
	 */
	public void unFreeze(Player player) {
		player.removePotionEffect(PotionEffectType.JUMP);
		player.setWalkSpeed(0.2F);
	}

	/** Set the lobby block for this game
	 * @param sign The sign to which the lobby will be set at
	 * @return True if lobby is set
	 */
	public boolean setLobbyBlock(Sign sign) {
		try {
			this.s = sign;
			Block c = s.getBlock();
			BlockFace face = Util.getSignFace(((Directional) s.getBlockData()).getFacing());
			this.s1 = (Sign) c.getRelative(face).getState();
			this.s2 = (Sign) s1.getBlock().getRelative(face).getState();

			s.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames");
			s.setLine(1, ChatColor.BOLD + name);
			s.setLine(2, ChatColor.BOLD + "Click To Join");
			s1.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Game Status");
			s1.setLine(1, status.getName());
			s2.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Alive");
			s2.setLine(1, ChatColor.BOLD + "" + 0 + "/" + maxPlayers);
			s.update(true);
			s1.update(true);
			s2.update(true);
		} catch (Exception e) {
			return false;
		}
		try {
			String[] h = Objects.requireNonNull(HG.plugin.getConfig().getString("settings.globalexit")).split(":");
			this.exit = new Location(Bukkit.getServer().getWorld(h[0]), Integer.parseInt(h[1]) + 0.5,
					Integer.parseInt(h[2]) + 0.1, Integer.parseInt(h[3]) + 0.5, Float.parseFloat(h[4]), Float.parseFloat(h[5]));
		} catch (Exception e) {
			this.exit = s.getWorld().getSpawnLocation();
		}
		return true;
	}

	/** Set exit location for this game
	 * @param location Location where players will exit
	 */
	public void setExit(Location location) {
		this.exit = location;
	}

	void cancelTasks() {
		if (spawner != null) spawner.stop();
		if (timer != null) timer.stop();
		if (starting != null) starting.stop();
		if (freeRoam != null) freeRoam.stop();
		if (chestDrop != null) chestDrop.shutdown();
	}

	/**
	 * Stop the game
	 */
	public void stop() {
		stop(false);
	}

	/** Stop the game
	 * @param death Whether the game stopped after the result of a death (false = no winnings payed out)
	 */
	public void stop(Boolean death) {
		bound.removeEntities();
		List<UUID> win = new ArrayList<>();
		cancelTasks();
		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				HG.plugin.getPlayers().get(p.getUniqueId()).restore(p);
				HG.plugin.getPlayers().remove(p.getUniqueId());
				win.add(p.getUniqueId());
				sb.restoreSB(p);
				unFreeze(p);
				exit(p);
			}
		}
		players.clear();

		for (UUID uuid : spectators) {
			Player spectator = Bukkit.getPlayer(uuid);
			if (spectator != null) {
				spectator.setCollidable(true);
				if (Config.spectateHide)
					revealPlayer(spectator);
				if (Config.spectateFly) {
					GameMode mode = HG.plugin.getSpectators().get(uuid).getGameMode();
					if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE)
						spectator.setAllowFlight(false);
				}
				HG.plugin.getSpectators().get(spectator.getUniqueId()).restore(spectator);
				HG.plugin.getSpectators().remove(spectator.getUniqueId());
				exit(spectator);
				sb.restoreSB(spectator);
			}
		}
		spectators.clear();

		if (this.getStatus() == Status.RUNNING) {
			bar.removeAll();
			bar = null;
		}

		if (!win.isEmpty() && death) {
			double db = (double) Config.cash / win.size();
			for (UUID u : win) {
				if (Config.giveReward) {
					Player p = Bukkit.getPlayer(u);
					assert p != null;
					if (!Config.rewardCommands.isEmpty()) {
						for (String cmd : Config.rewardCommands) {
							if (!cmd.equalsIgnoreCase("none"))
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("<player>", p.getName()));
						}
					}
					if (!Config.rewardMessages.isEmpty()) {
						for (String msg : Config.rewardMessages) {
							if (!msg.equalsIgnoreCase("none"))
								Util.scm(p, msg.replace("<player>", p.getName()));
						}
					}
					if (Config.cash != 0) {
						Vault.economy.depositPlayer(Bukkit.getServer().getOfflinePlayer(u), db);
						Util.scm(p, HG.plugin.getLang().winning_amount.replace("<amount>", String.valueOf(db)));
					}
				}
				plugin.getLeaderboard().addStat(u, Leaderboard.Stats.WINS);
				plugin.getLeaderboard().addStat(u, Leaderboard.Stats.GAMES);
			}
		}

		for (Location loc : chests) {
			if (loc.getBlock().getState() instanceof InventoryHolder) {
				((InventoryHolder) loc.getBlock().getState()).getInventory().clear();
				loc.getBlock().getState().update();
			}
		}
		chests.clear();
		String winner = Util.translateStop(Util.convertUUIDListToStringList(win));
		// prevent not death winners from gaining a prize
		if (death)
			Util.broadcast(HG.plugin.getLang().player_won.replace("<arena>", name).replace("<winner>", winner));
		if (!blocks.isEmpty()) {
			new Rollback(this);
		} else {
			status = Status.READY;
			updateLobbyBlock();
		}
		sb.resetAlive();
		if (Config.borderEnabled) {
			resetBorder();
		}
		runCommands(CommandType.STOP, null);
	}

	/** Make a player leave the game
	 * @param player Player to leave the game
	 * @param death Whether the player has died or not (Generally should be false)
	 */
	public void leave(Player player, Boolean death) {
		Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(this, player, death));
		players.remove(player.getUniqueId());
		unFreeze(player);
		if (death) {
			if (this.getStatus() == Status.RUNNING)
				bar.removePlayer(player);
			HG.plugin.getPlayers().get(player.getUniqueId()).restore(player);
			HG.plugin.getPlayers().remove(player.getUniqueId());
			exit(player);
			heal(player);
			sb.restoreSB(player);
			player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
			if (spectate && spectateOnDeath && !isGameOver()) {
				spectate(player);
				player.sendTitle(getName(), "You are now spectating!", 10, 100, 10); //TODO this a temp test
			}
		} else {
			HG.plugin.getPlayers().get(player.getUniqueId()).restore(player);
			HG.plugin.getPlayers().remove(player.getUniqueId());
			exit(player);
			heal(player);
			sb.restoreSB(player);
		}
		updateAfterDeath(player, death);
	}

	private void updateAfterDeath(Player player, boolean death) {
		if (status == Status.RUNNING || status == Status.BEGINNING || status == Status.COUNTDOWN) {
			if (isGameOver()) {
				if (!death) {
					for (UUID uuid : players) {
						if (kills.get(Bukkit.getPlayer(uuid)) >= 1) {
							death = true;
						}
					}
				}
				boolean finalDeath = death;
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					stop(finalDeath);
					updateLobbyBlock();
					sb.setAlive();
				}, 20);

			}
		} else if (status == Status.WAITING) {
			msgAll(HG.plugin.getLang().player_left_game.replace("<player>", player.getName()) +
					(minPlayers - players.size() <= 0 ? "!" : ":" + HG.plugin.getLang().players_to_start
							.replace("<amount>", String.valueOf((minPlayers - players.size())))));
		}
		updateLobbyBlock();
		sb.setAlive();
	}

	private boolean isGameOver() {
		if (players.size() <= 1) return true;
		for (Entry<UUID, PlayerData> f : HG.plugin.getPlayers().entrySet()) {

			Team t = f.getValue().getTeam();

			if (t != null && (t.getPlayers().size() >= players.size())) {
				List<UUID> ps = t.getPlayers();
				for (UUID u : players) {
					if (!ps.contains(u)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	private void exit(Player player) {
		player.setInvulnerable(false);
		if (this.getStatus() == Status.RUNNING)
			bar.removePlayer(player);
		if (this.exit == null) {
			player.teleport(s.getWorld().getSpawnLocation());
		} else {
			player.teleport(this.exit);
		}
	}

	public boolean isLobbyValid() {
		try {
			if (s != null && s1 != null && s2 != null) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	private static double getRandomIntegerBetweenRange(double max) {
		return (int) (Math.random() * ((max - (double) 0) + 1)) + (double) 0;
	}

	private void createBossbar(int time) {
		int min = (time / 60);
		int sec = (time % 60);
		String title = HG.plugin.getLang().bossbar.replace("<min>", String.valueOf(min)).replace("<sec>", String.valueOf(sec));
		bar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', title), BarColor.GREEN, BarStyle.SEGMENTED_20);
		for (UUID uuid : players) {
			Player player = Bukkit.getPlayer(uuid);
			assert player != null;
			bar.addPlayer(player);
		}
		for (UUID uuid : spectators) {
			Player player = Bukkit.getPlayer(uuid);
			assert player != null;
			bar.addPlayer(player);
		}
	}

	public void bossbarUpdate(int remaining) {
		double remain = ((double) remaining) / ((double) this.time);
		int min = (remaining / 60);
		int sec = (remaining % 60);
		String title = HG.plugin.getLang().bossbar.replace("<min>", String.valueOf(min)).replace("<sec>", String.valueOf(sec));
		bar.setTitle(ChatColor.translateAlternateColorCodes('&', title));
		bar.setProgress(remain);
		if (remain <= 0.5 && remain >= 0.2)
			bar.setColor(BarColor.YELLOW);
		if (remain < 0.2)
			bar.setColor(BarColor.RED);

	}

	private double getBorderSize(Location center) {
		double x1 = Math.abs(bound.getGreaterCorner().getX() - center.getX());
		double x2 = Math.abs(bound.getLesserCorner().getX() - center.getX());
		double z1 = Math.abs(bound.getGreaterCorner().getZ() - center.getZ());
		double z2 = Math.abs(bound.getLesserCorner().getZ() - center.getZ());

		double x = Math.max(x1, x2);
		double z = Math.max(z1, z2);
		double r = Math.max(x, z);

		return (r * 2) + 10;
	}

	/** Set the center of the border of this game
	 * @param borderCenter  Location of the center
	 */
	public void setBorderCenter(Location borderCenter) {
		this.borderCenter = borderCenter;
	}

	/** Set the final size for the border of this game
	 * @param borderSize The final size of the border
	 */
	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}

	public void setBorderTimer(int start, int end) {
		this.borderCountdownStart = start;
		this.borderCountdownEnd = end;
	}

	public List<Integer> getBorderTimer() {
		return Arrays.asList(borderCountdownStart, borderCountdownEnd);
	}

	public void setBorder(int time) {
		Location center;
		if (Config.centerSpawn) {
			center = this.spawns.get(0);
		} else if (borderCenter != null) {
			center = borderCenter;
		} else {
			center = bound.getCenter();
		}
		World world = center.getWorld();
		assert world != null;
		WorldBorder border = world.getWorldBorder();
		double size = Math.min(border.getSize(), getBorderSize(center));

		border.setCenter(center);
		border.setSize(((int) size));
		border.setWarningTime(5);
		border.setDamageBuffer(2);
		border.setSize(borderSize, time);
	}

	private void resetBorder() {
		World world = this.getRegion().getWorld();
		assert world != null;
		world.getWorldBorder().reset();
	}

	/** Put a player into spectator for this game
	 * @param spectator The player to spectate
	 */
	public void spectate(Player spectator) {
		if (plugin.getPlayers().containsKey(spectator.getUniqueId())) {
			PlayerData spectatorData = plugin.getPlayers().get(spectator.getUniqueId());
			plugin.getSpectators().put(spectator.getUniqueId(), spectatorData);
			plugin.getPlayers().remove(spectator.getUniqueId());
		} else {
			plugin.getSpectators().put(spectator.getUniqueId(), new PlayerData(spectator, this));
		}
		this.spectators.add(spectator.getUniqueId());
		spectator.setGameMode(GameMode.SURVIVAL);
		spectator.teleport(this.getSpawns().get(0));
		spectator.setCollidable(false);
		if (Config.spectateFly)
			spectator.setAllowFlight(true);

		if (Config.spectateHide) {
			for (UUID uuid : players) {
				Player player = Bukkit.getPlayer(uuid);
				if (player == null) continue;
				player.hidePlayer(plugin, spectator);
			}
			for (UUID uuid : spectators) {
				Player player = Bukkit.getPlayer(uuid);
				if (player == null) continue;
				player.hidePlayer(plugin, spectator);
			}
		}
		if (bar != null)
			bar.addPlayer(spectator);
	}

	/** Remove a player from spectator of this game
	 * @param spectator The player to remove
	 */
	public void leaveSpectate(Player spectator) {
		exit(spectator);
		spectator.setCollidable(true);
		if (Config.spectateFly) {
			GameMode mode = plugin.getSpectators().get(spectator.getUniqueId()).getGameMode();
			if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE)
				spectator.setAllowFlight(false);
		}
		if (Config.spectateHide)
			revealPlayer(spectator);
		plugin.getSpectators().get(spectator.getUniqueId()).restore(spectator);
		plugin.getSpectators().remove(spectator.getUniqueId());
		spectators.remove(spectator.getUniqueId());
	}

	private void revealPlayer(Player hidden) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.showPlayer(plugin, hidden);
		}
	}

	/** Run commands for this game that are defined in the arenas.yml
	 * @param commandType Type of command to run
	 * @param player The player involved (can be null)
	 */
	@SuppressWarnings("ConstantConditions")
	public void runCommands(CommandType commandType, @Nullable Player player) {
		if (commands == null) return;
		for (String command : commands) {
			String type = command.split(":")[0];
			if (!type.equals(commandType.getType())) continue;
			if (command.equalsIgnoreCase("none")) continue;
			command = command.split(":")[1]
					.replace("<world>", this.bound.getWorld().getName())
					.replace("<arena>", this.getName());
			if (player != null) {
				command = command.replace("<player>", player.getName());
			}
			if (commandType == CommandType.START && command.contains("<player>")) {
				for (UUID uuid : players) {
					String newCommand = command.replace("<player>", Bukkit.getPlayer(uuid).getName());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), newCommand);
				}
			} else
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		}
	}

	/**
	 * Command types
	 */
	public enum CommandType {
		/**
		 * A command to run when a player dies in game
		 */
		DEATH("death"),
		/**
		 * A command to run at the start of a game
		 */
		START("start"),
		/**
		 * A command to run at the end of a game
		 */
		STOP("stop"),
		/**
		 * A command to run when a player joins a game
		 */
		JOIN("join");

		String type;

		CommandType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

}
