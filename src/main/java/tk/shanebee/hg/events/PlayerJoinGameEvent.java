package tk.shanebee.hg.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.shanebee.hg.game.Game;

/**
 * Called when a player joins a game
 */
public class PlayerJoinGameEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Game game;
	private Player player;
	private boolean isCancelled;

	public PlayerJoinGameEvent(Game game, Player player) {
		this.game = game;
		this.player = player;
		this.isCancelled = false;

	}

	/** Get the player that joined a game
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

	@SuppressWarnings("NullableProblems")
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

}
