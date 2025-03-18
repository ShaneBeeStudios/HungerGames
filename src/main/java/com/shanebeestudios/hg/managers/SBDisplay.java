package com.shanebeestudios.hg.managers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.util.Util;

/**
 * General scoreboard manager
 */
public class SBDisplay {

	private ScoreboardManager manager;
	private Scoreboard board;
	private Objective ob;
	private HashMap<String, Scoreboard> score = new HashMap<>();
	private final Game game;

	public SBDisplay(Game game) {
		this.manager = Bukkit.getScoreboardManager();
		this.board = manager.getNewScoreboard();
		this.ob = board.registerNewObjective(ChatColor.translateAlternateColorCodes('&', HungerGames.getPlugin().getLang().players_alive), "dummy", "arena" + game.getGameArenaData().getName());
		this.ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.ob.setDisplayName(ChatColor.translateAlternateColorCodes('&', HungerGames.getPlugin().getLang().scoreboard_title));
		this.game = game;
	}

	public void setAlive() {
		/*
		Score score = ob.getScore(ChatColor.translateAlternateColorCodes('&', HG.getPlugin().getLang().players_alive));
		Score arena = ob.getScore(ChatColor.translateAlternateColorCodes('&', HG.getPlugin().getLang().scoreboard_arena + g.getName()));

		score.setScore(g.getPlayers().size());
		arena.setScore(g.getPlayers().size() + 1);

		 */
		ob.unregister();
		this.ob = board.registerNewObjective(ChatColor.translateAlternateColorCodes('&', HungerGames.getPlugin().getLang().players_alive), "dummy", "arena" + game.getGameArenaData().getName());
		this.ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.ob.setDisplayName(ChatColor.translateAlternateColorCodes('&', HungerGames.getPlugin().getLang().scoreboard_title));
		String alive = "  " + HungerGames.getPlugin().getLang().players_alive_num.replace("<num>", String.valueOf(game.getGamePlayerData().getPlayers().size()));

		Score space1 = ob.getScore(" ");
		Score space2 = ob.getScore("  ");
		Score space3 = ob.getScore("   ");
		Score arena1 = ob.getScore(Util.getColString(HungerGames.getPlugin().getLang().scoreboard_arena));
		Score arena2 = ob.getScore(Util.getColString("  &e" + game.getGameArenaData().getName()));

		Score alive1 = ob.getScore(Util.getColString(HungerGames.getPlugin().getLang().players_alive));
		Score alive2 = ob.getScore(Util.getColString(alive));

		space1.setScore(6);
		arena1.setScore(5);
		arena2.setScore(4);
		space2.setScore(3);
		alive1.setScore(2);
		alive2.setScore(1);
		space3.setScore(0);


	}

	public void resetAlive() {
		board.resetScores(ChatColor.translateAlternateColorCodes('&', HungerGames.getPlugin().getLang().players_alive));
		score.clear();
	}

	public void setSB(Player p) {
		score.put(p.getName(), p.getScoreboard());
		p.setScoreboard(board);
	}

	public void restoreSB(Player p) {
		if (score.get(p.getName()) == null) {
			p.setScoreboard(manager.getNewScoreboard());
		} else {
			p.setScoreboard(score.get(p.getName()));
			score.remove(p.getName());
		}
	}
}
