package tk.shanebee.hg.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.data.Leaderboard;
import tk.shanebee.hg.data.PlayerData;
import tk.shanebee.hg.events.ChestOpenEvent;
import tk.shanebee.hg.events.PlayerDeathGameEvent;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.game.GameArenaData;
import tk.shanebee.hg.game.GameBlockData;
import tk.shanebee.hg.game.GameCommandData.CommandType;
import tk.shanebee.hg.game.GamePlayerData;
import tk.shanebee.hg.managers.KillManager;
import tk.shanebee.hg.managers.Manager;
import tk.shanebee.hg.managers.PlayerManager;
import tk.shanebee.hg.util.BlockUtils;
import tk.shanebee.hg.util.Util;

import java.util.Collections;
import java.util.UUID;

/**
 * Internal event listener
 */
public class GameListener implements Listener {

	private final HG plugin;
	private final Language lang;
	private final String tsn = ChatColor.GOLD + "TrackingStick " + ChatColor.GREEN + "Uses: ";
	private final ItemStack trackingStick;
    private final KillManager killManager;
    private final Manager gameManager;
    private final PlayerManager playerManager;
    private final Leaderboard leaderboard;

	public GameListener(HG plugin) {
		this.plugin = plugin;
		this.lang = plugin.getLang();
		this.gameManager = plugin.getManager();
		this.playerManager = plugin.getPlayerManager();
		this.leaderboard = plugin.getLeaderboard();
		ItemStack it = new ItemStack(Material.STICK, 1);
		ItemMeta im = it.getItemMeta();
		assert im != null;
		im.setDisplayName(tsn + Config.trackingstickuses);
		it.setItemMeta(im);
		trackingStick = it;
        killManager = plugin.getKillManager();
        BlockUtils.setupBuilder();
	}

