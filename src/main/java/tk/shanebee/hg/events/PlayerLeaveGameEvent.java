package tk.shanebee.hg.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.shanebee.hg.Game;

/**
 * Called when a player leaves a game
 */
public class PlayerLeaveGameEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game;
	private Player player;
	private boolean death;

	public PlayerLeaveGameEvent(Game game, Player player, boolean death) {
		this.game = game;
		this.player = player;
		this.death = death;
	}

	/** Get the game the player left
	 * @return The game the player left
	 */
	public Game getGame() {
		return this.game;
	}

	/** Get the player that left the game
	 * @return The player that left the game
	 */
	public Player getPlayer() {
		return this.player;
	}

	/** Check if the player died when they left the game
	 * @return If the player died when they left the game
	 */
	@SuppressWarnings("unused")
	public boolean getDied() {
		return death;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
