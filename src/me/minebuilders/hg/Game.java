package me.minebuilders.hg;

import me.minebuilders.hg.mobhandler.Spawner;
import me.minebuilders.hg.tasks.ChestDropTask;
import me.minebuilders.hg.tasks.FreeRoamTask;
import me.minebuilders.hg.tasks.StartingTask;
import me.minebuilders.hg.tasks.TimerTask;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class Game {

	private String name;
	private List<Location> spawns;
	private Bound b;
	private List<UUID> players = new ArrayList<>();
	private List<Location> chests = new ArrayList<>();

	private List<BlockState> blocks = new ArrayList<>();
	private Location exit;
	private Status status;
	private int minplayers;
	private int maxplayers;
	private int time;
	private Sign s;
	private Sign s1;
	private Sign s2;
	private int roamtime;
	private SBDisplay sb;

	// Task ID's here!
	private Spawner spawner;
	private FreeRoamTask freeroam;
	public StartingTask starting;
	private TimerTask timer;
	private ChestDropTask chestdrop;


	public Game(String s, Bound bo, List<Location> spawns, Sign lobbysign, int timer, int minplayers, int maxplayers, int roam, boolean isready) {
		this.name = s;
		this.b = bo;
		this.spawns = spawns;
		this.s = lobbysign;
		this.time = timer;
		this.minplayers = minplayers;
		this.maxplayers = maxplayers;
		this.roamtime = roam;
		if (isready) status = Status.READY;
		else status = Status.BROKEN;


		setLobbyBlock(lobbysign);

		sb = new SBDisplay(this);
	}

	public Game(String s, Bound c, int timer, int minplayers, int maxplayers, int roam) {
		this.name = s;
		this.time = timer;
		this.minplayers = minplayers;
		this.maxplayers = maxplayers;
		this.roamtime = roam;
		this.spawns = new ArrayList<>();
		this.b = c;
		status = Status.NOTREADY;
		sb = new SBDisplay(this);
	}

	public Bound getRegion() {
		return b;
	}

	public void forceRollback() {
		Collections.reverse(blocks);
		for (BlockState st : blocks) {
			st.update(true);
		}
	}

	public void setStatus(Status st) {
		this.status = st;
		updateLobbyBlock();
	}

	private void addState(BlockState s) {
		if (s.getType() != Material.AIR) {
			blocks.add(s);
		}
	}

	public void addChest(Location l) {
		chests.add(l);
	}

	public boolean isLoggedChest(Location l) {
		return (chests.contains(l));
	}

	public void removeChest(Location l) {
		chests.remove(l);
	}

	public void recordBlockBreak(Block bl) {
		Block top = bl.getRelative(BlockFace.UP);

		if (!top.getType().isSolid() || !top.getType().isBlock()) {
			addState(bl.getRelative(BlockFace.UP).getState());
		}

		for (BlockFace bf : Util.faces) {
			Block rel = bl.getRelative(bf);

			if (Util.isAttached(bl, rel)) {
				addState(rel.getState());
			}
		}
		addState(bl.getState());
	}

	public void recordBlockPlace(BlockState bs) {
		blocks.add(bs);
	}

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

	public List<UUID> getPlayers() {
		return players;
	}

	public String getName() {
		return this.name;
	}

	public boolean isInRegion(Location l) {
		return b.isInRegion(l);
	}

	public List<Location> getSpawns() {
		return spawns;
	}

	public int getRoamTime() {
		return this.roamtime;
	}

	public void join(Player p) {
		if (status != Status.WAITING && status != Status.STOPPED && status != Status.COUNTDOWN && status != Status.READY) {
			Util.scm(p, HG.lang.arena_not_ready);
		} else if (maxplayers <= players.size()) {
			p.sendMessage(ChatColor.RED + name + " is currently full!");
			Util.scm(p, "&c" + name + " " + HG.lang.game_full);
		} else {
			if (p.isInsideVehicle()) {
				p.leaveVehicle();
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(HG.plugin, () -> {
                players.add(p.getUniqueId());
                HG.plugin.players.put(p.getUniqueId(), new PlayerData(p, this));

			Location loc = pickSpawn();
			if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
				loc.setY(Bukkit.getWorld(loc.getWorld().getName()).getHighestBlockAt(loc).getY());
			}
			p.teleport(loc);
			heal(p);
			freeze(p);

            if (players.size() == 1)
                status = Status.WAITING;
			if (players.size() >= minplayers && (status == Status.WAITING || status == Status.READY)) {
				startPreGame();
			} else if (status == Status.WAITING) {
				msgDef(HG.lang.player_joined_game.replace("<player>",
						p.getName()) + (minplayers - players.size() <= 0 ? "!" : ":" +
						HG.lang.players_to_start.replace("<amount>", String.valueOf((minplayers - players.size())))));
			}
			kitHelp(p);

			updateLobbyBlock();
			sb.setSB(p);
			sb.setAlive();
            }, 5);
		}
	}

	private void kitHelp(Player p) {
		// Clear the chat a little bit, making this message easier to see
		for(int i = 0; i < 20; ++i)
			Util.scm(p, " ");
		String kit = HG.plugin.kit.getKitList();
		Util.scm(p, " ");
		Util.scm(p, HG.lang.kit_join_header);
		Util.scm(p, " ");
		Util.scm(p, HG.lang.kit_join_msg);
		Util.scm(p, " ");
		Util.scm(p, HG.lang.kit_join_avail + kit);
		Util.scm(p, " ");
		Util.scm(p, HG.lang.kit_join_footer);
		Util.scm(p, " ");
	}

	public void respawnAll() {
		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			if (p != null)
				p.teleport(pickSpawn());
		}
	}

	public void startPreGame() {
		//setStatus(Status.COUNTDOWN);
        status = Status.COUNTDOWN;
		starting = new StartingTask(this);
		updateLobbyBlock();
	}

	public void startFreeRoam() {
		status = Status.BEGINNING;
		b.removeEntities();
		freeroam = new FreeRoamTask(this);
	}

	public void startGame() {
		status = Status.RUNNING;
		if (Config.spawnmobs) spawner = new Spawner(this, Config.spawnmobsinterval);
		if (Config.randomChest) chestdrop = new ChestDropTask(this);
		timer = new TimerTask(this, time);
		updateLobbyBlock();
	}


	public void addSpawn(Location l) {
		this.spawns.add(l);
	}

	private Location pickSpawn() {

		double spawn = getRandomIntegerBetweenRange(maxplayers - 1);
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

	private boolean containsPlayer(Location l) {
		if (l == null) return false;

		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			if (p != null && p.getLocation().getBlock().equals(l.getBlock()))
				return true;
		}
		return false;
	}

	public void msgAll(String s) {
		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			if (p != null)
				Util.msg(p, s);
		}
	}

	public void msgDef(String s) {
		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			if (p != null)
				Util.scm(p, s);
		}
	}

	private void updateLobbyBlock() {
		s1.setLine(1, status.getName());
		s2.setLine(1, ChatColor.BOLD + "" + players.size() + "/" + maxplayers);
		s1.update(true);
		s2.update(true);
	}

	private void heal(Player p) {
		for (PotionEffect ef : p.getActivePotionEffects()) {
			p.removePotionEffect(ef.getType());
		}
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setFireTicks(0);
	}

	public void freeze(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10));
		p.setWalkSpeed(0.0001F);
		p.setFoodLevel(1);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setGameMode(GameMode.SURVIVAL);
	}

	public void unFreeze(Player p) {
		p.removePotionEffect(PotionEffectType.JUMP);
		p.setWalkSpeed(0.2F);
	}

	public boolean setLobbyBlock(Sign sign) {
		try {
			this.s = sign;
			Block c = s.getBlock();
			@SuppressWarnings("deprecation")
			BlockFace face = Util.getSignFace(c.getData());
			this.s1 = (Sign) c.getRelative(face).getState();
			this.s2 = (Sign) s1.getBlock().getRelative(face).getState();

			s.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames");
			s.setLine(1, ChatColor.BOLD + name);
			s.setLine(2, ChatColor.BOLD + "Click To Join");
			s1.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Game Status");
			s1.setLine(1, status.getName());
			s2.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Alive");
			s2.setLine(1, ChatColor.BOLD + "" + 0 + "/" + maxplayers);
			s.update(true);
			s1.update(true);
			s2.update(true);
		} catch (Exception e) {
			return false;
		}
		try {
			String[] h = HG.plugin.getConfig().getString("settings.globalexit").split(":");
			this.exit = new Location(Bukkit.getServer().getWorld(h[0]), Integer.parseInt(h[1]) + 0.5,
					Integer.parseInt(h[2]) + 0.1, Integer.parseInt(h[3]) + 0.5, Float.parseFloat(h[4]), Float.parseFloat(h[5]));
		} catch (Exception e) {
			this.exit = s.getWorld().getSpawnLocation();
		}
		return true;
	}

	public void setExit(Location l) {
		this.exit = l;
	}

	void cancelTasks() {
		if (spawner != null) spawner.stop();
		if (timer != null) timer.stop();
		if (starting != null) starting.stop();
		if (freeroam != null) freeroam.stop();
		if (chestdrop != null) chestdrop.shutdown();
	}

	public void stop(Boolean death) {
		List<UUID> win = new ArrayList<>();
		cancelTasks();
		for (UUID u : players) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				heal(p);
				exit(p);
				HG.plugin.players.get(p.getUniqueId()).restore(p);
				HG.plugin.players.remove(p.getUniqueId());
				win.add(p.getUniqueId());
				sb.restoreSB(p);
			}
		}
		players.clear();

		if (!win.isEmpty() && Config.giveReward && death) {
			double db = (double) Config.cash / win.size();
			for (UUID u : win) {
				Player p = Bukkit.getPlayer(u);
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
                    if (p != null) {
                        Util.msg(p, HG.lang.winning_amount.replace("<amount>", String.valueOf(db)));
                    }
                }
			}
		}

		for (Location loc : chests) {
			((Chest) loc.getBlock().getState()).getInventory().clear();
			loc.getBlock().getState().update();
		}
		chests.clear();
		String winner = Util.translateStop(Util.convertUUIDListToStringList(win));
		// prevent not death winners from gaining a prize
		if (death)
			Util.broadcast(HG.lang.player_won.replace("<arena>", name).replace("<winner>", winner));
		if (!blocks.isEmpty()) {
			new Rollback(this);
		} else {
			status = Status.READY;
			updateLobbyBlock();
		}
		b.removeEntities();
		sb.resetAlive();
	}

	public void leave(Player p, Boolean death) {
		players.remove(p.getUniqueId());
		unFreeze(p);
		heal(p);
		exit(p);
		sb.restoreSB(p);
		HG.plugin.players.get(p.getUniqueId()).restore(p);
		HG.plugin.players.remove(p.getUniqueId());
		if (status == Status.RUNNING || status == Status.BEGINNING || status == Status.COUNTDOWN) {
			if (isGameOver()) {
				stop(death);
			}
		} else if (status == Status.WAITING) {
			msgDef(HG.lang.player_left_game.replace("<player>", p.getName()) +
					(minplayers - players.size() <= 0 ? "!" : ":" + HG.lang.players_to_start
							.replace("<amount>", String.valueOf((minplayers - players.size())))));
		}
		updateLobbyBlock();
		sb.setAlive();
	}

	private boolean isGameOver() {
		if (players.size() <= 1) return true;
		for (Entry<UUID, PlayerData> f : HG.plugin.players.entrySet()) {

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

	public void exit(Player p) {
		Util.clearInv(p);
		if (this.exit == null) {
			p.teleport(s.getWorld().getSpawnLocation());
		} else {
			p.teleport(this.exit);
		}
	}

	public int getMaxPlayers() {
		return maxplayers;
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

}
