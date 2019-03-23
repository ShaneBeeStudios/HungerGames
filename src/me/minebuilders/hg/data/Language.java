package me.minebuilders.hg.data;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Language {

    private FileConfiguration lang = null;
    private File customLangFile = null;
    private final HG plugin;

    public String prefix;
    public String player_joined_game;
    public String player_left_game;
    public String game_started;
    public String game_join;
    public String game_countdown;
    public String game_almost_over;
    public String game_ending_minsec;
    public String game_ending_min;
    public String game_ending_sec;
    public String players_to_start;
    public String arena_not_ready;
    public String game_full;
    public String player_won;
    public String winning_amount;
    public String kit_join_header;
    public String kit_join_footer;
    public String kit_join_msg;
    public String kit_join_avail;
    public String kit_no_perm;
    public String kit_doesnt_exist;
    public String players_alive;
    public String scoreboard_title;
    public String team_invite_1;
    public String team_invite_2;
    public String team_invite_3;
    public String team_invite_4;
    public String joined_team;
    public String chest_drop_1;
    public String chest_drop_2;
    public String compass_nearest_player;
    public String roam_game_started;
    public String roam_time;
    public String roam_finished;
    public String death_explosion;
    public String death_custom;
    public String death_fall;
    public String death_falling_block;
    public String death_fire;
    public String death_projectile;
    public String death_lava;
    public String death_magic;
    public String death_suicide;
    public String death_other_cause;
    public String death_player;
    public String death_zombie;
    public String death_skeleton;
    public String death_spider;
    public String death_drowned;
    public String death_stray;
    public String death_other_entity;



    public Language(HG plugin) {
        this.plugin = plugin;
        loadLangFile();
    }

    private void loadLangFile() {
        if (customLangFile == null) {
            customLangFile = new File(plugin.getDataFolder(), "language.yml");
        }
        if (!customLangFile.exists()) {
            plugin.saveResource("language.yml", false);
            lang = YamlConfiguration.loadConfiguration(customLangFile);
            loadLang();
            Util.log("&7New language.yml created");
        } else {
            lang = YamlConfiguration.loadConfiguration(customLangFile);
            loadLang();
        }
        Util.log("&7language.yml loaded");
    }

    private void loadLang() {
        prefix = lang.getString("prefix");
        player_joined_game = lang.getString("player-joined-game");
        player_left_game = lang.getString("player-left-game");
        game_started = lang.getString("game-started");
        game_join = lang.getString("game-join");
        game_countdown = lang.getString("game-countdown");
        game_almost_over = lang.getString("game-almost-over");
        game_ending_minsec = lang.getString("game-ending-minsec");
        game_ending_min = lang.getString("game-ending-min");
        game_ending_sec = lang.getString("game-ending-sec");

        players_to_start = lang.getString("players-to-start");
        arena_not_ready = lang.getString("arena-not-ready");
        game_full = lang.getString("game-full");
        player_won = lang.getString("player-won");

        kit_join_header = lang.getString("kit-join-header");
        kit_join_footer = lang.getString("kit-join-footer");
        kit_join_msg = lang.getString("kit-join-msg");
        kit_join_avail = lang.getString("kit-join-available");
        kit_no_perm = lang.getString("kit-no-perm");
        kit_doesnt_exist = lang.getString("kit-doesnt-exist");
        winning_amount = lang.getString("winning-amount");

        scoreboard_title = lang.getString("scoreboard-title");
        players_alive = lang.getString("players-alive");
        team_invite_1 = lang.getString("team-invite-1");
        team_invite_2 = lang.getString("team-invite-2");
        team_invite_3 = lang.getString("team-invite-3");
        team_invite_4 = lang.getString("team-invite-4");
        joined_team = lang.getString("joined-team");

        chest_drop_1 = lang.getString("chest-drop-1");
        chest_drop_2 = lang.getString("chest-drop-2");

        compass_nearest_player = lang.getString("compass-nearest-player");

        roam_game_started = lang.getString("roam-game-started");
        roam_time = lang.getString("roam-time");
        roam_finished = lang.getString("roam-finished");

        death_explosion = lang.getString("death-explosion");
        death_custom = lang.getString("death-custom");
        death_fall = lang.getString("death-fall");
        death_falling_block = lang.getString("death-falling-block");
        death_fire = lang.getString("death-fire");
        death_projectile = lang.getString("death-projectile");
        death_lava = lang.getString("death-lava");
        death_magic = lang.getString("death-magic");
        death_suicide = lang.getString("death-suicide");
        death_other_cause = lang.getString("death-other-cause");
        death_player = lang.getString("death-player");
        death_zombie = lang.getString("death-zombie");
        death_skeleton = lang.getString("death-skeleton");
        death_spider = lang.getString("death-spider");
        death_stray = lang.getString("death-stray");
        death_drowned = lang.getString("death-drowned");
        death_other_entity = lang.getString("death-other-entity");


    }

}
