package com.shanebeestudios.hg.plugin.listeners;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.api.util.BlockUtils;
import com.shanebeestudios.hg.api.util.Constants;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.Leaderboard;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.events.ChestOpenEvent;
import com.shanebeestudios.hg.events.PlayerDeathGameEvent;
import com.shanebeestudios.hg.game.GameRegion;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.game.GameBlockData;
import com.shanebeestudios.hg.game.GameCommandData.CommandType;
import com.shanebeestudios.hg.game.GamePlayerData;
import com.shanebeestudios.hg.managers.GameManager;
import com.shanebeestudios.hg.managers.KillManager;
import com.shanebeestudios.hg.managers.PlayerManager;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

/**
 * Internal event listener
 */
public class GameListener implements Listener {

    private final HungerGames plugin;
    private final Language lang;
    private final String tsn = ChatColor.GOLD + "TrackingStick " + ChatColor.GREEN + "Uses: ";
    private final ItemStack trackingStick;
    private final KillManager killManager;
    private final GameManager gameManager;
    private final PlayerManager playerManager;
    private final Leaderboard leaderboard;

    public GameListener(HungerGames plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.gameManager = plugin.getGameManager();
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

    private void dropInventoryOfPlayer(Player player) {
        PlayerInventory inv = player.getInventory();
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        for (ItemStack itemStack : inv.getStorageContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR && !isCursed(itemStack)) {
                world.dropItemNaturally(loc, itemStack).setPersistent(false);
            }
        }
        for (ItemStack itemStack : inv.getArmorContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR && !isCursed(itemStack)) {
                world.dropItemNaturally(loc, itemStack).setPersistent(false);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isCursed(ItemStack itemStack) {
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            if (enchantment.isCursed()) return true;
        }
        return false;
    }

    private void checkStick(Game g) {
        if (Config.playersfortrackingstick == g.getGamePlayerData().getPlayers().size()) {
            for (Player player : g.getGamePlayerData().getPlayers()) {
                Util.sendMessage(player, lang.track_bar);
                Util.sendMessage(player, lang.track_new1);
                Util.sendMessage(player, lang.track_new2);
                Util.sendMessage(player, lang.track_bar);
                player.getInventory().addItem(trackingStick);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAttack(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity attacker = event.getDamager();

        if (attacker instanceof Player attackerPlayer) {
            if (this.playerManager.hasSpectatorData(attackerPlayer)) {
                event.setCancelled(true);
                return;
            }
        }
        if (victim instanceof Player victimPlayer) {
            PlayerData playerData = playerManager.getPlayerData(victimPlayer);

            if (playerData != null) {
                Game game = playerData.getGame();

                if (game.getGameArenaData().getStatus() != Status.RUNNING) {
                    event.setCancelled(true);
                } else if (event.getFinalDamage() >= victimPlayer.getHealth()) {
                    if (hasTotem(victimPlayer)) return;
                    event.setCancelled(true);
                    processDeath(victimPlayer, game, attacker, event.getCause());
                }
            }
        }

        // Stop players from removing items from item frames
        if (victim instanceof Hanging hanging) {
            handleItemFrame(hanging, event, !Config.itemframe_take);
        }
    }

    @EventHandler // Prevent players breaking item frames
    private void onBreakItemFrame(HangingBreakByEntityEvent event) {
        handleItemFrame(event.getEntity(), event, true);
    }

    private void handleItemFrame(Hanging hanging, Event event, boolean cancel) {
        if (this.gameManager.isInRegion(hanging.getLocation())) {
            Game game = this.gameManager.getGame(hanging.getLocation());
            switch (game.getGameArenaData().getStatus()) {
                case RUNNING:
                case FREE_ROAM:
                case COUNTDOWN:
                    if (cancel && event instanceof Cancellable cancellable) {
                        cancellable.setCancelled(true);
                    } else if (hanging instanceof ItemFrame itemFrame) {
                        game.getGameBlockData().recordItemFrame(itemFrame);
                    }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDeathByOther(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (playerManager.hasSpectatorData(player)) {
                event.setCancelled(true);
                player.setFireTicks(0);
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) return;
            PlayerData playerData = playerManager.getPlayerData(player);
            if (playerData != null) {
                if (event.getFinalDamage() >= player.getHealth()) {
                    if (hasTotem(player)) return;
                    event.setCancelled(true);
                    processDeath(player, playerData.getGame(), null, event.getCause());
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean hasTotem(Player player) {
        PlayerInventory inv = player.getInventory();
        if (inv.getItemInMainHand() != null && inv.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING)
            return true;
        return inv.getItemInOffHand() != null && inv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING;
    }

    private void processDeath(Player player, Game game, Entity attacker, EntityDamageEvent.DamageCause cause) {
        dropInventoryOfPlayer(player);
        player.setHealth(20);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            GamePlayerData gamePlayerData = game.getGamePlayerData();
            String deathString;
            if (attacker instanceof Player attackerPlayer) {
                gamePlayerData.addKill(attackerPlayer);
                leaderboard.addStat(attackerPlayer, Leaderboard.Stats.KILLS);
                deathString = killManager.getKillString(player.getName(), attacker);
            } else if (cause == DamageCause.ENTITY_ATTACK) {
                deathString = killManager.getKillString(player.getName(), attacker);
            } else if (cause == DamageCause.PROJECTILE) {
                deathString = killManager.getKillString(player.getName(), attacker);
                if (killManager.isShotByPlayer(attacker) && killManager.getShooter(attacker) != player) {
                    gamePlayerData.addKill(killManager.getShooter(attacker));
                    leaderboard.addStat(killManager.getShooter(attacker), Leaderboard.Stats.KILLS);
                }
            } else {
                deathString = killManager.getDeathString(cause, player.getName());
            }

            // Send death message to all players in game
            gamePlayerData.msgAll(lang.death_fallen + " &d" + deathString);

            leaderboard.addStat(player, Leaderboard.Stats.DEATHS);
            leaderboard.addStat(player, Leaderboard.Stats.GAMES);

            for (Player alive : game.getGamePlayerData().getPlayers()) {
                if (alive != null && player != alive) {
                    alive.playSound(alive.getLocation(), Config.SOUNDS_DEATH, 5, 1);
                }
            }

            gamePlayerData.leave(player, true);
            game.getGameCommandData().runCommands(CommandType.DEATH, player);

            // Call our death event so other plugins can pick up the fake death
            PlayerDeathGameEvent event = new PlayerDeathGameEvent(player, deathString, game);
            Bukkit.getPluginManager().callEvent(event);
            // Call bukkit player death event so other plugins can pick up on that too
            // TODO manage damage source
//			PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(player, Collections.emptyList(), 0, deathString);
//			Bukkit.getPluginManager().callEvent(playerDeathEvent);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> checkStick(game), 40L);
        }, 1);

    }

    @EventHandler
    private void onSprint(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        // Prevent spectators from losing food level
        if (this.playerManager.hasSpectatorData(player)) {
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
                Util.sendMessage(p, lang.track_empty);
            } else {
                PlayerData pd = playerManager.getPlayerData(p);
                final Game g = pd.getGame();
                for (Entity e : p.getNearbyEntities(120, 50, 120)) {
                    if (e instanceof Player) {
                        if (!g.getGamePlayerData().getPlayers().contains(e.getUniqueId())) continue;
                        im.setDisplayName(tsn + (uses - 1));
                        Location l = e.getLocation();
                        int range = (int) p.getLocation().distance(l);
                        Util.sendMessage(p, lang.track_nearest
                            .replace("<player>", e.getName())
                            .replace("<range>", String.valueOf(range))
                            .replace("<location>", getDirection(p.getLocation().getBlock(), l.getBlock())));
                        i.setItemMeta(im);
                        p.updateInventory();
                        return;
                    }
                }
                Util.sendMessage(p, lang.track_no_near);
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
        if (target instanceof Player player) {
            if (this.playerManager.hasSpectatorData(player)) {
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
            HungerGames.getPlugin().getGameManager().fillChests(block, game, event.isBonus());
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
        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            if (isSpectatorCompass(event)) {
                handleSpectatorCompass(player);
            }
        } else if (action != Action.PHYSICAL && this.playerManager.hasPlayerData(player)) {
            Status status = this.playerManager.getPlayerData(player).getGame().getGameArenaData().getStatus();
            if (status != Status.RUNNING && status != Status.FREE_ROAM) {
                event.setCancelled(true);
                Util.sendMessage(player, lang.listener_no_interact);
            }
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            assert block != null;
            if (Tag.WALL_SIGNS.isTagged(block.getType())) {
                Sign sign = (Sign) block.getState();
                PersistentDataContainer pdc = sign.getPersistentDataContainer();
                if (pdc.has(Constants.LOBBY_SIGN_KEY, PersistentDataType.STRING)) {
                    String name = pdc.get(Constants.LOBBY_SIGN_KEY, PersistentDataType.STRING);
                    Game game = this.gameManager.getGame(name);
                    if (game == null) {
                        Util.sendMessage(player, lang.cmd_delete_noexist);
                    } else {
                        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            // Process this after event has finished running to prevent double click issues
                            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> game.joinGame(player), 2);
                        } else {
                            Util.sendMessage(player, this.lang.listener_sign_click_hand);
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
    private void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            return;
        }
        if (this.gameManager.isInRegion(block.getLocation())) {

            if (Config.breakblocks && this.playerManager.hasPlayerData(player)) {
                Game game = this.playerManager.getPlayerData(player).getGame();
                GameBlockData gameBlockData = game.getGameBlockData();
                Status status = game.getGameArenaData().getStatus();
                if (status == Status.RUNNING || status == Status.FREE_ROAM) {
                    if (!BlockUtils.isEditableBlock(block.getType())) {
                        Util.sendMessage(player, this.lang.listener_no_edit_block);
                        event.setCancelled(true);
                    } else {
                        gameBlockData.recordBlockPlace(event.getBlockReplacedState());
                        if (isChest(block)) {
                            gameBlockData.addPlayerChest(block.getLocation());
                        }
                    }
                } else {
                    Util.sendMessage(player, this.lang.listener_not_running);
                    event.setCancelled(true);
                }
            } else {
                if (Permissions.COMMAND_CREATE.has(player)) {
                    Game game = this.plugin.getGameManager().getGame(block.getLocation());
                    Status status = game.getGameArenaData().getStatus();
                    switch (status) {
                        case FREE_ROAM:
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

        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            return;
        }
        if (this.gameManager.isInRegion(block.getLocation())) {

            if (Config.breakblocks && this.playerManager.hasPlayerData(player)) {
                Game game = this.playerManager.getPlayerData(player).getGame();
                if (game.getGameArenaData().getStatus() == Status.RUNNING || !Config.protectCooldown) {
                    if (!BlockUtils.isEditableBlock(block.getType())) {
                        Util.sendMessage(player, this.lang.listener_no_edit_block);
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
                    Util.sendMessage(player, this.lang.listener_not_running);
                    event.setCancelled(true);
                }
            } else {
                if (!this.playerManager.hasPlayerData(player) && Permissions.COMMAND_CREATE.has(player)) {
                    Game game = this.gameManager.getGame(block.getLocation());
                    Status status = game.getGameArenaData().getStatus();
                    switch (status) {
                        case FREE_ROAM:
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
        Block block = event.getBlock();
        Player player = event.getPlayer();
        final boolean WATER = event.getBucket() == Material.WATER_BUCKET && BlockUtils.isEditableBlock(Material.WATER);
        final boolean LAVA = event.getBucket() == Material.LAVA_BUCKET && BlockUtils.isEditableBlock(Material.LAVA);

        if (this.gameManager.isInRegion(block.getLocation())) {
            if (Config.breakblocks && this.playerManager.hasPlayerData(player)) {
                Game game = this.playerManager.getPlayerData(player).getGame();
                GameBlockData gameBlockData = game.getGameBlockData();
                if (game.getGameArenaData().getStatus() == Status.RUNNING || !Config.protectCooldown) {
                    if (fill && BlockUtils.isEditableBlock(block.getType())) {
                        gameBlockData.recordBlockBreak(block);
                    } else if (!fill && (WATER || LAVA)) {
                        gameBlockData.recordBlockPlace(block.getState());
                    } else {
                        Util.sendMessage(player, this.lang.listener_no_edit_block);
                        event.setCancelled(true);
                    }
                } else {
                    Util.sendMessage(player, this.lang.listener_not_running);
                    event.setCancelled(true);
                }
            } else {
                if (this.playerManager.hasPlayerData(player) || !Permissions.COMMAND_CREATE.has(player)) {
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
    private void onFallingBlockLand(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        if (!Config.breakblocks || !this.gameManager.isInRegion(block.getLocation())) return;
        Game game = this.gameManager.getGame(block.getLocation());
        Status status = game.getGameArenaData().getStatus();

        Material blockType = block.getType();
        Entity entity = event.getEntity();

        if (entity instanceof FallingBlock fallingBlock && (status == Status.RUNNING || status == Status.FREE_ROAM)) {
            // Block starts falling
            if (blockType == fallingBlock.getBlockData().getMaterial()) {
                game.getGameBlockData().recordBlockBreak(block);
            }

            // Falling block lands
            if (!blockType.isSolid()) {
                game.getGameBlockData().recordBlockPlace(block.getState());
            }
        }
    }

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event) {
        if (this.gameManager.isInRegion(event.getLocation())) {
            Game game = this.gameManager.getGame(event.getLocation());
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
        if (this.gameManager.isInRegion(block.getLocation())) {
            if (Config.breakblocks) {
                Game game = this.gameManager.getGame(block.getLocation());
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
        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
            return;
        }
        if (this.gameManager.isInRegion(player.getLocation())) {
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
        PlayerData playerData = this.playerManager.getPlayerData(player);
        if (playerData != null) {
            Status status = playerData.getGame().getGameArenaData().getStatus();
            if (status != Status.FREE_ROAM && status != Status.RUNNING) {
                event.setCancelled(true);
            }
        }
        // Prevent spectators from dropping items
        if (this.playerManager.hasSpectatorData(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ItemFrame || entity instanceof ArmorStand) return;
        if (!(entity instanceof Player)) {
            if (this.gameManager.isInRegion(event.getLocation())) {
                Game game = gameManager.getGame(event.getLocation());
                if (entity instanceof LivingEntity) {
                    if (game.getGameArenaData().getStatus() != Status.RUNNING) {
                        event.setCancelled(true);
                        return;
                    }
                    if (event instanceof CreatureSpawnEvent creatureSpawnEvent) {
                        switch (creatureSpawnEvent.getSpawnReason()) {
                            case DEFAULT:
                            case NATURAL:
                                event.setCancelled(true);
                                return;
                        }
                    }
                }
                GameRegion gameRegion = game.getGameArenaData().getBound();
                if (!gameRegion.hasEntity(entity)) gameRegion.addEntity(entity);
            }
        }
    }

    @EventHandler
    private void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (this.playerManager.hasSpectatorData(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.playerManager.hasPlayerData(player)) {
            PlayerData playerData = this.playerManager.getPlayerData(player);
            assert playerData != null;
            playerData.setOnline(false);
            playerData.getGame().getGamePlayerData().leave(player, false);
        }
        if (this.playerManager.hasSpectatorData(player)) {
            PlayerData playerData = this.playerManager.getSpectatorData(player);
            assert playerData != null;
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
                for (Player player : game.getGamePlayerData().getPlayers()) {
                    event.getRecipients().remove(player);
                }
            }
        }
    }

    @EventHandler
    private void onTeleportIntoArena(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location location = event.getTo();
        for (Game game : this.plugin.getGameManager().getGames()) {
            GameArenaData gameArenaData = game.getGameArenaData();
            if (gameArenaData.isInRegion(location) && gameArenaData.getStatus() == Status.RUNNING) {
                if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && !game.getGamePlayerData().getPlayers().contains(player.getUniqueId()) && !game.getGamePlayerData().getSpectators().contains(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemoveEvent event) {
        Entity entity = event.getEntity();
        Game game = this.gameManager.getGame(entity.getLocation());
        if (game == null) return;

        GameRegion gameRegion = game.getGameArenaData().getBound();
        gameRegion.removeEntity(entity);
    }

}
