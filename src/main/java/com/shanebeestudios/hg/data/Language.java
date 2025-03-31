package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Language handler for plugin messages
 */
public class Language {

    private FileConfiguration lang = null;
    private File customLangFile = null;
    private final HungerGames plugin;

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
    public String game_border_closing;
    public String game_chest_refill;
    public String game_running;
    public String players_to_start;
    public String arena_not_ready;
    public String arena_spectate;
    public String game_full;
    public String player_won;
    public String winning_amount;
    public String kit_join_header;
    public String kit_join_footer;
    public String kit_join_msg;
    public String kit_join_avail;
    public String kit_no_perm;
    public String kit_doesnt_exist;
    public String kit_disabled;
    public String players_alive;
    public String players_alive_num;
    public String scoreboard_title;
    public String scoreboard_arena;
    public String team_invite_1;
    public String team_invite_2;
    public String team_invite_3;
    public String team_invite_4;
    public String team_prefix;
    public String team_suffix;
    public String team_joined;
    public String chest_drop_1;
    public String chest_drop_2;
    public String compass_nearest_player;
    public String roam_game_started;
    public String roam_time;
    public String roam_finished;
    public String status_running;
    public String status_stopped;
    public String status_ready;
    public String status_waiting;
    public String status_broken;
    public String status_rollback;
    public String status_not_ready;
    public String status_beginning;
    public String status_countdown;

    public String death_fallen;
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
    public String death_trident;
    public String death_stray;
    public String death_other_entity;
    public String cmd_spawn_same;
    public String cmd_spawn_set;
    public String cmd_kit_no_change;
    public String cmd_reload_attempt;
    public String cmd_reload_reloaded_arena;
    public String cmd_reload_reloaded_kit;
    public String cmd_reload_reloaded_items;
    public String cmd_reload_reloaded_config;
    public String cmd_reload_reloaded_success;
    public String cmd_start_starting;
    public String cmd_stop_all;
    public String cmd_stop_arena;
    public String cmd_stop_noexist;
    public String cmd_handler_nokit;
    public String cmd_handler_nocmd;
    public String cmd_handler_playing;
    public String cmd_border_size;
    public String cmd_border_center;
    public String cmd_border_timer;
    public String listener_not_running;
    public String listener_no_edit_block;
    public String listener_no_interact;
    public String listener_wand_create_arena;
    public String listener_wand_set_pos_2;
    public String listener_wand_big_enough;
    public String track_nearest;
    public String track_no_near;
    public String track_empty;
    public String track_bar;
    public String track_new1;
    public String track_new2;
    public String listener_sign_click_hand;
    public String bossbar;
    public String lb_blank_space;
    public String lb_combined_separator;
    public String lb_missing_player;
    public String lobby_sign_cost;
    public String lobby_sign_1_1;
    public String lobby_sign_1_3;
    public String lobby_sign_2_1;
    public String lobby_sign_3_1;
    public String spectator_compass;
    public String spectator_compass_head_lore;
    public String spectator_start_title;
    public String check_need_more_spawns;
    public String check_broken_debug;
    public String check_broken_debug_2;
    public String check_invalid_lobby;
    public String check_set_lobby;
    public String check_ready_run;

    // Tracking Stick
    public String tracking_stick_name;
    public String tracking_stick_lore;

    // COMMANDS
    // Base
    public String command_base_not_in_valid_game;
    public String command_base_no_region;
    public String command_base_status;
    // Create
    public String command_create_error_arguments;
    public String command_create_error_already_exists;
    public String command_create_error_session_exists;
    public String command_create_divisible_1;
    public String command_create_divisible_2;
    public String command_create_minmax;
    public String command_create_add_spawn;
    public String command_create_created;
    public String command_create_session_stick_name;
    public String command_create_session_start;
    public String command_create_session_next_corner;
    public String command_create_session_select_spawns;
    public String command_create_session_select_spawns_next;
    public String command_create_session_error_too_small;
    public String command_create_session_already_in_arena;
    public String command_create_session_select_sign;
    public String command_create_session_done;
    public String command_create_session_sign_invalid;
    // Delete
    public String command_delete_attempt;
    public String command_delete_kicking;
    public String command_delete_stopping;
    public String command_delete_deleted;
    public String command_delete_failed;
    public String command_delete_rollback;
    public String command_delete_noexist;

