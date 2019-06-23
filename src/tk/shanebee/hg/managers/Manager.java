package tk.shanebee.hg.managers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import tk.shanebee.hg.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Manager {

	private HG plugin;
	private Random rg = new Random();
	
	public Manager(HG plugin) {
		this.plugin = plugin;
	}
	
	public void runDebugger(CommandSender sender, String s) {
		Configuration arenadat = HG.arenaconfig.getCustomConfig();
		boolean isReady = true;
		List<Location> spawns = new ArrayList<>();
		Sign lobbysign = null;
		int timer = 0;
		int minplayers = 0;
		int maxplayers = 0;
		boolean border = Config.borderEnabled;
		Location borderCenter = null;
		int borderSize = 0;
		int borderCountdownStart = 0;
		int borderCountdownEnd = 0;
		int chestRefill = 0;

		try {
			timer = arenadat.getInt("arenas." + s + ".info." + "timer");
			minplayers = arenadat.getInt("arenas." + s + ".info." + "min-players");
			maxplayers = arenadat.getInt("arenas." + s + ".info." + "max-players");

			if (arenadat.isSet("arenas." + s + ".border.center")) {
				borderCenter = HG.arenaconfig.getSLoc(arenadat.getString("arenas." + s + ".border.center"));
			}
			if (arenadat.isSet("arenas." + s + ".border.size")) {
				borderSize = arenadat.getInt("arenas." + s + ".border.size");
			} else {
				borderSize = Config.borderFinalSize;
			}
			if (arenadat.isSet("arenas." + s + ".border.countdown-start") &&
					arenadat.isSet("arenas." + s + ".border.countdown-end")) {
				borderCountdownStart = arenadat.getInt("arenas." + s + ".border.countdown-start");
				borderCountdownEnd = arenadat.getInt("arenas." + s + ".border.countdown-end");
			} else {
				borderCountdownStart = Config.borderCountdownStart;
				borderCountdownEnd = Config.borderCountdownEnd;
			}
			if (arenadat.isSet("arenas." + s + ".chest-refill")) {
				chestRefill = arenadat.getInt("arenas." + s + ".chest-refill");
			}
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load information for arena " + s + "!");
			isReady = false;
		}

		try {
			lobbysign = (Sign) HG.arenaconfig.getSLoc(arenadat.getString("arenas." + s + "." + "lobbysign")).getBlock().getState();
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load lobbysign for arena " + s + "!"); 
			isReady = false;
		}

		try {
			for (String l : arenadat.getStringList("arenas." + s + "." + "spawns")) {
				spawns.add(HG.arenaconfig.getLocFromString(l));
			}
			int count = arenadat.getStringList("arenas." + s + "." + "spawns").size();
			if (count < maxplayers) {
				Util.scm(sender, "&cYou need to add " + (maxplayers - count) + " more spawns!"); 
				isReady = false;
			}
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load random spawns for arena " + s + "!"); 
			isReady = false;
		}

		try {
			@SuppressWarnings("unused")
			Bound b = new Bound(arenadat.getString("arenas." + s + ".bound." + "world"), HG.arenaconfig.BC(s, "x"), HG.arenaconfig.BC(s, "y"), HG.arenaconfig.BC(s, "z"), HG.arenaconfig.BC(s, "x2"), HG.arenaconfig.BC(s, "y2"), HG.arenaconfig.BC(s, "z2"));
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load region bounds for arena " + s + "!"); 
			isReady = false;
		}
		if (isReady) {
			Util.scm(sender,"&7&l---= &3&lYour HungerGames arena is ready to run! &7&l=---");
			Util.scm(sender, "&7Spawns:&b " + spawns.size());
			Util.scm(sender, "&7Lobby:&b x:" + lobbysign.getX() +", y:"+ lobbysign.getY() +", z:"+ lobbysign.getZ());
			Util.scm(sender, "&7Timer:&b " + timer);
			Util.scm(sender, "&7MinPlayers:&b " + minplayers);
			Util.scm(sender, "&7MaxPlayers:&b " + maxplayers);
			if (chestRefill > 0)
				Util.scm(sender, "&7Chest Refill: &b" + chestRefill + " seconds");
			if (border) {
				Util.scm(sender, "&7Border: &aEnabled");
				if (borderCenter != null)
					Util.scm(sender, "&7Border Center: &bx:" + borderCenter.getX() + ", y:" + borderCenter.getY() + ", z:" + borderCenter.getZ());
				if (borderSize > 0)
					Util.scm(sender, "&7Border Size: &b" + borderSize);
				if (borderCountdownStart > 0) {
					Util.scm(sender, "&7Border Timer Start: &b" + borderCountdownStart + " seconds");
					Util.scm(sender, "&7Border Timer End: &b" + borderCountdownEnd + " seconds");
				}
			} else {
				Util.scm(sender, "&7Border: &cDisabled");
			}
		}
	}

	public void checkGame(Game game, Player player) {
		if (game.getSpawns().size() <  game.getMaxPlayers()) {
			Util.scm(player, "&cYou still need &7" + (game.getMaxPlayers() - game.getSpawns().size()) + " &c more spawns!");
		} else if (game.getStatus() == Status.BROKEN) {
			Util.scm(player, "&cYour arena is marked as broken! use &7/hg debug &c to check for errors!");
			Util.scm(player, "&cIf no errors are found, please use &7/hg toggle " + game.getName() + "&c!");
		} else if (!game.isLobbyValid()) {
			Util.scm(player, "&cYour LobbyWall is invalid! Please reset them!");
			Util.scm(player, "&cSet lobbywall: &7/hg setlobbywall " + game.getName());
		} else {
			Util.scm(player, "&aYour HungerGames arena is ready to run!");
			game.setStatus(Status.WAITING);
		}
	}
	
	public void fillChests(Block block, Game game, boolean bonus) {
		Inventory i = ((InventoryHolder)block.getState()).getInventory();
		List<Integer> slots = new ArrayList<>();
		for (int slot = 0; slot <= 26; slot++) {
			slots.add(slot);
		}
		Collections.shuffle(slots);
		i.clear();
		int max = bonus ? Config.maxbonuscontent : Config.maxchestcontent;
		int min = bonus ? Config.minbonuscontent : Config.minchestcontent;

		int c = rg.nextInt(max) + 1;
		c = c >= min ? c : min;
		while (c != 0) {
			ItemStack it = randomItem(game, bonus);
			int slot = slots.get(0);
			slots.remove(0);
			i.setItem(slot, it);
			c--;
		}
	}

	public ItemStack randomItem(Game game, boolean bonus) {
		if (bonus) {
			int i = rg.nextInt(game.getBonusItems().size()) + 1;
			return game.getBonusItems().get(i);
		} else {
			int i = rg.nextInt(game.getItems().size()) + 1;
			return game.getItems().get(i);
		}
	}

	/** Check if a location is in a game's bounds
	 * @param location The location to check for a game
	 * @return True if the location is within a game's bounds
	 */
	public boolean isInRegion(Location location) {
		for (Game g : plugin.games) {
			if (g.isInRegion(location))
				return true;
		}
		return false;
	}

	/** Get a game at a location
	 * @param location The location to check for a game
	 * @return The game
	 */
	public Game getGame(Location location) {
		for (Game g : plugin.games) {
			if (g.isInRegion(location))
				return g;
		}
		return null;
	}

	/** Get a game by name
	 * @param name The name of the game to find
	 * @return The game
	 */
	public Game getGame(String name) {
		for (Game g : plugin.games) {
			if (g.getName().equalsIgnoreCase(name)) {
				return g;
			}
		}
		return null;
	}

}