    private void dropInv(Player player) {
		PlayerInventory inv = player.getInventory();
		Location loc = player.getLocation();
		World world = loc.getWorld();
		if (world == null) return;

		for (ItemStack i : inv.getStorageContents()) {
			if (i != null && i.getType() != Material.AIR && !isCursed(i)) {
				world.dropItemNaturally(loc, i).setPersistent(false);
			}
		}
		for (ItemStack i : inv.getArmorContents()) {
			if (i != null && i.getType() != Material.AIR && !isCursed(i)) {
				world.dropItemNaturally(loc, i).setPersistent(false);
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isCursed(ItemStack itemStack) {
		return itemStack.containsEnchantment(Enchantment.BINDING_CURSE) || itemStack.containsEnchantment(Enchantment.VANISHING_CURSE);
	}

	private void checkStick(Game g) {
		if (Config.playersfortrackingstick == g.getGamePlayerData().getPlayers().size()) {
			for (UUID u : g.getGamePlayerData().getPlayers()) {
				Player player = Bukkit.getPlayer(u);
				if (player != null) {
					Util.scm(player, lang.track_bar);
					Util.scm(player, lang.track_new1);
					Util.scm(player, lang.track_new2);
					Util.scm(player, lang.track_bar);
					player.getInventory().addItem(trackingStick);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onAttack(EntityDamageByEntityEvent event) {
		Entity defender = event.getEntity();
		Entity damager = event.getDamager();

		if (damager instanceof Player) {
			if (playerManager.hasSpectatorData(((Player) damager))) {
				event.setCancelled(true);
				return;
			}
		}
		if (defender instanceof Player) {
			Player player = (Player) defender;
			PlayerData playerData = playerManager.getPlayerData(player);

			if (playerData != null) {
				Game game = playerData.getGame();

				if (game.getGameArenaData().getStatus() != Status.RUNNING) {
					event.setCancelled(true);
				} else if (event.getFinalDamage() >= player.getHealth()) {
					if (hasTotem(player)) return;
					event.setCancelled(true);
					processDeath(player, game, damager, event.getCause());
				}
			}
		}

		// Stop players from removing items from item frames
		if (defender instanceof Hanging) {
            handleItemFrame((Hanging) event.getEntity(), event, !Config.itemframe_take);
        }
	}

	@EventHandler // Prevent players breaking item frames
    private void onBreakItemFrame(HangingBreakByEntityEvent event) {
	    handleItemFrame(event.getEntity(),event, true);
    }

	private void handleItemFrame(Hanging itemFrame, Event event, boolean cancel) {
	    if (gameManager.isInRegion(itemFrame.getLocation())) {
	        Game game = gameManager.getGame(itemFrame.getLocation());
	        switch (game.getGameArenaData().getStatus()) {
                case RUNNING:
                case BEGINNING:
                case COUNTDOWN:
                    if (cancel) {
                        ((Cancellable) event).setCancelled(true);
                    } else if (itemFrame instanceof ItemFrame){
                        game.getGameBlockData().recordItemFrame(((ItemFrame) itemFrame));
                    }
            }
        }
    }

	@EventHandler(priority =  EventPriority.HIGHEST)
	private void onDeathByOther(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = ((Player) event.getEntity());
			if (playerManager.hasSpectatorData(player)) {
				event.setCancelled(true);
				player.setFireTicks(0);
				return;
			}
			if (event instanceof EntityDamageByEntityEvent) return;
			PlayerData pd = playerManager.getPlayerData(player);
			if (pd != null) {
				if (event.getFinalDamage() >= player.getHealth()) {
					if (hasTotem(player)) return;
					event.setCancelled(true);
					processDeath(player, pd.getGame(), null, event.getCause());
				}
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
    private boolean hasTotem(Player player) {
		PlayerInventory inv = player.getInventory();
		if (inv.getItemInMainHand() != null && inv.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) return true;
        return inv.getItemInOffHand() != null && inv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING;
    }

	private void processDeath(Player player, Game game, Entity damager, EntityDamageEvent.DamageCause cause) {
		dropInv(player);
		player.setHealth(20);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			GamePlayerData gamePlayerData = game.getGamePlayerData();
			String deathString;
			if (damager instanceof Player) {
				gamePlayerData.addKill(((Player) damager));
				leaderboard.addStat(((Player) damager), Leaderboard.Stats.KILLS);
				deathString = killManager.getKillString(player.getName(), damager);
			} else if (cause == DamageCause.ENTITY_ATTACK) {
				deathString = killManager.getKillString(player.getName(), damager);
			} else if (cause == DamageCause.PROJECTILE) {
				deathString = killManager.getKillString(player.getName(), damager);
				if (killManager.isShotByPlayer(damager) && killManager.getShooter(damager) != player) {
					gamePlayerData.addKill(killManager.getShooter(damager));
                    leaderboard.addStat(killManager.getShooter(damager), Leaderboard.Stats.KILLS);
                }
			} else {
				deathString = killManager.getDeathString(cause, player.getName());
			}

			// Send death message to all players in game
			gamePlayerData.msgAll(lang.death_fallen + " &d" + deathString);

			leaderboard.addStat(player, Leaderboard.Stats.DEATHS);
			leaderboard.addStat(player, Leaderboard.Stats.GAMES);

			for (UUID uuid : game.getGamePlayerData().getPlayers()) {
				Player alive = Bukkit.getPlayer(uuid);
				if (alive != null && player != alive) {
					alive.playSound(alive.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
				}
			}

			gamePlayerData.leave(player, true);
			game.getGameCommandData().runCommands(CommandType.DEATH, player);

			// Call our death event so other plugins can pick up the fake death
			PlayerDeathGameEvent event = new PlayerDeathGameEvent(player, deathString, game);
			Bukkit.getPluginManager().callEvent(event);
			// Call bukkit player death event so other plugins can pick up on that too
			PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(player, Collections.emptyList(), 0, deathString);
			Bukkit.getPluginManager().callEvent(playerDeathEvent);

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> checkStick(game), 40L);
		}, 1);

	}

	@EventHandler
	private void onSprint(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		if (playerManager.hasPlayerData(player)) {
			Status status = playerManager.getPlayerData(player).getGame().getGameArenaData().getStatus();
			if (status == Status.WAITING || status == Status.COUNTDOWN) {
				player.setFoodLevel(1);
				event.setCancelled(true);
			}
		}
		// Prevent spectators from losing food level
		if (playerManager.hasSpectatorData(player)) {
            player.setFoodLevel(20);
		    event.setCancelled(true);
        }
	}

	private void useTrackStick(Player p) {
		ItemStack i = p.getInventory().getItemInMainHand();
		ItemMeta im = i.getItemMeta();
		assert im != null;
		im.getDisplayName();
		if (im.getDisplayName().startsWith(tsn)) {
			int uses = Integer.parseInt(im.getDisplayName().replace(tsn, ""));
			if (uses == 0) {
				Util.scm(p, lang.track_empty);
			} else {
				PlayerData pd = playerManager.getPlayerData(p);
				final Game g = pd.getGame();
				for (Entity e : p.getNearbyEntities(120, 50, 120)) {
					if (e instanceof Player) {
						if (!g.getGamePlayerData().getPlayers().contains(e.getUniqueId())) continue;
						im.setDisplayName(tsn + (uses - 1));
						Location l = e.getLocation();
						int range = (int) p.getLocation().distance(l);
						Util.scm(p, lang.track_nearest
								.replace("<player>", e.getName())
								.replace("<range>", String.valueOf(range))
								.replace("<location>", getDirection(p.getLocation().getBlock(), l.getBlock())));
						i.setItemMeta(im);
						p.updateInventory();
						return;
					}
				}
				Util.scm(p, lang.track_no_near);
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
	private void onTarget(EntityTargetEvent event) {
		Entity target = event.getTarget();
		if (target instanceof Player) {
			if (playerManager.hasSpectatorData(((Player) target))) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onChestOpen(ChestOpenEvent event) {
		Block block = event.getChest();
		Game game = event.getGame();
		GameBlockData gameBlockData = game.getGameBlockData();
		if (!gameBlockData.isLoggedChest(block.getLocation())) {
			HG.getPlugin().getManager().fillChests(block, game, event.isBonus());
			gameBlockData.addGameChest(block.getLocation());
		}
	}

	@EventHandler
	private void onChestUse(PlayerInteractEvent event) {
		//noinspection deprecation
		if (event.isCancelled()) return;
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && playerManager.hasPlayerData(player)) {
			Block block = event.getClickedBlock();
			assert block != null;
			PlayerData pd = playerManager.getPlayerData(player);
			if (block.getType() == Material.CHEST) {
				Bukkit.getServer().getPluginManager().callEvent(new ChestOpenEvent(pd.getGame(), block, false));
			} else if (BlockUtils.isBonusBlock(block)) {
				Bukkit.getServer().getPluginManager().callEvent(new ChestOpenEvent(pd.getGame(), block, true));
			}
		}
	}

    private boolean isSpectatorCompass(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return false;
        if (!playerManager.hasSpectatorData(player)) return false;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.COMPASS) return false;
        return item.getItemMeta() != null && item.getItemMeta().getDisplayName().equalsIgnoreCase(Util.getColString(lang.spectator_compass));

    }

    private void handleSpectatorCompass(Player player) {
        GamePlayerData gamePlayerData = playerManager.getSpectatorData(player).getGame().getGamePlayerData();
        gamePlayerData.getSpectatorGUI().openInventory(player);
    }

	@EventHandler
	private void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
        if (playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            if (isSpectatorCompass(event)) {
                handleSpectatorCompass(player);
            }
        } else if (action != Action.PHYSICAL && playerManager.hasPlayerData(player)) {
            Status status = playerManager.getPlayerData(player).getGame().getGameArenaData().getStatus();
            if (status != Status.RUNNING && status != Status.BEGINNING) {
				if(event.getItem().getType() == Material.RED_BED){
					//Bukkit.dispatchCommand(event.getPlayer(), "hg leave");
					playerManager.getPlayerData(player).getGame().getGamePlayerData().leave(player, false);
				} else if (event.getItem().getType() == Material.NETHER_STAR){
					playerManager.getPlayerData(player).getGame().startFreeRoam();
				} else {
					event.setCancelled(true);
					Util.scm(player, lang.listener_no_interact);
				}
            }
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			assert block != null;
			if (Util.isWallSign(block.getType())) {
				Sign sign = (Sign) block.getState();
				if (sign.getLine(0).equals(Util.getColString(lang.lobby_sign_1_1))) {
					Game game = gameManager.getGame(sign.getLine(1).substring(2));
					if (game == null) {
						Util.scm(player, lang.cmd_delete_noexist);
					} else {
						if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
						    // Process this after event has finished running to prevent double click issues
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> game.getGamePlayerData().join(player), 2);
						} else {
							Util.scm(player, lang.listener_sign_click_hand);
						}
					}
				}
			}
		} else if (action == Action.LEFT_CLICK_AIR) {
			if (player.getInventory().getItemInMainHand().getType().equals(Material.STICK) && playerManager.hasPlayerData(player)) {
				useTrackStick(player);
			}
		}
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent e){
		if (e.getWhoClicked() instanceof Player){
			Player player = (Player) e.getWhoClicked();
			InventoryAction action = e.getAction(); //might not be needed
			Game game = playerManager.getPlayerData(player).getGame();
			Status status = game.getGameArenaData().getStatus();
			if (status != Status.RUNNING && status != Status.BEGINNING) {
				if (e.getClickedInventory().contains(Material.RED_BED)) {
					game.getGamePlayerData().leave(player,false);
				} else if (e.getClickedInventory().contains(Material.NETHER_STAR)) {
					game.startFreeRoam();
				}
			} else {
				e.setCancelled(true);
			}
			return;
		}
		e.setCancelled(true);
	}

	@EventHandler
	private void blockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (playerManager.hasSpectatorData(player)) {
			event.setCancelled(true);
		}
		if (gameManager.isInRegion(block.getLocation())) {

			if (Config.breakblocks && playerManager.hasPlayerData(player)) {
                Game game = playerManager.getPlayerData(player).getGame();
                GameBlockData gameBlockData = game.getGameBlockData();
                Status status = game.getGameArenaData().getStatus();
				if (status == Status.RUNNING || status == Status.BEGINNING) {
					if (!BlockUtils.isBreakableBlock(block)) {
						Util.scm(player, lang.listener_no_edit_block);
						event.setCancelled(true);
					} else {
						gameBlockData.recordBlockPlace(event.getBlockReplacedState());
						if (isChest(block)) {
							gameBlockData.addPlayerChest(block.getLocation());
                        }
					}
				} else {
					Util.scm(player, lang.listener_not_running);
					event.setCancelled(true);
				}
			} else {
				if (player.hasPermission("hg.create")) {
				    Game game = plugin.getManager().getGame(block.getLocation());
				    Status status = game.getGameArenaData().getStatus();
				    switch (status) {
                        case BEGINNING:
                        case RUNNING:
                            game.getGameBlockData().recordBlockPlace(event.getBlockReplacedState());
                        default:
                            return;
                    }
                }
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void blockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (playerManager.hasSpectatorData(player)) {
			event.setCancelled(true);
		}
		if (gameManager.isInRegion(block.getLocation())) {

			if (Config.breakblocks && playerManager.hasPlayerData(player)) {
                Game game = playerManager.getPlayerData(player).getGame();
				if (game.getGameArenaData().getStatus() == Status.RUNNING || !Config.protectCooldown) {
					if (!BlockUtils.isBreakableBlock(block)) {
						Util.scm(player, lang.listener_no_edit_block);
						event.setCancelled(true);
					} else {
						GameBlockData gameBlockData = game.getGameBlockData();
						gameBlockData.recordBlockBreak(block);
						if (isChest(block)) {
                            gameBlockData.removeGameChest(block.getLocation());
                            gameBlockData.removePlayerChest(block.getLocation());
                        }
					}
				} else {
					Util.scm(player, lang.listener_not_running);
					event.setCancelled(true);
				}
			} else {
                if (!playerManager.hasPlayerData(player) && player.hasPermission("hg.create")) {
                    Game game = gameManager.getGame(block.getLocation());
                    Status status = game.getGameArenaData().getStatus();
                    switch (status) {
                        case BEGINNING:
                        case RUNNING:
							game.getGameBlockData().removeGameChest(block.getLocation());
                        default:
                            return;
                    }
                }
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
    private void onBucketEmpty(PlayerBucketEmptyEvent event) {
	    handleBucketEvent(event, false);
    }

    @EventHandler
    private void onBucketDrain(PlayerBucketFillEvent event) {
	    handleBucketEvent(event, true);
    }

    private void handleBucketEvent(PlayerBucketEvent event, boolean fill) {
	    Block block;
	    if (Util.methodExists(PlayerBucketEvent.class, "getBlock")) {
	        block = event.getBlock();
        } else {
	        block = event.getBlockClicked().getRelative(event.getBlockFace());
        }
	    Player player = event.getPlayer();
	    final boolean WATER = event.getBucket() == Material.WATER_BUCKET && (Config.blocks.contains("WATER") || Config.blocks.contains("ALL"));
	    final boolean LAVA = event.getBucket() == Material.LAVA_BUCKET && (Config.blocks.contains("LAVA") || Config.blocks.contains("ALL"));

        if (plugin.getManager().isInRegion(block.getLocation())) {
            if (Config.breakblocks && playerManager.hasPlayerData(player)) {
                Game game = playerManager.getPlayerData(player).getGame();
                GameBlockData gameBlockData = game.getGameBlockData();
                if (game.getGameArenaData().getStatus() == Status.RUNNING || !Config.protectCooldown) {
                    if (fill && BlockUtils.isBreakableBlock(block)) {
                        gameBlockData.recordBlockBreak(block);
                    } else if (!fill && (WATER || LAVA)) {
                        gameBlockData.recordBlockPlace(block.getState());
                    } else {
                        Util.scm(player, plugin.getLang().listener_no_edit_block);
                        event.setCancelled(true);
                    }
                } else {
                    Util.scm(player, plugin.getLang().listener_not_running);
                    event.setCancelled(true);
                }
            } else {
                if (playerManager.hasPlayerData(player) || !player.hasPermission("hg.create")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isChest(Block block) {
        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getState() instanceof Shulker) {
            return true;
        }
        return Util.isRunningMinecraft(1, 14) && block.getType() == Material.BARREL;
    }

	@EventHandler
	private void onBlockFall(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		if (Config.breakblocks && gameManager.isInRegion(block.getLocation())) {
			Game game = gameManager.getGame(block.getLocation());
			Status status = game.getGameArenaData().getStatus();
			if (status == Status.RUNNING || status == Status.BEGINNING) {
				game.getGameBlockData().recordBlockBreak(block);
			}
		}
	}

	@EventHandler
	private void onFallingBlockLand(EntityChangeBlockEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.LAVA) {
			if (Config.breakblocks && gameManager.isInRegion(event.getEntity().getLocation())) {
				Game game = gameManager.getGame(event.getEntity().getLocation());
				Status status = game.getGameArenaData().getStatus();
				if (status == Status.RUNNING || status == Status.BEGINNING) {
					game.getGameBlockData().recordBlockPlace(block.getState());
				}
			}
		}
	}

	@EventHandler
	private void onEntityExplode(EntityExplodeEvent event) {
		if (gameManager.isInRegion(event.getLocation())) {
			Game game = gameManager.getGame(event.getLocation());
			for (Block block : event.blockList()) {
				game.getGameBlockData().recordBlockBreak(block);
			}
			event.setYield(0);
		}
	}

	@EventHandler
	private void onBlockExplode(BlockExplodeEvent event) {
		if (gameManager.isInRegion(event.getBlock().getLocation())) {
			GameBlockData gameBlockData = gameManager.getGame(event.getBlock().getLocation()).getGameBlockData();
			for (Block block : event.blockList()) {
				gameBlockData.recordBlockBreak(block);
			}
			event.setYield(0);
		}
	}

	@EventHandler
	private void onLeafDecay(LeavesDecayEvent event) {
		if (!Config.fixleaves) return;
		Block block = event.getBlock();
		if (gameManager.isInRegion(block.getLocation())) {
			if (Config.breakblocks) {
				Game game = gameManager.getGame(block.getLocation());
				if (game.getGameArenaData().getStatus() == Status.RUNNING) {
					game.getGameBlockData().recordBlockBreak(block);
				}
			}
		}
	}

	@EventHandler
	private void onTrample(PlayerInteractEvent event) {
		if (!Config.preventtrample) return;
		Player player = event.getPlayer();
		if (playerManager.hasSpectatorData(player)) {
			event.setCancelled(true);
		}
		if (gameManager.isInRegion(player.getLocation())) {
			if (event.getAction() == Action.PHYSICAL) {
				assert event.getClickedBlock() != null;
				Material block = event.getClickedBlock().getType();
				if (block == Material.FARMLAND) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		PlayerData playerData = playerManager.getPlayerData(player);
		if (playerData != null) {
			Status status = playerData.getGame().getGameArenaData().getStatus();
		    if (status != Status.BEGINNING && status != Status.RUNNING) {
                event.setCancelled(true);
            }
		}
		// Prevent spectators from dropping items
		if (playerManager.hasSpectatorData(player)) {
		    event.setCancelled(true);
        }
	}

    @EventHandler
	private void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ItemFrame || entity instanceof ArmorStand) return;
        if (!(entity instanceof Player)) {
            if (gameManager.isInRegion(event.getLocation())) {
                Game game = gameManager.getGame(event.getLocation());
                if (entity instanceof LivingEntity) {
                    if (game.getGameArenaData().getStatus() != Status.RUNNING) {
                        event.setCancelled(true);
                        return;
                    }
                    if (event instanceof CreatureSpawnEvent) {
                        SpawnReason reason = ((CreatureSpawnEvent) event).getSpawnReason();
                        switch (reason) {
                            case DEFAULT:
                            case NATURAL:
                                event.setCancelled(true);
                                return;
                        }
                    }
                }
                entity.setPersistent(false);
                game.getGameArenaData().getBound().addEntity(entity);
            }
        }
    }

	@EventHandler
	private void onPickup(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player) {
			if (playerManager.hasSpectatorData(event.getEntity().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onLogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (playerManager.hasPlayerData(player)) {
		    PlayerData playerData = playerManager.getPlayerData(player);
		    playerData.setOnline(false);
			playerData.getGame().getGamePlayerData().leave(player, false);
		}
		if (playerManager.hasSpectatorData(player)) {
		    PlayerData playerData = playerManager.getSpectatorData(player);
            playerData.setOnline(false);
			playerData.getGame().getGamePlayerData().leaveSpectate(player);
		}
	}

	@EventHandler
	private void onEntityShoot(EntityShootBowEvent event) {
	    LivingEntity entity = event.getEntity();
		if (entity.hasMetadata("death-message")) {
			event.getProjectile().setMetadata("death-message",
					new FixedMetadataValue(plugin, entity.getMetadata("death-message").get(0).asString()));
		}
		if (entity instanceof Player && playerManager.hasPlayerData(entity.getUniqueId())) {
		    event.getProjectile().setMetadata("shooter", new FixedMetadataValue(plugin, entity.getName()));
        }
	}

	@EventHandler
	private void onChat(AsyncPlayerChatEvent event) {
		if (!Config.spectateChat) {
			Player spectator = event.getPlayer();
			if (playerManager.hasSpectatorData(spectator)) {
				PlayerData data = playerManager.getSpectatorData(spectator);
				Game game = data.getGame();
				for (UUID uuid : game.getGamePlayerData().getPlayers()) {
					Player player = Bukkit.getPlayer(uuid);
					event.getRecipients().remove(player);
				}
			}
		}
	}

	@EventHandler
	private void onTeleportIntoArena(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location location = event.getTo();
		for (Game game : plugin.getGames()) {
			GameArenaData gameArenaData = game.getGameArenaData();
			if (gameArenaData.isInRegion(location) && gameArenaData.getStatus() == Status.RUNNING) {
				if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && !game.getGamePlayerData().getPlayers().contains(player.getUniqueId()) && !game.getGamePlayerData().getSpectators().contains(player.getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}

}
