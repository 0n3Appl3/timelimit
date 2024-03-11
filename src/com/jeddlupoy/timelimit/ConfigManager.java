package com.jeddlupoy.timelimit;

import com.jeddlupoy.timelimit.Utility.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.ArrayList;

public class ConfigManager {
    TimeLimit plugin = TimeLimit.getPlugin(TimeLimit.class);
    private SendMessage messageUtility = new SendMessage();
    public FileConfiguration config;
    public File settings;
    public String settingsFileName = "settings.yml";

    public void setup() {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir(); // Create plugin directory if not found.

        settings = new File(plugin.getDataFolder(), settingsFileName);
        checkFile(settings, settingsFileName);
        config = YamlConfiguration.loadConfiguration(settings);
    }

    public void checkFile(File file, String fileName) {
        if (file.exists()) return;
        try {
            file.createNewFile();
            messageUtility.sendConsoleMessage("&aThe " + fileName + " file has been created!");
        } catch (IOException e) {
            messageUtility.sendConsoleMessage("&cCould not create the " + fileName + " file!");
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(settings);
            messageUtility.sendConsoleMessage("&aThe " + settingsFileName + " file has been saved!");
        } catch (IOException e) {
            messageUtility.sendConsoleMessage("&cCould not save the " + settingsFileName + " file!");
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(settings);
        messageUtility.sendConsoleMessage("&aThe " + settingsFileName + " file has been reloaded!");
    }

    public Location getLocation(double x, double y, double z, float yaw, float pitch, String worldName) {
        World world = Bukkit.getServer().getWorld(worldName);
        Location location = new Location(world, x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        return location;
    }

    public void setSpawn(Player player, int playerNumber) {
        getConfig().set("settings.spawn." + playerNumber + ".x", player.getLocation().getX());
        getConfig().set("settings.spawn." + playerNumber + ".y", player.getLocation().getY());
        getConfig().set("settings.spawn." + playerNumber + ".z", player.getLocation().getZ());
        getConfig().set("settings.spawn." + playerNumber + ".yaw", player.getLocation().getYaw());
        getConfig().set("settings.spawn." + playerNumber + ".pitch", player.getLocation().getPitch());
        getConfig().set("settings.spawn." + playerNumber + ".world", player.getLocation().getWorld().getName());
        saveConfig();
    }

    public Location getSpawn(int playerNumber) {
        double x = getConfig().getDouble("settings.spawn." + playerNumber + ".x");
        double y = getConfig().getDouble("settings.spawn." + playerNumber + ".y");
        double z = getConfig().getDouble("settings.spawn." + playerNumber + ".z");
        float yaw = (float) getConfig().getDouble("settings.spawn." + playerNumber + ".yaw");
        float pitch = (float) getConfig().getDouble("settings.spawn." + playerNumber + ".pitch");
        String worldName = getConfig().getString("settings.spawn." + playerNumber + ".world");
        return getLocation(x, y, z, yaw, pitch, worldName);
    }

    public int getSpawnCount() {
        try {
            Set<String> spawns = Objects.requireNonNull(getConfig().getConfigurationSection("settings.spawn")).getKeys(false);
            return spawns.size();
        } catch (Exception ignored) { }
        return 0;
    }

    public void setLobby(Player player) {
        getConfig().set("settings.lobby.x", player.getLocation().getX());
        getConfig().set("settings.lobby.y", player.getLocation().getY());
        getConfig().set("settings.lobby.z", player.getLocation().getZ());
        getConfig().set("settings.lobby.yaw", player.getLocation().getYaw());
        getConfig().set("settings.lobby.pitch", player.getLocation().getPitch());
        getConfig().set("settings.lobby.world", player.getLocation().getWorld().getName());
        saveConfig();
    }

    public Location getLobby() {
        double x = getConfig().getDouble("settings.lobby.x");
        double y = getConfig().getDouble("settings.lobby.y");
        double z = getConfig().getDouble("settings.lobby.z");
        float yaw = (float) getConfig().getDouble("settings.lobby.yaw");
        float pitch = (float) getConfig().getDouble("settings.lobby.pitch");
        String worldName = getConfig().getString("settings.lobby.world");
        return getLocation(x, y, z, yaw, pitch, worldName);
    }

    public boolean doesLobbyExist() {
        return getConfig().isSet("settings.lobby");
    }

    public void setGoldBlockSpawnArea(Player player, int number) {
        getConfig().set("settings.gold-block-spawn-area." + number + ".x", player.getLocation().getX());
        getConfig().set("settings.gold-block-spawn-area." + number + ".y", player.getLocation().getY());
        getConfig().set("settings.gold-block-spawn-area." + number + ".z", player.getLocation().getZ());
        getConfig().set("settings.gold-block-spawn-area." + number + ".yaw", player.getLocation().getYaw());
        getConfig().set("settings.gold-block-spawn-area." + number + ".pitch", player.getLocation().getPitch());
        getConfig().set("settings.gold-block-spawn-area." + number + ".world", player.getLocation().getWorld().getName());
        saveConfig();
    }

    public Location getGoldBlockSpawnArea(int number) {
        double x = getConfig().getDouble("settings.gold-block-spawn-area." + number + ".x");
        double y = getConfig().getDouble("settings.gold-block-spawn-area." + number + ".y");
        double z = getConfig().getDouble("settings.gold-block-spawn-area." + number + ".z");
        float yaw = (float) getConfig().getDouble("settings.gold-block-spawn-area." + number + ".yaw");
        float pitch = (float) getConfig().getDouble("settings.gold-block-spawn-area." + number + ".pitch");
        String worldName = getConfig().getString("settings.gold-block-spawn-area." + number + ".world");
        return getLocation(x, y, z, yaw, pitch, worldName);
    }

    public ArrayList<Location> getGoldBlockSpawnAreas() {
        ArrayList<Location> locations = new ArrayList<>();
        try {
            Set<String> spawns = Objects.requireNonNull(getConfig().getConfigurationSection("settings.gold-block-spawn-area")).getKeys(false);
            for (String spawnNumber : spawns) {
                Location location = getGoldBlockSpawnArea(Integer.parseInt(spawnNumber));
                locations.add(location);
            }
            return locations;
        } catch (Exception ignored) { }
        return locations;
    }

    public boolean doesGoldBlockSpawnAreasExist() {
        return getConfig().isSet("settings.gold-block-spawn-area");
    }
}
