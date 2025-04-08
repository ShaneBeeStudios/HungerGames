package com.shanebeestudios.hg.plugin.configs;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
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
    public String winning_amount;
    public String chest_drop_1;
    public String chest_drop_2;
    public String compass_nearest_player;
    public String cmd_start_starting;

    // ARENA DEBUG
    public String arena_debug_need_more_spawns;
    public String arena_debug_min_max_players;
    public String arena_debug_broken_debug;
    public String arena_debug_broken_debug_2;
    public String arena_debug_invalid_lobby;
    public String arena_debug_set_lobby;
    public String arena_debug_ready_run;

    // COMMANDS
    // - Base
    public String command_base_not_in_valid_game;
    public String command_base_no_region;
    public String command_base_status;
    // - Create
    public String command_create_error_arguments;
    public String command_create_error_already_exists;
    public String command_create_error_session_exists;
    public String command_create_divisible_1;
    public String command_create_divisible_2;
    public String command_create_minmax;
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
    // - Delete
    public String command_delete_attempt;
    public String command_delete_kicking;
    public String command_delete_stopping;
    public String command_delete_deleted;
    public String command_delete_failed;
    public String command_delete_rollback;
    public String command_delete_no_exist;

    // - Edit
    //   - ChestRefill
    public String command_edit_chest_refill_time_set;
    public String command_edit_chest_refill_repeat_set;
    public String command_chest_refill_now;
    //   - LobbyWall
    public String command_edit_lobbywall_set;
    public String command_edit_lobbywall_incorrect;
    public String command_edit_lobbywall_format;

    // - Exit
    public String command_exit_set_global;
    public String command_exit_set_all;
    public String command_exit_set_arena;
    // - Join
    public String command_join_already_in_game;
    public String command_join_already_in_game_other;
    public String command_join_no_money;
    // - Kit
    public String command_kit_game_running;
    public String command_kit_invalid_name;
    public String command_kit_no_permission;
    // - List
    public String command_list_players;
    public String command_list_players_delimiter;
    // - Leave
    public String command_leave_left;
    public String command_leave_refund;
    // - Team
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
    // - Toggle
    public String command_toggle_locked;
    public String command_toggle_unlocked;
    public String command_toggle_running;

    // DEATH MESSAGES
    public Map<String, String> death_message_entity_types = new HashMap<>();
    public Map<String, String> death_message_damage_types = new TreeMap<>();
    public String death_messages_prefix;
    public String death_messages_other;

    // GAME
    public String game_waiting_join;
    public String game_waiting_players_to_start;
    public String game_join;
    public String game_countdown_started;
    public String game_countdown_timer;
    public String game_almost_over;
    public String game_ending_min_sec;
    public String game_ending_min;
    public String game_ending_sec;
    public String game_border_closing;
    public String game_chest_refill;
    public String game_running;
    public String game_full;
    public String game_player_joined_game;
    public String game_player_left_game;
    public String game_bossbar_title;
    public String game_player_won;
    public String game_arena_not_ready;
    public String game_arena_spectate;
    public String game_roam_game_started;
    public String game_roam_time;
    public String game_roam_finished;

    // KITS
    public String kits_join_header;
    public String kits_join_footer;
    public String kits_join_msg;
    public String kits_join_avail;
    public String kits_join_kits_command;
    public String kits_doesnt_exist;
    public String kits_give_default;
    // - Kits GUI
    public String kits_kits_gui_title;
    // - Kit GUI
    public String kits_kit_gui_title;
    public String kits_kit_gui_exit;
    public String kits_kit_gui_apply;
    public String kits_kit_gui_no_helmet;
    public String kits_kit_gui_no_chestplate;
    public String kits_kit_gui_no_leggings;
    public String kits_kit_gui_no_boots;
    public String kits_kit_gui_potion_effects;
    public String kits_kit_gui_potion_effect_lore;
    public String kits_kit_gui_potion_effect_none;

    // LEADERBOARD
    public String leaderboard_blank_space;
    public String leaderboard_combined_separator;
    public String leaderboard_missing_player;

    // LISTENERS
    public String listener_not_running;
    public String listener_no_edit_block;
    public String listener_no_interact;
    public String listener_sign_click_hand;
    public String listener_command_handler_no_command;
    public String listener_command_handler_playing;

    // LOBBY SIGN
    public String lobby_sign_cost;
    public String lobby_sign_1_1;
    public String lobby_sign_1_3;
    public String lobby_sign_2_1;
    public String lobby_sign_3_1;

    // SCOREBOARD
    public String scoreboard_sidebar_title;
    public String scoreboard_sidebar_arena;
    public String scoreboard_sidebar_players_alive;
    public String scoreboard_sidebar_players_alive_num;
    public String scoreboard_show_health_name;

    // SPECTATOR
    public String spectate_gui_title;
    public String spectate_compass_name;
    public List<String> spectate_compass_head_lore;
    public String spectate_start_title;

    // STATUS
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

    // TEAM
    public String team_invite_1;
    public String team_invite_2;
    public String team_invite_3;
    public String team_invite_4;
    public String team_prefix;
    public String team_suffix;
    public String team_joined;

    // TRACKING STICK
    public String tracking_stick_name;
    public List<String> tracking_stick_lore;
    public String tracking_stick_nearest;
    public String tracking_stick_no_near;
    public String tracking_stick_bar;
    public String tracking_stick_new1;
    public String tracking_stick_new2;


    public Language(HungerGames plugin) {
        this.plugin = plugin;
        loadLangFile();
    }

    private void loadLangFile() {
        if (this.customLangFile == null) {
            this.customLangFile = new File(this.plugin.getDataFolder(), "language.yml");
        }
        if (!this.customLangFile.exists()) {
            this.plugin.saveResource("language.yml", false);
            this.lang = YamlConfiguration.loadConfiguration(this.customLangFile);
            Util.log("New language.yml <green>created");
        } else {
            this.lang = YamlConfiguration.loadConfiguration(this.customLangFile);
        }
        matchConfig(this.lang, this.customLangFile);
        loadLang();
        Util.log("language.yml <green>successfully loaded");
    }

    // Used to update config
    @SuppressWarnings({"ConstantConditions", "CallToPrintStackTrace"})
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
        this.prefix = this.lang.getString("prefix");

        winning_amount = this.lang.getString("winning-amount");

        chest_drop_1 = this.lang.getString("chest-drop-1");
        chest_drop_2 = this.lang.getString("chest-drop-2");

        compass_nearest_player = this.lang.getString("compass-nearest-player");

        cmd_start_starting = this.lang.getString("cmd-start-starting");

        this.game_chest_refill = this.lang.getString("game-chests-refill");

        // ARENA DEBUG
        this.arena_debug_need_more_spawns = this.lang.getString("arena-debug.need-more-spawns");
        this.arena_debug_min_max_players = this.lang.getString("arena-debug.min-max-players");
        this.arena_debug_broken_debug = this.lang.getString("arena-debug.broken-debug");
        this.arena_debug_broken_debug_2 = this.lang.getString("arena-debug.broken-debug-2");
        this.arena_debug_invalid_lobby = this.lang.getString("arena-debug.invalid-lobby");
        this.arena_debug_set_lobby = this.lang.getString("arena-debug.set-lobby");
        this.arena_debug_ready_run = this.lang.getString("arena-debug.ready-run");

        // COMMANDS
        // - Base
        this.command_base_not_in_valid_game = this.lang.getString("command.base-not-in-valid-game");
        this.command_base_no_region = this.lang.getString("command.base-no-region");
        this.command_base_status = this.lang.getString("command.base-status");
        // - ChestRefill
        this.command_chest_refill_now = this.lang.getString("command.chest-refill-now");
        // - Create
        this.command_create_error_arguments = this.lang.getString("command.create-error-arguments");
        this.command_create_error_already_exists = this.lang.getString("command.create-error-already-exists");
        this.command_create_error_session_exists = this.lang.getString("command.create-error-session-exists");
        this.command_create_divisible_1 = this.lang.getString("command.create-divisible-1");
        this.command_create_divisible_2 = this.lang.getString("command.create-divisible-2");
        this.command_create_minmax = this.lang.getString("command.create-minmax");
        this.command_create_session_stick_name = this.lang.getString("command.create-session-stick-name");
        this.command_create_session_start = this.lang.getString("command.create-session-start");
        this.command_create_session_next_corner = this.lang.getString("command.create-session-next-corner");
        this.command_create_session_select_spawns = this.lang.getString("command.create-session-select-spawns");
        this.command_create_session_select_spawns_next = this.lang.getString("command.create-session-select-spawns-next");
        this.command_create_session_error_too_small = this.lang.getString("command.create-session-error-too-small");
        this.command_create_session_already_in_arena = this.lang.getString("command.create-session-already-in-arena");
        this.command_create_session_select_sign = this.lang.getString("command.create-session-select-sign");
        this.command_create_session_sign_invalid = this.lang.getString("command.create-session-sign-invalid");
        this.command_create_session_done = this.lang.getString("command.create-session-done");
        // - Delete
        this.command_delete_attempt = this.lang.getString("command.delete-attempt");
        this.command_delete_kicking = this.lang.getString("command.delete-kicking");
        this.command_delete_stopping = this.lang.getString("command.delete-stopping");
        this.command_delete_deleted = this.lang.getString("command.delete-deleted");
        this.command_delete_failed = this.lang.getString("command.delete-failed");
        this.command_delete_rollback = this.lang.getString("command.delete-rollback");
        this.command_delete_no_exist = this.lang.getString("command.delete-noexist");
        // - Edit
        //   - ChestRefill
        this.command_edit_chest_refill_time_set = this.lang.getString("command.edit.chest-refill-time-set");
        this.command_edit_chest_refill_repeat_set = this.lang.getString("command.edit.chest-refill-repeat-set");
        //   - LobbyWall
        this.command_edit_lobbywall_set = this.lang.getString("command.edit.lobbywall-set");
        this.command_edit_lobbywall_incorrect = this.lang.getString("command.edit.lobbywall-incorrect");
        this.command_edit_lobbywall_format = this.lang.getString("command.edit.lobbywall-format");

        // - Exit
        this.command_exit_set_global = this.lang.getString("command.exit-set-global");
        this.command_exit_set_all = this.lang.getString("command.exit-set-all");
        this.command_exit_set_arena = this.lang.getString("command.exit-set-arena");
        // - Join
        this.command_join_already_in_game = this.lang.getString("command.join-already-in-game");
        this.command_join_already_in_game_other = this.lang.getString("command.join-already-in-game-other");
        this.command_join_no_money = this.lang.getString("command.join-no-money");
        // - Kit
        this.command_kit_game_running = this.lang.getString("command.kit.game-running");
        this.command_kit_invalid_name = this.lang.getString("command.kit.invalid-name");
        this.command_kit_no_permission = this.lang.getString("command.kit.no-permission");
        // - List
        this.command_list_players = this.lang.getString("command.list-players");
        this.command_list_players_delimiter = this.lang.getString("command.list-players-delimiter");
        // - Leave
        this.command_leave_left = this.lang.getString("command.leave-left");
        this.command_leave_refund = this.lang.getString("command.leave-refund");
        // - Team
        this.command_team_player_not_available = this.lang.getString("command.team-player-not-available");
        this.command_team_only_leader = this.lang.getString("command.team-only-leader");
        this.command_team_on_team = this.lang.getString("command.team-on-team");
        this.command_team_max = this.lang.getString("command.team-max");
        this.command_team_invited = this.lang.getString("command.team-invited");
        this.command_team_wrong = this.lang.getString("command.team-wrong");
        this.command_team_no_pend = this.lang.getString("command.team-no-pending");
        this.command_team_joined = this.lang.getString("command.team-joined");
        this.command_team_deny = this.lang.getString("command.team-deny");
        this.command_team_no_team = this.lang.getString("command.team-no-team");
        this.command_team_not_on_team = this.lang.getString("command.team-not-on-team");
        this.command_team_tp = this.lang.getString("command.team-tp");
        this.command_team_self = this.lang.getString("command.team-self");
        this.command_team_created = this.lang.getString("command.team-created");
        this.command_team_already_exists = this.lang.getString("command.team-already-exists");
        this.command_team_already_have = this.lang.getString("command.team-already-have");
        this.command_team_none = this.lang.getString("command.team-none");
        // - Toggle
        this.command_toggle_unlocked = this.lang.getString("command.toggle-unlocked");
        this.command_toggle_locked = this.lang.getString("command.toggle-locked");
        this.command_toggle_running = this.lang.getString("command.toggle-running");

        // DEATH MESSAGES
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

        // GAME
        this.game_waiting_join = this.lang.getString("game.waiting-join");
        this.game_waiting_players_to_start = this.lang.getString("game.waiting-players-to-start");
        this.game_join = this.lang.getString("game.join");
        this.game_countdown_started = this.lang.getString("game.countdown-started");
        this.game_countdown_timer = this.lang.getString("game.countdown-timer");
        this.game_almost_over = this.lang.getString("game.almost-over");
        this.game_ending_min_sec = this.lang.getString("game.ending-min-sec");
        this.game_ending_min = this.lang.getString("game.ending-min");
        this.game_ending_sec = this.lang.getString("game.ending-sec");
        this.game_border_closing = this.lang.getString("game.border-closing");
        this.game_running = this.lang.getString("game.running");
        this.game_full = this.lang.getString("game.full");
        this.game_player_joined_game = this.lang.getString("game.player-joined-game");
        this.game_player_left_game = this.lang.getString("game.player-left-game");
        this.game_bossbar_title = this.lang.getString("game.bossbar-title");
        this.game_player_won = this.lang.getString("game.player-won");
        this.game_arena_not_ready = this.lang.getString("game.arena-not-ready");
        this.game_arena_spectate = this.lang.getString("game.arena-spectate");
        this.game_roam_game_started = this.lang.getString("game.roam-game-started");
        this.game_roam_time = this.lang.getString("game.roam-time");
        this.game_roam_finished = this.lang.getString("game.roam-finished");

        // KITS
        this.kits_join_header = this.lang.getString("kits.join-header");
        this.kits_join_footer = this.lang.getString("kits.join-footer");
        this.kits_join_msg = this.lang.getString("kits.join-msg");
        this.kits_join_avail = this.lang.getString("kits.join-available");
        this.kits_join_kits_command = this.lang.getString("kits.join-kits-command");
        this.kits_doesnt_exist = this.lang.getString("kits.doesnt-exist");
        this.kits_give_default = this.lang.getString("kits.give-default");
        this.kits_kits_gui_title = this.lang.getString("kits.kits-gui.title");
        this.kits_kit_gui_apply = this.lang.getString("kits.kit-gui.apply");
        this.kits_kit_gui_exit = this.lang.getString("kits.kit-gui.exit");
        this.kits_kit_gui_title = this.lang.getString("kits.kit-gui.title");
        this.kits_kit_gui_no_helmet = this.lang.getString("kits.kit-gui.helmet-none");
        this.kits_kit_gui_no_chestplate = this.lang.getString("kits.kit-gui.chestplate-none");
        this.kits_kit_gui_no_leggings = this.lang.getString("kits.kit-gui.leggings-none");
        this.kits_kit_gui_no_boots = this.lang.getString("kits.kit-gui.boots-none");
        this.kits_kit_gui_potion_effects = this.lang.getString("kits.kit-gui.potion-effects");
        this.kits_kit_gui_potion_effect_lore = this.lang.getString("kits.kit-gui.potion-effect-lore");
        this.kits_kit_gui_potion_effect_none = this.lang.getString("kits.kit-gui.potion-effect-none");

        // LEADERBOARD
        this.leaderboard_blank_space = this.lang.getString("leaderboard.blank-space");
        this.leaderboard_combined_separator = this.lang.getString("leaderboard.combined-separator");
        this.leaderboard_missing_player = this.lang.getString("leaderboard.missing-player");

        // LISTENER
        this.listener_not_running = this.lang.getString("listener.not-running");
        this.listener_no_edit_block = this.lang.getString("listener.no-edit-block");
        this.listener_no_interact = this.lang.getString("listener.no-interact");
        this.listener_sign_click_hand = this.lang.getString("listener.sign-click-hand");
        this.listener_command_handler_no_command = this.lang.getString("listener.command-handler-no-command");
        this.listener_command_handler_playing = this.lang.getString("listener.command-handler-playing");

        // LOBBY SIGN
        this.lobby_sign_1_1 = this.lang.getString("lobby-signs.sign-1.line-1");
        this.lobby_sign_1_3 = this.lang.getString("lobby-signs.sign-1.line-3");
        this.lobby_sign_cost = this.lang.getString("lobby-signs.sign-1.line-4");
        this.lobby_sign_2_1 = this.lang.getString("lobby-signs.sign-2.line-1");
        this.lobby_sign_3_1 = this.lang.getString("lobby-signs.sign-3.line-1");

        // SCOREBOARD
        this.scoreboard_sidebar_title = this.lang.getString("scoreboard.sidebar.title");
        this.scoreboard_sidebar_arena = this.lang.getString("scoreboard.sidebar.arena");
        this.scoreboard_sidebar_players_alive = this.lang.getString("scoreboard.sidebar.players-alive");
        this.scoreboard_sidebar_players_alive_num = this.lang.getString("scoreboard.sidebar.players-alive-num");
        this.scoreboard_show_health_name = this.lang.getString("scoreboard.show-health.name");

        // SPECTATOR
        this.spectate_gui_title = this.lang.getString("spectate.gui.title");
        this.spectate_compass_name = this.lang.getString("spectate.compass.name");
        this.spectate_compass_head_lore = this.lang.getStringList("spectate.compass.head-lore");
        this.spectate_start_title = this.lang.getString("spectate.start-title");

        // STATUS
        this.game_status_running = this.lang.getString("status.game-status.running");
        this.game_status_stopped = this.lang.getString("status.game-status.stopped");
        this.game_status_ready = this.lang.getString("status.game-status.ready");
        this.game_status_waiting = this.lang.getString("status.game-status.waiting");
        this.game_status_broken = this.lang.getString("status.game-status.broken");
        this.game_status_rollback = this.lang.getString("status.game-status.rollback");
        this.game_status_not_ready = this.lang.getString("status.game-status.not-ready");
        this.game_status_beginning = this.lang.getString("status.game-status.beginning");
        this.game_status_countdown = this.lang.getString("status.game-status.countdown");
        this.player_status_in_game = this.lang.getString("status.player-status.in-game");
        this.player_status_spectator = this.lang.getString("status.player-status.spectator");
        this.player_status_not_in_game = this.lang.getString("status.player-status.not-in-game");

        // TEAM
        this.team_invite_1 = this.lang.getString("team.invite-1");
        this.team_invite_2 = this.lang.getString("team.invite-2");
        this.team_invite_3 = this.lang.getString("team.invite-3");
        this.team_invite_4 = this.lang.getString("team.invite-4");
        this.team_prefix = this.lang.getString("team.prefix");
        this.team_suffix = this.lang.getString("team.suffix");
        this.team_joined = this.lang.getString("team.joined");

        // TRACKING STICK
        this.tracking_stick_name = this.lang.getString("tracking-stick.name");
        this.tracking_stick_lore = this.lang.getStringList("tracking-stick.lore");
        this.tracking_stick_nearest = this.lang.getString("tracking-stick.nearest");
        this.tracking_stick_no_near = this.lang.getString("tracking-stick.no-near");
        this.tracking_stick_bar = this.lang.getString("tracking-stick.bar");
        this.tracking_stick_new1 = this.lang.getString("tracking-stick.new1");
        this.tracking_stick_new2 = this.lang.getString("tracking-stick.new2");
    }

}
