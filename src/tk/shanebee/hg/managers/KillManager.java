package tk.shanebee.hg.managers;

import tk.shanebee.hg.HG;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class KillManager {

	public String getDeathString(DamageCause dc, String name) {
		switch (dc) {
			case ENTITY_EXPLOSION:
			case BLOCK_EXPLOSION:
				return (HG.plugin.lang.death_explosion.replace("<player>", name));
			case CUSTOM:
				return (HG.plugin.lang.death_custom.replace("<player>", name));
			case FALL:
				return (HG.plugin.lang.death_fall.replace("<player>", name));
			case FALLING_BLOCK:
				return (HG.plugin.lang.death_falling_block.replace("<player>", name));
			case FIRE:
			case FIRE_TICK:
				return (HG.plugin.lang.death_fire.replace("<player>", name));
			case PROJECTILE:
				return (HG.plugin.lang.death_projectile.replace("<player>", name));
			case LAVA:
				return (HG.plugin.lang.death_lava.replace("<player>", name));
			case MAGIC:
				return (HG.plugin.lang.death_magic.replace("<player>", name));
			case SUICIDE:
				return (HG.plugin.lang.death_suicide.replace("<player>", name));
			default:
				return (HG.plugin.lang.death_other_cause.replace("<player>", name).replace("<cause>", dc.toString().toLowerCase()));
		}
	}

	public String getKillString(String name, Entity e) {
		switch (e.getType()) {
			case PLAYER:
				String weapon;
				if (((Player) e).getInventory().getItemInMainHand().getType() == Material.AIR)
					weapon = "fist";
				else
					weapon = ((Player) e).getInventory().getItemInMainHand().getType().name().toLowerCase();
				return (HG.plugin.lang.death_player.replace("<player>", name)
						.replace("<killer>", e.getName())
						.replace("<weapon>", weapon));
			case ZOMBIE:
				return (HG.plugin.lang.death_zombie.replace("<player>", name));
			case SKELETON:
			case ARROW:
				return (HG.plugin.lang.death_skeleton.replace("<player>", name));
			case SPIDER:
				return (HG.plugin.lang.death_spider.replace("<player>", name));
			case DROWNED:
				return (HG.plugin.lang.death_drowned.replace("<player>", name));
			case TRIDENT:
				return (HG.plugin.lang.death_trident.replace("<player>", name));
			case STRAY:
				return (HG.plugin.lang.death_stray.replace("<player>", name));
			default:
				return (HG.plugin.lang.death_other_entity.replace("<player>", name));
		}
	}
}
