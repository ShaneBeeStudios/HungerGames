package tk.shanebee.hg;


import org.bukkit.ChatColor;

public enum Status {

	RUNNING(ChatColor.GREEN  + "" + ChatColor.BOLD +  "Running"),
	STOPPED(ChatColor.DARK_RED  + "" + ChatColor.BOLD +  "Stopped"),
	READY(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Ready"),
	WAITING(ChatColor.AQUA  + "" + ChatColor.BOLD +  "Waiting..."),
	BROKEN(ChatColor.DARK_RED  + "" + ChatColor.BOLD +  "BROKEN"),
	ROLLBACK(ChatColor.RED  + "" + ChatColor.BOLD +  "Restoring..."),
	NOTREADY(ChatColor.DARK_BLUE  + "" + ChatColor.BOLD +  "NotReady"),
	BEGINNING(ChatColor.GREEN  + "" + ChatColor.BOLD +  "Running"),
	COUNTDOWN(ChatColor.AQUA  + "" + ChatColor.BOLD +  "Starting...");

	private String name;

	Status(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
