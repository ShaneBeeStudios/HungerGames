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
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Bound;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * General manager for games
 */
public class Manager {

	private HG plugin;
	private Random rg = new Random();
	
	public Manager(HG plugin) {
		this.plugin = plugin;
	}

    /** Run arena debugger
     * @param sender Sender who issued this debuger
     * @param gameName Name of the game to debug
     */
	public void runDebugger(CommandSender sender, String gameName) {
		Configuration arenadat = HG.getPlugin().getArenaConfig().getCustomConfig();
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
			timer = arenadat.getInt("arenas." + gameName + ".info." + "timer");
			minplayers = arenadat.getInt("arenas." + gameName + ".info." + "min-players");
			maxplayers = arenadat.getInt("arenas." + gameName + ".info." + "max-players");

			if (arenadat.isSet("arenas." + gameName + ".border.center")) {
				borderCenter = HG.getPlugin().getArenaConfig().getSLoc(arenadat.getString("arenas." + gameName + ".border.center"));
			}
			if (arenadat.isSet("arenas." + gameName + ".border.size")) {
				borderSize = arenadat.getInt("arenas." + gameName + ".border.size");
			} else {
				borderSize = Config.borderFinalSize;
			}
			if (arenadat.isSet("arenas." + gameName + ".border.countdown-start") &&
					arenadat.isSet("arenas." + gameName + ".border.countdown-end")) {
				borderCountdownStart = arenadat.getInt("arenas." + gameName + ".border.countdown-start");
				borderCountdownEnd = arenadat.getInt("arenas." + gameName + ".border.countdown-end");
			} else {
				borderCountdownStart = Config.borderCountdownStart;
				borderCountdownEnd = Config.borderCountdownEnd;
			}
			if (arenadat.isSet("arenas." + gameName + ".chest-refill")) {
				chestRefill = arenadat.getInt("arenas." + gameName + ".chest-refill");
			}
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load information for arena " + gameName + "!");
			isReady = false;
		}

		try {
			lobbysign = (Sign) HG.getPlugin().getArenaConfig().getSLoc(arenadat.getString("arenas." + gameName + "." + "lobbysign")).getBlock().getState();
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load lobbysign for arena " + gameName + "!");
			isReady = false;
		}

		try {
			for (String l : arenadat.getStringList("arenas." + gameName + "." + "spawns")) {
				spawns.add(HG.getPlugin().getArenaConfig().getLocFromString(l));
			}
			int count = arenadat.getStringList("arenas." + gameName + "." + "spawns").size();
			if (count < maxplayers) {
				Util.scm(sender, "&cYou need to add " + (maxplayers - count) + " more spawns!"); 
				isReady = false;
			}
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load random spawns for arena " + gameName + "!");
			isReady = false;
		}

		try {
			@SuppressWarnings("unused")
            Bound b = new Bound(arenadat.getString("arenas." + gameName + ".bound." + "world"), HG.getPlugin().getArenaConfig().BC(gameName, "x"), HG.getPlugin().getArenaConfig().BC(gameName, "y"), HG.getPlugin().getArenaConfig().BC(gameName, "z"), HG.getPlugin().getArenaConfig().BC(gameName, "x2"), HG.getPlugin().getArenaConfig().BC(gameName, "y2"), HG.getPlugin().getArenaConfig().BC(gameName, "z2"));
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load region bounds for arena " + gameName + "!");
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

    /** Check the status of a game while being set up
     * @param game Game to check
     * @param player Player issuing the check
     */
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

    /** Fill chests in a game
     * @param block Chest to fill
     * @param game Game this chest is in
     * @param bonus Whether or not this is a bonus chest
     */
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
		c = Math.max(c, min);
		while (c != 0) {
			ItemStack it = randomItem(game, bonus);
			int slot = slots.get(0);
			slots.remove(0);
			i.setItem(slot, it);
			c--;
		}
	}

    /** Get a random item from a game's item list
     * @param game Game to get the item from
     * @param bonus Whether or not its a bonus item
     * @return Random ItemStack
     */
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
		for (Game g : plugin.getGames()) {
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
		for (Game g : plugin.getGames()) {
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
		for (Game g : plugin.getGames()) {
			if (g.getName().equalsIgnoreCase(name)) {
				return g;
			}
		}
		return null;
	}

}
