package com.shanebeestudios.hg.listeners;

import org.bukkit.event.Listener;
import com.shanebeestudios.hg.HG;
import com.shanebeestudios.hg.managers.PlayerManager;

/**
 * Internal mcMMO listeners
 */
public class McmmoListeners implements Listener {

	private HG plugin;
	private final PlayerManager playerManager;

	public McmmoListeners(HG plugin) {
		this.plugin = plugin;
		this.playerManager = plugin.getPlayerManager();
	}

	// TODO are we going to bother with McMMO?
//	// Handle mcMMO EXP gain events
//	@EventHandler
//	private void mcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
//		handleExpEvent(event);
//	}
//
//	@EventHandler
//	private void mcMMOLevelDown(McMMOPlayerLevelDownEvent event) {
//		handleExpEvent(event);
//	}
//
//	@EventHandler
//	private void mcMMOXpGain(McMMOPlayerXpGainEvent event) {
//		handleExpEvent(event);
//	}
//
//	private void handleExpEvent(McMMOPlayerExperienceEvent event) {
//		if (!Config.mcmmoGainExp) {
//			Player player = event.getPlayer();
//			if (playerManager.hasPlayerData(player.getUniqueId())) {
//			//if (playerManager.hasPlayerData(player.getUniqueId())) {
//				event.setCancelled(true);
//			}
//		}
//	}
//
//	// Handle mcMMO skill use events
//	@EventHandler
//	private void mcMMOUseSkill(McMMOPlayerAbilityActivateEvent event) {
//		if (!Config.mcmmoUseSkills) {
//			Player player = event.getPlayer();
//			if (playerManager.hasPlayerData(player.getUniqueId())) {
//				event.setCancelled(true);
//			}
//		}
//	}
//
//	@EventHandler
//	private void mcMMOUseSubSkill(SubSkillEvent event) {
//		if (!Config.mcmmoUseSkills) {
//			Player player = event.getPlayer();
//			if (playerManager.hasPlayerData(player.getUniqueId())) {
//				event.setCancelled(true);
//			}
//		}
//	}
//
//	@EventHandler
//	private void blockBreakEvent(FakeBlockBreakEvent event) {
//		if (!Config.mcmmoUseSkills) {
//			Player player = event.getPlayer();
//			if (playerManager.hasPlayerData(player.getUniqueId())) {
//				event.setCancelled(true);
//			}
//		}
//	}
//
//	@EventHandler
//	protected void blockDamageEvent(FakeBlockDamageEvent event) {
//		if (!Config.mcmmoUseSkills) {
//			Player player = event.getPlayer();
//			if (playerManager.hasPlayerData(player.getUniqueId())) {
//				event.setCancelled(true);
//			}
//		}
//	}
//
//	@EventHandler
//	private void entityDamageByEntityEvent(FakeEntityDamageByEntityEvent event) {
//		if (!Config.mcmmoUseSkills) {
//			Entity damager = event.getDamager();
//			Entity victim = event.getEntity();
//			if (playerManager.hasPlayerData(damager.getUniqueId()) || playerManager.hasPlayerData(victim.getUniqueId())) {
//				event.setCancelled(true);
//			}
//		}
//	}
//
//	@EventHandler
//	private void entityDamageEvent(FakeEntityDamageEvent event) {
//		if (!Config.mcmmoUseSkills) {
//			if (event.getEntity() instanceof Player) {
//				Player player = ((Player) event.getEntity());
//				if (playerManager.hasPlayerData(player.getUniqueId())) {
//					event.setCancelled(true);
//				}
//			}
//		}
//	}
//
//	@EventHandler
//	private void fishEvent(FakePlayerFishEvent event) {
//		if (!Config.mcmmoUseSkills) {
//			Player player = event.getPlayer();
//			if (playerManager.hasPlayerData(player.getUniqueId())) {
//				event.setCancelled(true);
//			}
//		}
//	}
//
//	@EventHandler
//	private void itemSpawnEvent(McMMOItemSpawnEvent event) {
//		if (!Config.mcmmoUseSkills) {
//			Location loc = event.getLocation();
//			plugin.getGames().stream().filter(game -> game.getGameArenaData().isInRegion(loc)).map(game -> true).forEach(event::setCancelled);
//		}
//	}

}
