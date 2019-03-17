package me.minebuilders.hg.events;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.minebuilders.hg.Game;

public class ChestOpenEvent extends Event {

	private Game g;
	private Block b;
	
	public ChestOpenEvent(Game g, Block b) {
		this.g = g;
		this.b = b;
	}
	
	public Game getGame() {
		return g;
	}
	
	public Block getChest() {
		return b;
	}
	
	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}
