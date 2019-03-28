package me.minebuilders.hg;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

class SBDisplay {

	private ScoreboardManager manager;
	private Scoreboard board;
	private Objective ob;
	private HashMap<String, Scoreboard> score = new HashMap<>();
	private Game g;

	SBDisplay(Game g) {
		this.manager = Bukkit.getScoreboardManager();
		this.board = manager.getNewScoreboard();
		this.ob = board.registerNewObjective(ChatColor.translateAlternateColorCodes('&', HG.lang.players_alive), "dummy", "arena" + g.getName());
		this.ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.ob.setDisplayName(ChatColor.translateAlternateColorCodes('&', HG.lang.scoreboard_title));
		this.g = g;
	}

	void setAlive() {
		Score score = ob.getScore(ChatColor.translateAlternateColorCodes('&', HG.lang.players_alive));
		
		score.setScore(g.getPlayers().size());
	}

	void resetAlive() {
		board.resetScores(ChatColor.translateAlternateColorCodes('&', HG.lang.players_alive));
		score.clear();
	}

	void setSB(Player p) {
		score.put(p.getName(), p.getScoreboard());
		p.setScoreboard(board);
	}

	void restoreSB(Player p) {
		if (score.get(p.getName()) == null) {
			p.setScoreboard(manager.getNewScoreboard());
		} else {
			p.setScoreboard(score.get(p.getName()));
			score.remove(p.getName());
		}
	}
}
