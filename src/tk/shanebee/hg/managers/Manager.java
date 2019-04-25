package tk.shanebee.hg.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import tk.shanebee.hg.Bound;
import tk.shanebee.hg.Config;
import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Manager {

	private HG plugin;
	private Random rg = new Random();
	
	public Manager(HG p) {
		plugin = p;
	}
	
	public void runDebugger(CommandSender sender, String s) {
		Configuration arenadat = HG.arenaconfig.getCustomConfig();
		boolean isReady = true;
		List<Location> spawns = new ArrayList<Location>();
		Sign lobbysign = null;
		int timer = 0;
		int minplayers = 0;
		int maxplayers = 0;

		try {
			timer = arenadat.getInt("arenas." + s + ".info." + "timer");
			minplayers = arenadat.getInt("arenas." + s + ".info." + "min-players");
			maxplayers = arenadat.getInt("arenas." + s + ".info." + "max-players");
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load infomation for arena " + s + "!"); 
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
			Util.scm(sender, "&7Lobby:&b z:" + lobbysign.getX() +", y:"+ lobbysign.getY() +", z:"+ lobbysign.getZ());
			Util.scm(sender, "&7Timer:&b " + timer);
			Util.scm(sender, "&7MinPlayers:&b " + minplayers);
			Util.scm(sender, "&7MaxPlayers:&b " + maxplayers);
		}
	}

	public void checkGame(Game g, Player p) {
		if (g.getSpawns().size() <  g.getMaxPlayers()) {
			Util.scm(p, "&cYou still need &7" + (g.getMaxPlayers() - g.getSpawns().size()) + " &c more spawns!");
		} else if (g.getStatus() == Status.BROKEN) {
			Util.scm(p, "&cYour arena is marked as broken! use &7/hg debug &c to check for errors!");
			Util.scm(p, "&cIf no errors are found, please use &7/hg toggle " + g.getName() + "&c!");
		} else if (!g.isLobbyValid()) {
			Util.scm(p, "&cYour LobbyWall is invalid! Please reset them!");
			Util.scm(p, "&cSet lobbywall: &7/hg setlobbywall " + g.getName());
		} else {
			Util.scm(p, "&aYour HungerGames arena is ready to run!");
			g.setStatus(Status.WAITING);
		}
	}

	
	//@Note: dwoikdopw
	//We need to change this because we want to just create false chest./

	/**public void restoreChests(Game arena) {
		ArrayList<Location> chests = arena.getChests();
		for (Location l : chests) {
			Block b = l.getBlock();
			if (b.getType().equals(Material.CHEST)) {
				Inventory i = ((InventoryHolder)b.getState()).getInventory();
				i.clear();
				int c = rg.nextInt(Config.maxchestcontent) + 1;
				while (c != 0) {
					ItemStack it = randomitem();
					i.setItem(rg.nextInt(27), it);
					c--;
				}
			}
		}
	}*/
	
	public void fillChests(Block b) {
		Inventory i = ((InventoryHolder)b.getState()).getInventory();
		i.clear();
		int c = rg.nextInt(Config.maxchestcontent) + 1;
		while (c != 0) {
			ItemStack it = randomitem();
			i.setItem(rg.nextInt(27), it);
			c--;
		}
	}

	public ItemStack randomitem() {
		return plugin.items.get(rg.nextInt(HG.ri.size));
	}
	
	public boolean isInRegion(Location l) {
		for (Game g : HG.plugin.games) {
			if (g.isInRegion(l))
				return true;
		}
		return false;
	}

	public Game getGame(Location l) {
		for (Game g : HG.plugin.games) {
			if (g.isInRegion(l))
				return g;
		}
		return null;
	}

	public Game getGame(String s) {
		for (Game g : HG.plugin.games) {
			if (g.getName().equalsIgnoreCase(s)) {
				return g;
			}
		}
		return null;
	}
}
