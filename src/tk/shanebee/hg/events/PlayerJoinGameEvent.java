package tk.shanebee.hg.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.shanebee.hg.Game;

/**
 * Called when a player joins a game
 */
public class PlayerJoinGameEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game;
	private Player player;

	public PlayerJoinGameEvent(Game game, Player player) {
		this.game = game;
		this.player = player;
	}

	/** Get the player that joined a gam
	 * @return The player that joined the game
	 */
	public Player getPlayer() {
		return this.player;
	}

	/** Get the game the player joined
	 * @return The game the player joined
	 */
	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
