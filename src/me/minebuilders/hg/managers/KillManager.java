package me.minebuilders.hg.managers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class KillManager {

	public String getDeathString(DamageCause dc, String name) {
		switch (dc) {
			case ENTITY_EXPLOSION:
			case BLOCK_EXPLOSION:
				return (name + " Was blown to bits!");
			case CUSTOM:
				return (name + " Was killed by an unknown cause!");
			case FALL:
				return (name + " Fell to their death!");
			case FALLING_BLOCK:
				return (name + " Was smashed by a block!");
			case FIRE:
			case FIRE_TICK:
				return (name + " Was Burned alive!");
			case PROJECTILE:
				return (name + " Got hit by a projectile!");
			case LAVA:
				return (name + " Fell into a pit of lava!");
			case MAGIC:
				return (name + " Was destroyed by magic!");
			case SUICIDE:
				return (name + " Couldn't handle HungerGames!");
			default:
				return (name + " Was killed by " + dc.toString().toLowerCase());
		}
	}

	public String getKillString(String name, Entity e) {
		switch (e.getType()) {
			case PLAYER:
				return ("&6" + name + " &c&lWas killed by &6" + ((Player) e).getName() + " &cusing a(n) &6" + ((Player) e).getInventory().getItemInMainHand().getType().name().toLowerCase() + "&c!");
			case ZOMBIE:
				return (name + " Was ripped to bits by a Zombie!");
			case SKELETON:
				return (name + " Was shot in the face by a Skeleton");
			case ARROW:
				return (name + " Was shot in the face by a Skeleton");
			case SPIDER:
				return (name + " Was eaten alive by a Spider!");
			case DROWNED:
				return (name + " Was ripped apart by a Drowned!");
			case STRAY:
				return (name + " Was shot in the face by a Stray!");
			default:
				return (name + " Spontaneously died!");
		}
	}
}
