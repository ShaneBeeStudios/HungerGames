package tk.shanebee.hg;


import org.bukkit.ChatColor;

public enum Status {

	/**
	 * Game is running
	 */
	RUNNING(ChatColor.GREEN  + "" + ChatColor.BOLD +  "Running"),
	/**
	 * Game has stopped
	 */
	STOPPED(ChatColor.DARK_RED  + "" + ChatColor.BOLD +  "Stopped"),
	/**
	 * Game is ready to run
	 */
	READY(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Ready"),
	/**
	 * Game is waiting
	 */
	WAITING(ChatColor.AQUA  + "" + ChatColor.BOLD +  "Waiting..."),
	/**
	 * Game is broken
	 */
	BROKEN(ChatColor.DARK_RED  + "" + ChatColor.BOLD +  "BROKEN"),
	/**
	 * Game is currently rolling back blocks
	 */
	ROLLBACK(ChatColor.RED  + "" + ChatColor.BOLD +  "Restoring..."),
	/**
	 * Game is not ready
	 */
	NOTREADY(ChatColor.DARK_BLUE  + "" + ChatColor.BOLD +  "NotReady"),
	/**
	 * Game is starting to run
	 */
	BEGINNING(ChatColor.GREEN  + "" + ChatColor.BOLD +  "Running"),
	/**
	 * Game is counting down to start
	 */
	COUNTDOWN(ChatColor.AQUA  + "" + ChatColor.BOLD +  "Starting...");

	private String name;

	Status(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
