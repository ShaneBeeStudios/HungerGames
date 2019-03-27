package me.minebuilders.hg.managers;

import me.minebuilders.hg.HG;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class KillManager {

	public String getDeathString(DamageCause dc, String name) {
		switch (dc) {
			case ENTITY_EXPLOSION:
			case BLOCK_EXPLOSION:
				return (HG.lang.death_explosion.replace("<player>", name));
			case CUSTOM:
				return (HG.lang.death_custom.replace("<player>", name));
			case FALL:
				return (HG.lang.death_fall.replace("<player>", name));
			case FALLING_BLOCK:
				return (HG.lang.death_falling_block.replace("<player>", name));
			case FIRE:
			case FIRE_TICK:
				return (HG.lang.death_fire.replace("<player>", name));
			case PROJECTILE:
				return (HG.lang.death_projectile.replace("<player>", name));
			case LAVA:
				return (HG.lang.death_lava.replace("<player>", name));
			case MAGIC:
				return (HG.lang.death_magic.replace("<player>", name));
			case SUICIDE:
				return (HG.lang.death_suicide.replace("<player>", name));
			default:
				return (HG.lang.death_other_cause.replace("<player>", name).replace("<cause>", dc.toString().toLowerCase()));
		}
	}

	public String getKillString(String name, Entity e) {
		switch (e.getType()) {
			case PLAYER:
				return (HG.lang.death_player.replace("<player>", name)
						.replace("<killer>", e.getName())
						.replace("<weapon>", ((Player) e).getInventory().getItemInMainHand().getType().name().toLowerCase()));
			case ZOMBIE:
				return (HG.lang.death_zombie.replace("<player>", name));
			case SKELETON:
			case ARROW:
				return (HG.lang.death_skeleton.replace("<player>", name));
			case SPIDER:
				return (HG.lang.death_spider.replace("<player>", name));
			case DROWNED:
				return (HG.lang.death_drowned.replace("<player>", name));
			case TRIDENT:
				return (HG.lang.death_trident.replace("<player>", name));
			case STRAY:
				return (HG.lang.death_stray.replace("<player>", name));
			default:
				return (HG.lang.death_other_entity.replace("<player>", name));
		}
	}
}