    // EDIT
    // - ChestRefill
    public String command_edit_chest_refill_time_set;
    public String command_edit_chest_refill_repeat_set;
    public String command_chest_refill_now;
    // - LobbyWall
    public String command_edit_lobbywall_set;
    public String command_edit_lobbywall_incorrect;
    public String command_edit_lobbywall_format;

    // Exit
    public String command_exit_set_global;
    public String command_exit_set_all;
    public String command_exit_set_arena;
    // Join
    public String command_join_already_in_game;
    public String command_join_already_in_game_other;
    public String command_join_no_money;
    // List
    public String command_list_players;
    public String command_list_players_delimiter;
    // Leave
    public String command_leave_left;
    public String command_leave_refund;
    // Team
    public String command_team_player_not_available;
    public String command_team_only_leader;
    public String command_team_on_team;
    public String command_team_max;
    public String command_team_invited;
    public String command_team_wrong;
    public String command_team_no_pend;
    public String command_team_joined;
    public String command_team_deny;
    public String command_team_no_team;
    public String command_team_not_on_team;
    public String command_team_tp;
    public String command_team_self;
    public String command_team_created;
    public String command_team_already_exists;
    public String command_team_already_have;
    public String command_team_none;
    // Toggle
    public String command_toggle_locked;
    public String command_toggle_unlocked;
    public String command_toggle_running;


