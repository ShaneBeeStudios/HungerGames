package tk.shanebee.hg;

import java.util.Random;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class ChestDrop implements Listener {

	private FallingBlock fb;
	private BlockState beforeBlock;
	private Player invopener;
	private Chunk c;

	public ChestDrop(FallingBlock fb) {
		this.fb = fb;
		this.c = fb.getLocation().getChunk();
		c.load();
		Bukkit.getPluginManager().registerEvents(this, HG.plugin);
	}

	@EventHandler
	public void onUnload(ChunkUnloadEvent event) {
		if (event.getChunk().equals(c)) {
			//event.setCancelled(true); I guess this was removed?!?
			event.getChunk().setForceLoaded(true); // Let's give this a try
		}
	}

	public void remove() {
		if (fb != null && !fb.isDead()) fb.remove();
		if (beforeBlock != null) {
			beforeBlock.update(true);
			Block b = beforeBlock.getBlock();
			if (b.getType() == Material.ENDER_CHEST) {
				b.setType(Material.AIR);
			}
		}

		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onEntityModifyBlock(EntityChangeBlockEvent event) {
		Entity en = event.getEntity();

		if (!(en instanceof FallingBlock)) return;

		FallingBlock fb2 = (FallingBlock) en;

		if (fb2.equals(fb)) {
			beforeBlock = event.getBlock().getState();

			Location l = beforeBlock.getLocation();
			Util.shootFirework(new Location(l.getWorld(), l.getX() + 0.5, l.getY(), l.getZ() + 0.5));
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		for (HumanEntity p : event.getViewers()) {
			if (p.equals(invopener)) {
				Location l = beforeBlock.getLocation();
				l.getWorld().createExplosion(l.getBlockX(), l.getBlockY(), l.getBlockZ(), 1, false, false);
				remove();
				return;
			}
		}
	}

	@EventHandler
	public void onOpenChestDrop(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && beforeBlock != null && event.getClickedBlock().getLocation().equals(beforeBlock.getLocation())) {
			Player p = event.getPlayer();
			Game game = HG.plugin.players.get(p.getUniqueId()).getGame();
			Random rg = new Random();
			invopener = p;

			Inventory i = Bukkit.getServer().createInventory(p, 54);
			i.clear();
			int c = rg.nextInt(Config.randomChestMaxContent) + 1;
			while (c != 0) {
				ItemStack it = HG.manager.randomItem(game,false);
				if (it != null) {
					i.addItem(it);
				}
				c--;
			}
			event.setCancelled(true);
			p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
			p.openInventory(i);
		}
	}

}
