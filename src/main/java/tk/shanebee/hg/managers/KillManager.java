package tk.shanebee.hg.managers;

import tk.shanebee.hg.HG;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Manager for deaths in game
 */
public class KillManager {

    /** Get the death message when a player dies of natural causes (non-entity involved deaths)
     * @param dc Cause of the damage
     * @param name Name of the player
     * @return Message that will be sent when the player dies
     */
	public String getDeathString(DamageCause dc, String name) {
		switch (dc) {
			case ENTITY_EXPLOSION:
			case BLOCK_EXPLOSION:
				return (HG.getPlugin().getLang().death_explosion.replace("<player>", name));
			case CUSTOM:
				return (HG.getPlugin().getLang().death_custom.replace("<player>", name));
			case FALL:
				return (HG.getPlugin().getLang().death_fall.replace("<player>", name));
			case FALLING_BLOCK:
				return (HG.getPlugin().getLang().death_falling_block.replace("<player>", name));
			case FIRE:
			case FIRE_TICK:
				return (HG.getPlugin().getLang().death_fire.replace("<player>", name));
			case PROJECTILE:
				return (HG.getPlugin().getLang().death_projectile.replace("<player>", name));
			case LAVA:
				return (HG.getPlugin().getLang().death_lava.replace("<player>", name));
			case MAGIC:
				return (HG.getPlugin().getLang().death_magic.replace("<player>", name));
			case SUICIDE:
				return (HG.getPlugin().getLang().death_suicide.replace("<player>", name));
			default:
				return (HG.getPlugin().getLang().death_other_cause.replace("<player>", name).replace("<cause>", dc.toString().toLowerCase()));
		}
	}

    /** Get the death message when a player is killed by an entity
     * @param name Name of player whom died
     * @param entity Entity that killed this player
     * @return
     */
	public String getKillString(String name, Entity entity) {
		if (entity.hasMetadata("death-message")) {
			return entity.getMetadata("death-message").get(0).asString().replace("<player>", name);
		}
		switch (entity.getType()) {
			case PLAYER:
				String weapon;
				if (((Player) entity).getInventory().getItemInMainHand().getType() == Material.AIR)
					weapon = "fist";
				else
					weapon = ((Player) entity).getInventory().getItemInMainHand().getType().name().toLowerCase();
				return (HG.getPlugin().getLang().death_player.replace("<player>", name)
						.replace("<killer>", entity.getName())
						.replace("<weapon>", weapon));
			case ZOMBIE:
				return (HG.getPlugin().getLang().death_zombie.replace("<player>", name));
			case SKELETON:
			case ARROW:
				return (HG.getPlugin().getLang().death_skeleton.replace("<player>", name));
			case SPIDER:
				return (HG.getPlugin().getLang().death_spider.replace("<player>", name));
			case DROWNED:
				return (HG.getPlugin().getLang().death_drowned.replace("<player>", name));
			case TRIDENT:
				return (HG.getPlugin().getLang().death_trident.replace("<player>", name));
			case STRAY:
				return (HG.getPlugin().getLang().death_stray.replace("<player>", name));
			default:
				return (HG.getPlugin().getLang().death_other_entity.replace("<player>", name));
		}
	}
}
