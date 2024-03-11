package com.jeddlupoy.timelimit;

import com.jeddlupoy.timelimit.Listeners.*;
import com.jeddlupoy.timelimit.Utility.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TimeLimit extends JavaPlugin {
    public static final String TL_COMMAND_PERMISSION = "timelimit.admin";

    GameManager gm;
    public ConfigManager config;
    SendMessage messageUtility;

    String errorMessagePrefix = "&7[&4✖&7]&r ";
    String successMessagePrefix = "&7[&a✔&7]&r ";
    String gameMessagePrefix = "&7[&6▷&7]&r ";

    @Override
    public void onEnable() {
        loadConfigManager();
        gm = new GameManager(this);
        messageUtility = new SendMessage();

        messageUtility.sendConsoleMessage("&7> &aTimeLimit has been Enabled!");
        Bukkit.getPluginManager().registerEvents(new LeaveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MoveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerBlockBreakEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerBlockPlaceEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerKilledEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new RespawnEvent(this), this);
    }

    @Override
    public void onDisable() {
        messageUtility.sendConsoleMessage("&7> &aTimeLimit has been Disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String exactLabel, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (!cmd.getLabel().equalsIgnoreCase("tl")) return true;
        if (!sender.hasPermission(TL_COMMAND_PERMISSION)) {
            messageUtility.sendMessage(sender, errorMessagePrefix + "&cYou do not have permission to manage TimeLimit games!");
            return true;
        }
        if (args.length < 1) {
            showHelpPage(((Player) sender).getPlayer());
            return true;
        }

        Player player = (Player) sender;
        String subCommand = args[0].trim();

        if (subCommand.equalsIgnoreCase("start")) {
            if (config.getSpawnCount() < GameManager.MAX_PLAYERS) {
                messageUtility.sendMessage(player, errorMessagePrefix + "&cYou have not set up all the player spawn points! Finish setting up all spawn points and try again!");
                return true;
            }
            if (!config.doesLobbyExist()) {
                messageUtility.sendMessage(player, errorMessagePrefix + "&cYou have not set up the lobby spawn location! Set the lobby location and try again!");
                return true;
            }
            if (!config.doesGoldBlockSpawnAreasExist()) {
                messageUtility.sendMessage(player, errorMessagePrefix + "&cYou have not set a gold block spawn area! Finish setting up all spawn areas and try again!");
                return true;
            }
            messageUtility.sendMessage(player, gameMessagePrefix + "&eGame starting...");
            gm.startGame();
            return true;
        }
        if (subCommand.equalsIgnoreCase("stop")) {
            if (!GameManager.gameInProgress) {
                messageUtility.sendMessage(player, errorMessagePrefix + "&cYou cannot stop a game that has not started!");
                return true;
            }
            messageUtility.sendMessage(player, gameMessagePrefix + "&eGame stopped!");
            gm.stopGame();
            return true;
        }
        if (subCommand.equalsIgnoreCase("pause")) {
            if (!GameManager.gameInProgress) {
                messageUtility.sendMessage(player, errorMessagePrefix + "&cYou cannot pause a game that has not started!");
                return true;
            }
            messageUtility.sendMessage(player, gameMessagePrefix + "&eGame " + (GameManager.gamePaused ? "resumed" : "paused") + "!");
            gm.togglePauseGame();
            return true;
        }
        if (subCommand.equalsIgnoreCase("set")) {
            String setSubCommand = args[1].trim();

            if (setSubCommand.equalsIgnoreCase("lobby")) {
                config.setLobby(player);
                messageUtility.sendMessage(player, successMessagePrefix + "&2Game lobby set!");
                return true;
            }
            if (setSubCommand.equalsIgnoreCase("spawn")) {
                int spawnNumber;
                try {
                    spawnNumber = Integer.parseInt(args[2]);
                    if (!(spawnNumber > 0 && spawnNumber <= GameManager.MAX_PLAYERS)) {
                        messageUtility.sendMessage(player, errorMessagePrefix + "&cYou can only set spawn points for player numbers between 1 and " + GameManager.MAX_PLAYERS + "!");
                        return true;
                    }
                    config.setSpawn(player, spawnNumber);
                    messageUtility.sendMessage(player, successMessagePrefix + "&2Game spawn set!");
                } catch (NumberFormatException ex) {
                    messageUtility.sendMessage(player, errorMessagePrefix + "&cYou need to enter a valid spawn point number!");
                    return true;
                }
                return true;
            }
            if (setSubCommand.equalsIgnoreCase("goldspawnarea")) {
                int areaNumber;
                try {
                    areaNumber = Integer.parseInt(args[2]);
                    if (!(areaNumber > 0 && areaNumber <= GameManager.MAX_GOLD_BLOCK_SPAWN_AREAS)) {
                        messageUtility.sendMessage(player, errorMessagePrefix + "&cYou can only set a gold block spawn area value between 1 and " + GameManager.MAX_GOLD_BLOCK_SPAWN_AREAS + "!");
                        return true;
                    }
                    config.setGoldBlockSpawnArea(player, areaNumber);
                    messageUtility.sendMessage(player, successMessagePrefix + "&2Gold spawn area " + areaNumber + " set!");
                } catch (NumberFormatException ex) {
                    messageUtility.sendMessage(player, errorMessagePrefix + "&cYou need to enter a valid spawn point number!");
                    return true;
                }
                return true;
            }
        }
        showHelpPage(player);
        return true;
    }

    public void showHelpPage(Player player) {
        messageUtility.sendMessage(player,
                "&6&lCommand Help" +
                        "\n&b/tl start &7- &fStarts a new game of Time Limit." +
                        "\n&b/tl stop &7- &fStops the game." +
                        "\n&b/tl pause &7- &fToggles pause state of the game." +
                        "\n&b/tl set lobby &7- &fSets the game lobby spawn location." +
                        "\n&b/tl set spawn [num] &7- &fSets game spawn locations for different players." +
                        "\n&b/tl set goldspawnarea [num] &7- &fSets the areas for generating gold blocks.");
    }

    public void loadConfigManager() {
        config = new ConfigManager();
        config.setup();
        config.saveConfig();
        config.reloadConfig();
    }
}
