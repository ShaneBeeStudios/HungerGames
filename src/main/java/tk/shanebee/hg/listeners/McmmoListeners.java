package tk.shanebee.hg.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerExperienceEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.fake.*;
import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.HG;

/**
 * Internal mcMMO listeners
 */
public class McmmoListeners implements Listener {

	private HG plugin;

	public McmmoListeners(HG plugin) {
		this.plugin = plugin;
	}

	// Handle mcMMO EXP gain events
	@EventHandler
	private void mcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
		handleExpEvent(event);
	}

	@EventHandler
	private void mcMMOLevelDown(McMMOPlayerLevelDownEvent event) {
		handleExpEvent(event);
	}

	@EventHandler
	private void mcMMOXpGain(McMMOPlayerXpGainEvent event) {
		handleExpEvent(event);
	}

	private void handleExpEvent(McMMOPlayerExperienceEvent event) {
		if (!Config.mcmmoGainExp) {
			Player player = event.getPlayer();
			if (plugin.getPlayers().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	// Handle mcMMO skill use events
	@EventHandler
	private void mcMMOUseSkill(McMMOPlayerAbilityActivateEvent event) {
		if (!Config.mcmmoUseSkills) {
			Player player = event.getPlayer();
			if (plugin.getPlayers().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void mcMMOUseSubSkill(SubSkillEvent event) {
		if (!Config.mcmmoUseSkills) {
			Player player = event.getPlayer();
			if (plugin.getPlayers().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void blockBreakEvent(FakeBlockBreakEvent event) {
		if (!Config.mcmmoUseSkills) {
			Player player = event.getPlayer();
			if (plugin.getPlayers().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	protected void blockDamageEvent(FakeBlockDamageEvent event) {
		if (!Config.mcmmoUseSkills) {
			Player player = event.getPlayer();
			if (plugin.getPlayers().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void entityDamageByEntityEvent(FakeEntityDamageByEntityEvent event) {
		if (!Config.mcmmoUseSkills) {
			Entity damager = event.getDamager();
			Entity victim = event.getEntity();
			if (plugin.getPlayers().containsKey(damager.getUniqueId()) || plugin.getPlayers().containsKey(victim.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void entityDamageEvent(FakeEntityDamageEvent event) {
		if (!Config.mcmmoUseSkills) {
			if (event.getEntity() instanceof Player) {
				Player player = ((Player) event.getEntity());
				if (plugin.getPlayers().containsKey(player.getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	private void fishEvent(FakePlayerFishEvent event) {
		if (!Config.mcmmoUseSkills) {
			Player player = event.getPlayer();
			if (plugin.getPlayers().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void itemSpawnEvent(McMMOItemSpawnEvent event) {
		if (!Config.mcmmoUseSkills) {
			Location loc = event.getLocation();
			plugin.getGames().stream().filter(game -> game.isInRegion(loc)).map(game -> true).forEach(event::setCancelled);
		}
	}

}