    public Language(HungerGames plugin) {
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
            Util.log("New language.yml <green>created");
        } else {
            lang = YamlConfiguration.loadConfiguration(customLangFile);
        }
        matchConfig(lang, customLangFile);
        loadLang();
        Util.log("language.yml <green>successfully loaded");
    }

    // Used to update config
    @SuppressWarnings("ConstantConditions")
    private void matchConfig(FileConfiguration config, File file) {
        try {
            boolean hasUpdated = false;
            InputStream test = plugin.getResource(file.getName());
            assert test != null;
            InputStreamReader is = new InputStreamReader(test);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(is);
            for (String key : defConfig.getConfigurationSection("").getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defConfig.get(key));
                    hasUpdated = true;
                }
            }
            for (String key : config.getConfigurationSection("").getKeys(true)) {
                if (!defConfig.contains(key)) {
                    config.set(key, null);
                    hasUpdated = true;
                }
            }
            if (hasUpdated)
                config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        game_border_closing = lang.getString("game-border-closing");
        game_running = lang.getString("game-running");

        players_to_start = lang.getString("players-to-start");
        arena_not_ready = lang.getString("arena-not-ready");
        arena_spectate = lang.getString("arena-spectate");
        game_full = lang.getString("game-full");
        player_won = lang.getString("player-won");

        kit_join_header = lang.getString("kit-join-header");
        kit_join_footer = lang.getString("kit-join-footer");
        kit_join_msg = lang.getString("kit-join-msg");
        kit_join_avail = lang.getString("kit-join-available");
        kit_no_perm = lang.getString("kit-no-perm");
        kit_doesnt_exist = lang.getString("kit-doesnt-exist");
        kit_disabled = lang.getString("kit-disabled");
        winning_amount = lang.getString("winning-amount");

        scoreboard_title = lang.getString("scoreboard-title");
        scoreboard_arena = lang.getString("scoreboard-arena");
        players_alive = lang.getString("players-alive");
        players_alive_num = lang.getString("players-alive-num");
        team_invite_1 = lang.getString("team.invite-1");
        team_invite_2 = lang.getString("team.invite-2");
        team_invite_3 = lang.getString("team.invite-3");
        team_invite_4 = lang.getString("team.invite-4");
        team_prefix = lang.getString("team.prefix");
        team_suffix = lang.getString("team.suffix");
        team_joined = lang.getString("team.joined");

        chest_drop_1 = lang.getString("chest-drop-1");
        chest_drop_2 = lang.getString("chest-drop-2");

        compass_nearest_player = lang.getString("compass-nearest-player");

        roam_game_started = lang.getString("roam-game-started");
        roam_time = lang.getString("roam-time");
        roam_finished = lang.getString("roam-finished");

        death_fallen = lang.getString("death-fallen");
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
        death_trident = lang.getString("death-trident");
        death_other_entity = lang.getString("death-other-entity");

        cmd_spawn_same = lang.getString("cmd-spawn-same");
        cmd_spawn_set = lang.getString("cmd-spawn-set");
        cmd_kit_no_change = lang.getString("cmd-kit-no-change");
        cmd_reload_attempt = lang.getString("cmd-reload-attempt");
        cmd_reload_reloaded_arena = lang.getString("cmd-reload-reloaded-arena");
        cmd_reload_reloaded_config = lang.getString("cmd-reload-reloaded-config");
        cmd_reload_reloaded_items = lang.getString("cmd-reload-reloaded-items");
        cmd_reload_reloaded_kit = lang.getString("cmd-reload-reloaded-kit");
        cmd_reload_reloaded_success = lang.getString("cmd-reload-reloaded-success");
        cmd_start_starting = lang.getString("cmd-start-starting");
        cmd_stop_all = lang.getString("cmd-stop-all");
        cmd_stop_arena = lang.getString("cmd-stop-arena");
        cmd_stop_noexist = lang.getString("cmd-stop-noexist");
        cmd_handler_nokit = lang.getString("cmd-handler-nokit");
        cmd_handler_nocmd = lang.getString("cmd-handler-nocmd");
        cmd_handler_playing = lang.getString("cmd-handler-playing");
        listener_not_running = lang.getString("listener-not-running");
        listener_no_edit_block = lang.getString("listener-no-edit-block");
        listener_no_interact = lang.getString("listener-no-interact");
        track_nearest = lang.getString("track-nearest");
        track_no_near = lang.getString("track-no-near");
        track_empty = lang.getString("track-empty");
        track_bar = lang.getString("track-bar");
        track_new1 = lang.getString("track-new1");
        track_new2 = lang.getString("track-new2");
        listener_sign_click_hand = lang.getString("listener-sign-click-hand");
        listener_wand_create_arena = lang.getString("listener-wand-create-arena");
        listener_wand_set_pos_2 = lang.getString("listener-wand-set-pos-2");
        listener_wand_big_enough = lang.getString("listener-wand-big-enough");
        bossbar = lang.getString("game-bossbar");

        game_chest_refill = lang.getString("game-chests-refill");
        cmd_border_center = lang.getString("cmd-border-center");
        cmd_border_size = lang.getString("cmd-border-size");
        cmd_border_timer = lang.getString("cmd-border-timer");

        lb_blank_space = lang.getString("lb-blank-space");
        lb_combined_separator = lang.getString("lb-combined-separator");
        lb_missing_player = lang.getString("lb-missing-player");

        lobby_sign_1_1 = lang.getString("lobby-signs.sign-1.line-1");
        lobby_sign_1_3 = lang.getString("lobby-signs.sign-1.line-3");
        lobby_sign_cost = lang.getString("lobby-signs.sign-1.line-4");
        lobby_sign_2_1 = lang.getString("lobby-signs.sign-2.line-1");
        lobby_sign_3_1 = lang.getString("lobby-signs.sign-3.line-1");

        spectator_compass = lang.getString("spectator-compass");
        spectator_compass_head_lore = lang.getString("spectator-head-lore");
        spectator_start_title = lang.getString("spectator-start-title");

        status_running = lang.getString("status-running");
        status_stopped = lang.getString("status-stopped");
        status_ready = lang.getString("status-ready");
        status_waiting = lang.getString("status-waiting");
        status_broken = lang.getString("status-broken");
        status_rollback = lang.getString("status-rollback");
        status_not_ready = lang.getString("status-notready");
        status_beginning = lang.getString("status-beginning");
        status_countdown = lang.getString("status-countdown");

        check_need_more_spawns = lang.getString("check-need-more-spawns");
        check_broken_debug = lang.getString("check-broken-debug");
        check_broken_debug_2 = lang.getString("check-broken-debug-2");
        check_invalid_lobby = lang.getString("check-invalid-lobby");
        check_set_lobby = lang.getString("check-set-lobby");
        check_ready_run = lang.getString("check-ready-run");

        // Tracking Stick
        tracking_stick_name = lang.getString("tracking-stick.name");
        tracking_stick_lore = lang.getString("tracking-stick.lore");

        // COMMANDS
        // Base
        command_base_not_in_valid_game = lang.getString("command.base-not-in-valid-game");
        command_base_no_region = lang.getString("command.base-no-region");
        command_base_status = lang.getString("command.base-status");
        // ChestRefill
        command_chest_refill_now = lang.getString("command.chest-refill-now");
        // Create
        command_create_error_arguments = lang.getString("command.create-error-arguments");
        command_create_error_already_exists = lang.getString("command.create-error-already-exists");
        command_create_error_session_exists = lang.getString("command.create-error-session-exists");
        command_create_divisible_1 = lang.getString("command.create-divisible-1");
        command_create_divisible_2 = lang.getString("command.create-divisible-2");
        command_create_minmax = lang.getString("command.create-minmax");
        command_create_created = lang.getString("command.create-created");
        command_create_add_spawn = lang.getString("command.create-add-spawns");
        command_create_session_stick_name = lang.getString("command.create-session-stick-name");
        command_create_session_start = lang.getString("command.create-session-start");
        command_create_session_next_corner = lang.getString("command.create-session-next-corner");
        command_create_session_select_spawns = lang.getString("command.create-session-select-spawns");
        command_create_session_select_spawns_next = lang.getString("command.create-session-select-spawns-next");
        command_create_session_error_too_small = lang.getString("command.create-session-error-too-small");
        command_create_session_already_in_arena = lang.getString("command.create-session-already-in-arena");
        command_create_session_select_sign = lang.getString("command.create-session-select-sign");
        command_create_session_sign_invalid = lang.getString("command.create-session-sign-invalid");
        command_create_session_done = lang.getString("command.create-session-done");
        // Delete
        command_delete_attempt = lang.getString("command.delete-attempt");
        command_delete_kicking = lang.getString("command.delete-kicking");
        command_delete_stopping = lang.getString("command.delete-stopping");
        command_delete_deleted = lang.getString("command.delete-deleted");
        command_delete_failed = lang.getString("command.delete-failed");
        command_delete_rollback = lang.getString("command.delete-rollback");
        command_delete_noexist = lang.getString("command.delete-noexist");
        // Edit
        // - ChestRefill
        command_edit_chest_refill_time_set = lang.getString("command.edit.chest-refill-time-set");
        command_edit_chest_refill_repeat_set = lang.getString("command.edit.chest-refill-repeat-set");
        // - LobbyWall
        command_edit_lobbywall_set = lang.getString("command.edit.lobbywall-set");
        command_edit_lobbywall_incorrect = lang.getString("command.edit.lobbywall-incorrect");
        command_edit_lobbywall_format = lang.getString("command.edit.lobbywall-format");

        // Exit
        command_exit_set_global = lang.getString("command.exit-set-global");
        command_exit_set_all = lang.getString("command.exit-set-all");
        command_exit_set_arena = lang.getString("command.exit-set-arena");
        // Join
        command_join_already_in_game = lang.getString("command.join-already-in-game");
        command_join_already_in_game_other = lang.getString("command.join-already-in-game-other");
        command_join_no_money = lang.getString("command.join-no-money");
        // List
        command_list_players = lang.getString("command.list-players");
        command_list_players_delimiter = lang.getString("command.list-players-delimiter");
        // Leave
        command_leave_left = lang.getString("command.leave-left");
        command_leave_refund = lang.getString("command.leave-refund");
        // Team
        command_team_player_not_available = lang.getString("command.team-player-not-available");
        command_team_only_leader = lang.getString("command.team-only-leader");
        command_team_on_team = lang.getString("command.team-on-team");
        command_team_max = lang.getString("command.team-max");
        command_team_invited = lang.getString("command.team-invited");
        command_team_wrong = lang.getString("command.team-wrong");
        command_team_no_pend = lang.getString("command.team-no-pending");
        command_team_joined = lang.getString("command.team-joined");
        command_team_deny = lang.getString("command.team-deny");
        command_team_no_team = lang.getString("command.team-no-team");
        command_team_not_on_team = lang.getString("command.team-not-on-team");
        command_team_tp = lang.getString("command.team-tp");
        command_team_self = lang.getString("command.team-self");
        command_team_created = lang.getString("command.team-created");
        command_team_already_exists = lang.getString("command.team-already-exists");
        command_team_already_have = lang.getString("command.team-already-have");
        command_team_none = lang.getString("command.team-none");
        // Toggle
        command_toggle_unlocked = lang.getString("command.toggle-unlocked");
        command_toggle_locked = lang.getString("command.toggle-locked");
        command_toggle_running = lang.getString("command.toggle-running");
    }

}
