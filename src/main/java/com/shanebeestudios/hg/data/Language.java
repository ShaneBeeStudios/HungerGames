package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    // Tracking Stick
    public String tracking_stick_name;
    public List<String> tracking_stick_lore;
    public String tracking_stick_nearest;
    public String tracking_stick_no_near;
    public String tracking_stick_bar;
    public String tracking_stick_new1;
    public String tracking_stick_new2;

    // ARENA DEBUG
    public String arena_debug_need_more_spawns;
    public String arena_debug_min_max_players;
    public String arena_debug_broken_debug;
    public String arena_debug_broken_debug_2;
    public String arena_debug_invalid_lobby;
    public String arena_debug_set_lobby;
    public String arena_debug_ready_run;

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

    // Edit
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

    // Death Messages
    public Map<String, String> death_message_entity_types = new HashMap<>();
    public Map<String, String> death_message_damage_types = new TreeMap<>();
    public String death_messages_prefix;
    public String death_messages_other;

    // Scoreboard
    public String scoreboard_sidebar_title;
    public String scoreboard_sidebar_arena;
    public String scoreboard_sidebar_players_alive;
    public String scoreboard_sidebar_players_alive_num;
    public String scoreboard_show_health_name;

    // Status
    public String game_status_running;
    public String game_status_stopped;
    public String game_status_ready;
    public String game_status_waiting;
    public String game_status_broken;
    public String game_status_rollback;
    public String game_status_not_ready;
    public String game_status_beginning;
    public String game_status_countdown;
    public String player_status_in_game;
    public String player_status_spectator;
    public String player_status_not_in_game;


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
                if (!defConfig.contains(key) && !key.contains("death-messages")) {
                    Util.log("Deleting: " + key);
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

        // ARENA DEBUG
        arena_debug_need_more_spawns = lang.getString("arena-debug.need-more-spawns");
        arena_debug_min_max_players = lang.getString("arena-debug.min-max-players");
        arena_debug_broken_debug = lang.getString("arena-debug.broken-debug");
        arena_debug_broken_debug_2 = lang.getString("arena-debug.broken-debug-2");
        arena_debug_invalid_lobby = lang.getString("arena-debug.invalid-lobby");
        arena_debug_set_lobby = lang.getString("arena-debug.set-lobby");
        arena_debug_ready_run = lang.getString("arena-debug.ready-run");

        // Tracking Stick
        this.tracking_stick_name = this.lang.getString("tracking-stick.name");
        this.tracking_stick_lore = this.lang.getStringList("tracking-stick.lore");
        this.tracking_stick_nearest = this.lang.getString("tracking-stick.nearest");
        this.tracking_stick_no_near = this.lang.getString("tracking-stick.no-near");
        this.tracking_stick_bar = this.lang.getString("tracking-stick.bar");
        this.tracking_stick_new1 = this.lang.getString("tracking-stick.new1");
        this.tracking_stick_new2 = this.lang.getString("tracking-stick.new2");

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

        // Death Messages
        ConfigurationSection entityTypeSection = this.lang.getConfigurationSection("death-messages.entity-types");
        if (entityTypeSection != null) {
            entityTypeSection.getKeys(false).forEach(key -> {
                String string = entityTypeSection.getString(key);
                this.death_message_entity_types.put(key, string);
            });
        }
        ConfigurationSection damageTypeSection = this.lang.getConfigurationSection("death-messages.damage-types");
        if (damageTypeSection != null) {
            damageTypeSection.getKeys(false).forEach(key -> {
                String string = damageTypeSection.getString(key);
                this.death_message_damage_types.put(key, string);
            });
        }
        this.death_messages_prefix = this.lang.getString("death-messages.prefix");
        this.death_messages_other = this.lang.getString("death-messages.other");

        // Scoreboard
        this.scoreboard_sidebar_title = this.lang.getString("scoreboard.sidebar.title");
        this.scoreboard_sidebar_arena = this.lang.getString("scoreboard.sidebar.arena");
        this.scoreboard_sidebar_players_alive = this.lang.getString("scoreboard.sidebar.players-alive");
        this.scoreboard_sidebar_players_alive_num = this.lang.getString("scoreboard.sidebar.players-alive-num");
        this.scoreboard_show_health_name = this.lang.getString("scoreboard.show-health.name");

        // Status
        this.game_status_running = this.lang.getString("game-status.running");
        this.game_status_stopped = this.lang.getString("game-status.stopped");
        this.game_status_ready = this.lang.getString("game-status.ready");
        this.game_status_waiting = this.lang.getString("game-status.waiting");
        this.game_status_broken = this.lang.getString("game-status.broken");
        this.game_status_rollback = this.lang.getString("game-status.rollback");
        this.game_status_not_ready = this.lang.getString("game-status.not-ready");
        this.game_status_beginning = this.lang.getString("game-status.beginning");
        this.game_status_countdown = this.lang.getString("game-status.countdown");
        this.player_status_in_game = this.lang.getString("player-status.in-game");
        this.player_status_spectator = this.lang.getString("player-status.spectator");
        this.player_status_not_in_game = this.lang.getString("player-status.not-in-game");
    }

}
