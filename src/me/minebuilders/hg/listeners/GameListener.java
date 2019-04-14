package me.minebuilders.hg.listeners;

import me.minebuilders.hg.*;
import me.minebuilders.hg.events.ChestOpenEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GameListener implements Listener {

	private HG plugin;
	private String tsn = ChatColor.GOLD + "TrackingStick " + ChatColor.GREEN + "Uses: ";
	private ItemStack trackingStick;
	private HashMap<Player, Entity> killerMap = new HashMap<>();

	public GameListener(HG plugin) {
		this.plugin = plugin;
		ItemStack it = new ItemStack(Material.STICK, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(tsn + Config.trackingstickuses);
		it.setItemMeta(im);
		trackingStick = it;
	}

	private void dropInv(Player p) {
		PlayerInventory inv = p.getInventory();
		Location l = p.getLocation();
		for (ItemStack i : inv.getContents()) {
			if (i != null && i.getType() != Material.AIR)
				l.getWorld().dropItemNaturally(l, i);
		}
		for (ItemStack i : inv.getArmorContents()) {
			if (i != null && i.getType() != Material.AIR)
				l.getWorld().dropItemNaturally(l, i);
		}
	}

	private void checkStick(Game g) {
		if (Config.playersfortrackingstick == g.getPlayers().size()) {
			for (UUID u : g.getPlayers()) {
				Player p = Bukkit.getPlayer(u);
				if (p != null) {
					Util.scm(p, HG.lang.track_bar);
					Util.scm(p, HG.lang.track_new1);
					Util.scm(p, HG.lang.track_new2);
					Util.scm(p, HG.lang.track_bar);
					p.getInventory().addItem(trackingStick);
				}
			}
		}
	}



	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		final Player p = event.getEntity();

		PlayerData pd = plugin.players.get(p.getUniqueId());

		if (pd != null) {
			final Game g = pd.getGame();
			dropInv(p);

			// TODO Leaving this out for now and replacing with setCancelled to see if this performs any better
			//p.setHealth((p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
			//p.setHealth(20);
			p.setHealth(20);
			p.spigot().respawn();

			Player killer = p.getKiller();

			if (killer != null) {
				g.msgDef(HG.lang.death_fallen + " &d" + HG.killmanager.getKillString(p.getName(), killer));
			} else if (Objects.requireNonNull(p.getLastDamageCause()).getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
				g.msgDef(HG.lang.death_fallen + " &d" + HG.killmanager.getKillString(p.getName(), killerMap.get(p)));
			} else if (p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
				g.msgDef(HG.lang.death_fallen + " &d" + HG.killmanager.getKillString(p.getName(), killerMap.get(p)));
			} else {
				g.msgDef(HG.lang.death_fallen + " &d" + HG.killmanager.getDeathString(p.getLastDamageCause().getCause(), p.getName()));
			}
			event.setDeathMessage(null);
			event.getDrops().clear();

			for (UUID uuid : g.getPlayers()) {
				Player player = Bukkit.getPlayer(uuid);
				assert player != null;
				player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				g.exit(p);
				g.leave(p, true);
				p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
			}, 5);

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> checkStick(g), 10L);
		}
	}

	@EventHandler
	public void onSprint(FoodLevelChangeEvent event) {
		Player p = (Player) event.getEntity();
		if (plugin.players.containsKey(p.getUniqueId())) {
			Status st = plugin.players.get(p.getUniqueId()).getGame().getStatus();
			if (st == Status.WAITING || st == Status.COUNTDOWN) {
				event.setFoodLevel(1);
				event.setCancelled(true);
			}
		}
	}


	private void useTrackStick(Player p) {
		ItemStack i = p.getInventory().getItemInMainHand();
		ItemMeta im = i.getItemMeta();
		if (im.getDisplayName() != null && im.getDisplayName().startsWith(tsn)) {
			int uses = Integer.parseInt(im.getDisplayName().replace(tsn, ""));
			if (uses == 0) {
				Util.scm(p, HG.lang.track_empty);
			} else {
				for (Entity e : p.getNearbyEntities(120, 50, 120)) {
					if (e instanceof Player) {
						im.setDisplayName(tsn + (uses - 1));
						Location l = e.getLocation();
						int range = (int) p.getLocation().distance(l);
						Util.msg(p, HG.lang.track_nearest
								.replace("<player>", e.getName())
								.replace("<range>", String.valueOf(range))
								.replace("<location>", getDirection(p.getLocation().getBlock(), l.getBlock())));
						i.setItemMeta(im);
						p.updateInventory();
						return;
					}
				}
				Util.msg(p, HG.lang.track_no_near);

			}
		}
	}

	private String getDirection(Block block, Block block1) {
		Vector bv = block.getLocation().toVector();
		Vector bv2 = block1.getLocation().toVector();
		float y = (float) angle(bv.getX(), bv.getZ(), bv2.getX(), bv2.getZ());
		float cal = (y * 10);
		int c = (int) cal;
		if (c <= 1 && c >= -1) {
			return "South";
		} else if (c > -14 && c < -1) {
			return "SouthWest";
		} else if (c >= -17 && c <= -14) {
			return "West";
		} else if (c > -29 && c < -17) {
			return "NorthWest";
		} else if (c > 17 && c < 29) {
			return "NorthEast";
		} else if (c <= 17 && c >= 14) {
			return "East";
		} else if (c > 1 && c < 14) {
			return "SouthEast";
		} else if (c <= 29 && c >= -29) {
			return "North";
		} else {
			return "UnKnown";
		}
	}


	private double angle(double d, double e, double f, double g) {
		//Vector differences
		int x = (int) (f - d);
		int z = (int) (g - e);

		return Math.atan2(x, z);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAttack(EntityDamageByEntityEvent event) {
		Entity defender = event.getEntity();
		Entity damager = event.getDamager();

		if (defender instanceof Player) {
			if (plugin.players.get(defender.getUniqueId()) != null) {
				if (!killerMap.containsKey(defender))
					killerMap.put(((Player) defender), damager);
				else
					killerMap.replace(((Player) defender), damager);
			}
		}

		if (defender instanceof Player && damager != null) {
			Player p = (Player) defender;
			PlayerData pd = plugin.players.get(p.getUniqueId());

			if (pd != null) {
				Game g = pd.getGame();

				if (g.getStatus() != Status.RUNNING) {
					event.setCancelled(true);
				} else if (pd.isOnTeam(p.getUniqueId()) && damager instanceof Player && pd.getTeam().isOnTeam(damager.getUniqueId())) {
					Util.scm(damager, "&c" + p.getName() + " is on your team!");
					event.setCancelled(true);
				} else if (event.isCancelled()) event.setCancelled(false);
			}
		}
	}

	@EventHandler
	public void onChestOpen(ChestOpenEvent event) {
		Block b = event.getChest();
		Game g = event.getGame();
		if (!g.isLoggedChest(b.getLocation())) {
			HG.manager.fillChests(b);
			g.addChest(b.getLocation());
		}
	}

	@EventHandler
	public void onChestUse(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && plugin.players.containsKey(p.getUniqueId())) {
			Block b = event.getClickedBlock();
			if (b.getType() == Material.CHEST) {
				PlayerData pd = plugin.players.get(p.getUniqueId());
				Bukkit.getServer().getPluginManager().callEvent(new ChestOpenEvent(pd.getGame(), b));
			}
		}
	}

	@EventHandler
	public void onItemUseAttempt(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() != Action.PHYSICAL && plugin.players.containsKey(p.getUniqueId())) {
			Status st = plugin.players.get(p.getUniqueId()).getGame().getStatus();
			if (st == Status.WAITING || st == Status.COUNTDOWN) {
				event.setCancelled(true);
				Util.scm(p, HG.lang.listener_no_interact);
			}
		}
	}

	@EventHandler
	public void onPlayerClickLobby(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = event.getClickedBlock();
			if (b.getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign) b.getState();
				if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames")) {
					Game game = HG.manager.getGame(sign.getLine(1).substring(2));
					if (game == null) {
						Util.msg(p, HG.lang.cmd_delete_noexist);
					} else {
						if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
							game.join(p);
						} else {
							Util.msg(p, HG.lang.listener_sign_click_hand);
						}
					}
				}
			}
		} else if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
			if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK) && plugin.players.containsKey(p.getUniqueId())) {
				useTrackStick(p);
			}
		}
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();

		if (HG.manager.isInRegion(b.getLocation())) {

			if (Config.breakblocks && plugin.players.containsKey(p.getUniqueId())) {

				Game g = plugin.players.get(p.getUniqueId()).getGame();

				if (g.getStatus() == Status.RUNNING || g.getStatus() == Status.BEGINNING) {
					if (!Config.blocks.contains(b.getType().toString())) {
						Util.scm(p, HG.lang.listener_no_edit_block);
						event.setCancelled(true);
					} else {
						g.recordBlockPlace(event.getBlockReplacedState());
						if (b.getType() == Material.CHEST) {
							g.addChest(b.getLocation());
						}
					}
				} else {
					Util.scm(p, HG.lang.listener_not_running);
					event.setCancelled(true);
				}
			} else {
				if (p.hasPermission("hg.create") && HG.manager.getGame(b.getLocation()).getStatus() != Status.RUNNING)
					return;
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (HG.manager.isInRegion(b.getLocation())) {
			if (Config.breakblocks && plugin.players.containsKey(p.getUniqueId())) {
				Game g = plugin.players.get(p.getUniqueId()).getGame();
				if (g.getStatus() == Status.RUNNING) {
					if (!Config.blocks.contains(b.getType().toString())) {
						Util.scm(p, HG.lang.listener_no_edit_block);
						event.setCancelled(true);
					} else {
						g.recordBlockBreak(b);
						if (b.getType() == Material.CHEST) {
							g.removeChest(b.getLocation());
						}
					}
				} else {
					Util.scm(p, HG.lang.listener_not_running);
					event.setCancelled(true);
				}
			} else {
				if (p.hasPermission("hg.create") && HG.manager.getGame(b.getLocation()).getStatus() != Status.RUNNING)
					return;
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void blockExplode(EntityExplodeEvent e) {
		if (HG.manager.isInRegion(e.getLocation())) {
			List<Block> blocks = e.blockList();
			Game g = HG.manager.getGame(e.getLocation());
			for (Block block : blocks) {
				g.recordBlockBreak(block);
			}
			e.setYield(0);
		}
	}

	@EventHandler
	public void onLeafDecay(LeavesDecayEvent event) {
		if (!Config.fixleaves) return;
		Block b = event.getBlock();
		if (HG.manager.isInRegion(b.getLocation())) {
			if (Config.breakblocks) {
				Game g = HG.manager.getGame(b.getLocation());
				if (g.getStatus() == Status.RUNNING) {
					g.recordBlockBreak(b);
				}
			}
		}
	}

	@EventHandler
	public void onTrample(PlayerInteractEvent e) {
		if (!Config.preventtrample) return;
		Player p = e.getPlayer();
		if (HG.manager.isInRegion(p.getLocation())) {
			if (e.getAction() == Action.PHYSICAL) {
				Material block = e.getClickedBlock().getType();
				if (block == Material.FARMLAND) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (plugin.players.containsKey(p.getUniqueId()) && plugin.players.get(p.getUniqueId()).getGame().getStatus() == Status.WAITING) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent e) {
		Entity entity = e.getEntity();
		if (!(entity instanceof Player)) {
			if (HG.manager.isInRegion(e.getLocation())) {
				Game g = HG.manager.getGame(e.getLocation());
				if (g.getStatus() != Status.RUNNING) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onlogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.players.containsKey(player.getUniqueId())) {
			plugin.players.get(player.getUniqueId()).getGame().leave(player, false);
		}
	}

}
