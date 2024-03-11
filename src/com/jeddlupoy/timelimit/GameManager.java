package com.jeddlupoy.timelimit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jeddlupoy.timelimit.Utility.SendBossBar;
import com.jeddlupoy.timelimit.Utility.SendMessage;
import com.jeddlupoy.timelimit.Utility.SendTitle;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;

public class GameManager {
    public static Map<Player, Integer> players = new HashMap<>();
    public static ArrayList<Player> eliminatedPlayers = new ArrayList<>();
    public static Map<Player, Integer> playerSpawnAreas = new HashMap<>();
    public static ArrayList<Location> placedBlocks = new ArrayList<>();
    public static ArrayList<Color> colours;
    public static ArrayList<Material> woolColours;
    public static ArrayList<String> displayNameColours;
    public static final int TIME_TO_LIVE = 20;
    public static final int MAX_PLAYERS = 10;
    public static final int MAX_GOLD_BLOCK_SPAWN_AREAS = 100;
    public static long gameStartTime = 0;
    public static boolean gameStarted = false;
    public static boolean gameInProgress = false;
    public static boolean gamePaused = false;

    // Game Stats for Website
    public static Map<Player, Integer> kills = new HashMap<>();
    public static Map<Player, Integer> deaths = new HashMap<>();
    public static Map<Player, Integer> blocksCollected = new HashMap<>();
    public static Map<Player, Integer> timeLeft = new HashMap<>();
    public static JsonArray users = new JsonArray();
    public static JsonArray timeline = new JsonArray();

    TimeLimit timeLimit;
    public static TimeManager timeManager;
    SendTitle titleUtility;
    SendMessage messageUtility;
    public static SendBossBar bossBarUtility;

    String gameMessagePrefix = "&7[&6▷&7]&r ";
    String deathMessagePrefix = "&7[&c☠&7]&r ";

    public GameManager(TimeLimit pl) {
        timeLimit = pl;
        timeManager = new TimeManager(timeLimit);
        titleUtility = new SendTitle();
        messageUtility = new SendMessage();
        bossBarUtility = new SendBossBar();

        colours = new ArrayList<>();
        colours.add(Color.RED);
        colours.add(Color.ORANGE);
        colours.add(Color.YELLOW);
        colours.add(Color.LIME);
        colours.add(Color.GREEN);
        colours.add(Color.TEAL);
        colours.add(Color.AQUA);
        colours.add(Color.BLUE);
        colours.add(Color.FUCHSIA);
        colours.add(Color.PURPLE);

        woolColours = new ArrayList<>();
        woolColours.add(Material.RED_WOOL);
        woolColours.add(Material.ORANGE_WOOL);
        woolColours.add(Material.YELLOW_WOOL);
        woolColours.add(Material.LIME_WOOL);
        woolColours.add(Material.GREEN_WOOL);
        woolColours.add(Material.CYAN_WOOL);
        woolColours.add(Material.LIGHT_BLUE_WOOL);
        woolColours.add(Material.BLUE_WOOL);
        woolColours.add(Material.MAGENTA_WOOL);
        woolColours.add(Material.PURPLE_WOOL);

        displayNameColours = new ArrayList<>();
        displayNameColours.add("&c");
        displayNameColours.add("&6");
        displayNameColours.add("&e");
        displayNameColours.add("&a");
        displayNameColours.add("&2");
        displayNameColours.add("&3");
        displayNameColours.add("&b");
        displayNameColours.add("&9");
        displayNameColours.add("&d");
        displayNameColours.add("&5");
    }

    public void startGame() {
        Random rand = new Random();
        int playerNumber;
        gameInProgress = true;
        bossBarUtility.createBossBar();
//        gameStartTime = System.currentTimeMillis();
        for (World world : Bukkit.getServer().getWorlds())
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            players.put(player, TIME_TO_LIVE);
            kills.put(player, 0);
            deaths.put(player, 0);
            blocksCollected.put(player, 0);
            timeLeft.put(player, 0);
            do playerNumber = rand.nextInt(MAX_PLAYERS);
            while (playerSpawnAreas.containsValue(playerNumber));
            playerSpawnAreas.put(player, playerNumber);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setDisplayName(messageUtility.formatMessage(displayNameColours.get(playerNumber) + "&l" + player.getName()));
            player.setPlayerListName(messageUtility.formatMessage(displayNameColours.get(playerNumber) + player.getName()));
            player.teleport(timeLimit.config.getSpawn(playerNumber));
            getGameItems(player);
        }
        startGameCountdown();
    }

    public void stopGame() {
        gameStarted = false;
        gameInProgress = false;
        gamePaused = false;
        if (getPlayersRemaining() == 1) {
            titleUtility.sendGlobalTitle("&6&lGAME OVER", getWinner().getDisplayName() + " &fis the winner!", 70);
            timeLeft.put(getWinner(), (int) timeManager.getTimeRemaining(getWinner()) / 1000);
            eliminatedPlayers.add(getWinner());
            saveGameData();
        } else {
            titleUtility.sendGlobalTitle("&c&lGAME OVER", "&cNo winner", 70);
            saveGameData();
        }
        for (World world : Bukkit.getServer().getWorlds())
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
        for (Location location : placedBlocks)
            location.getBlock().setType(Material.AIR);
        for (Player player : players.keySet()) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
            player.getInventory().clear();
            player.teleport(timeLimit.config.getLobby());
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            if (player.getGameMode() == GameMode.SPECTATOR)
                player.setGameMode(GameMode.SURVIVAL);
        }
        resetGame();
    }

    public void saveGameData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        JsonObject gameResult = new JsonObject();
        for (int i = eliminatedPlayers.size() - 1; i >= 0; i--) {
            Player player = eliminatedPlayers.get(i);
            JsonObject stat = new JsonObject();
            stat.addProperty("name", player.getName());
            stat.addProperty("kills", kills.get(player));
            stat.addProperty("deaths", deaths.get(player));
            stat.addProperty("blocksCollected", blocksCollected.get(player));
            stat.addProperty("timeLeft", timeLeft.get(player));
            users.add(stat);
        }
        gameResult.addProperty("date", dateFormat.format(new Date()));
        gameResult.add("users", users);
        gameResult.add("timeline", timeline);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://appl3pvp.com:3000/api/v1/games/results"))
                    .headers("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gameResult.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            messageUtility.sendConsoleMessage("Status Code: " + response.statusCode() + "\nBody: " + response.body());
            messageUtility.sendGlobalMessage("&f&lView the game results: &b&nhttps://appl3pvp.com/timelimit");
        } catch(Exception ex) {
            messageUtility.sendConsoleMessage(ex.toString());
        }
    }

    public void resetGame() {
        // Clear game JSON data.
        kills.clear();
        deaths.clear();
        blocksCollected.clear();
        timeLeft.clear();
        users = new JsonArray();
        timeline = new JsonArray();
        // Clear game data.
        bossBarUtility.hideBossBar();
        players.clear();
        eliminatedPlayers.clear();
        playerSpawnAreas.clear();
        placedBlocks.clear();
        timeManager.stopGameTimer();
    }

    public void togglePauseGame() {
        gamePaused = !gamePaused;
        if (gamePaused) {
            timeManager.stopGameTimer();
            titleUtility.sendGlobalTitle("&c&lGAME PAUSED", null, 70);
        } else {
            timeManager.startGameTimer(this);
            titleUtility.sendGlobalTitle("&c&lGAME RESUMED", null, 70);
        }
    }

    public void startGameCountdown() {
        new BukkitRunnable() {
            int countdown = 10;
            final int TIME_START_COUNTING_DOWN = 5;
            final int TIME_SHOW_OBJECTIVES = 10;
            @Override
            public void run() {
                if (countdown == 0) {
                    titleUtility.sendGlobalTitle("&c&lFIGHT!", null, 70);
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_2, 1.0F, 1.0F));
                    timeManager.startGameTimer(GameManager.this);
                    generateGoldBlocks();
                    bossBarUtility.showBossBar();
                    gameStarted = true;
                    gameStartTime = System.currentTimeMillis();
                    cancel();
                    return;
                }
                if (countdown <= TIME_START_COUNTING_DOWN) {
                    titleUtility.sendGlobalTitle("&c" + countdown, null, 70);
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F));
                }
                if (countdown == TIME_SHOW_OBJECTIVES) {
                    messageUtility.sendGlobalMessage("&7&m---&r &6&lObjectives&r &7&m---" +
                                                    "\n&7► &fKill your opponents to take minutes off their time &c(-5 mins) &fand gain time &a(+2 mins)" +
                                                    "\n&7► &fBreak &6gold blocks &fto gain time &a(+1 min)" +
                                                    "\n&7► &fThe last player standing wins");
                }
                countdown--;
            }
        }.runTaskTimer(timeLimit, 0L, 20L);
    }

    public void generateGoldBlocks() {
        Random rand = new Random();
        ArrayList<Location> locations = timeLimit.config.getGoldBlockSpawnAreas();
        final int DELAY = 5;
        final int SPAWN_PERIOD = 300;
        final int RADIUS = 1;
        final int MAX_CHANCE_NUM = 6;

        new BukkitRunnable() {
            int countdown = 0;
            @Override
            public void run() {
                if (!gameInProgress) {
                    cancel();
                    return;
                }
                if (countdown == 10) {
                    messageUtility.sendGlobalMessage(gameMessagePrefix + "&eGold blocks regenerating in &6" + countdown + " &eseconds...");
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F));
                }
                if (countdown == 0) {
                    for (Location location : locations) {
                        for (int x = -RADIUS; x <= RADIUS; x++) {
                            for (int z = -RADIUS; z <= RADIUS; z++) {
                                if (rand.nextInt(MAX_CHANCE_NUM) == 1) {
                                    Location l = new Location(location.getWorld(), location.getX() + x, location.getY(), location.getZ() + z);
                                    l.getBlock().setType(Material.GOLD_BLOCK);
                                }
                            }
                        }
                    }
                    messageUtility.sendGlobalMessage(gameMessagePrefix + "&eGold blocks have been generated!");
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0F, 1.0F));
                    countdown = SPAWN_PERIOD;
                }
                bossBarUtility.updateBossBar(countdown, SPAWN_PERIOD);
                countdown--;
            }
        }.runTaskTimer(timeLimit, DELAY * 20L, 20L);
    }

    public void getGameItems(Player player) {
        Color colour = colours.get(playerSpawnAreas.get(player));
        Material wool = woolColours.get(playerSpawnAreas.get(player));
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
        player.getInventory().addItem(
                new ItemStack(Material.STONE_SWORD),
                new ItemStack(Material.IRON_PICKAXE),
                new ItemStack(Material.STONE_AXE),
                new ItemStack(Material.SHEARS),
                new ItemStack(Material.BOW),
                new ItemStack(Material.ARROW, 32),
                new ItemStack(wool, 64),
                new ItemStack(Material.COOKED_BEEF, 16));
        player.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
        player.getInventory().setHelmet(customiseLeatherAmour(Material.LEATHER_HELMET, colour));
        player.getInventory().setChestplate(customiseLeatherAmour(Material.LEATHER_CHESTPLATE, colour));
        player.getInventory().setLeggings(customiseLeatherAmour(Material.LEATHER_LEGGINGS, colour));
        player.getInventory().setBoots(customiseLeatherAmour(Material.LEATHER_BOOTS, colour));
    }

    public ItemStack customiseLeatherAmour(Material armour, Color colour) {
        if (colour == null)
            colour = Color.WHITE;
        ItemStack item = new ItemStack(armour);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(colour);
        item.setItemMeta(meta);
        return item;
    }

    public int getPlayersRemaining() {
        return players.size() - eliminatedPlayers.size();
    }

    public Player getWinner() {
        for (Player player : players.keySet()) {
            if (!eliminatedPlayers.contains(player))
                return player;
        }
        return null;
    }

    public void eliminatePlayer(Player player) {
        eliminatedPlayers.add(player);
        player.getInventory().clear();
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0F, 1.0F);
        player.setGameMode(GameMode.SPECTATOR);
        if (getPlayersRemaining() > 1)
            titleUtility.sendTitle(player, "&c&lGAME OVER", "&cYou ran out of time!", 80);
        messageUtility.sendGlobalMessage(deathMessagePrefix + player.getDisplayName() + " &7has run out of time! &b" + getPlayersRemaining() + " &fplayers remaining.");
    }

    public void addToTimeline(String id, String user) {
        JsonObject record = new JsonObject();
        record.addProperty("id", id);
        record.addProperty("time", (int) timeManager.getTimeElapsed() / 1000);
        record.addProperty("user", user);
        timeline.add(record);
    }
}
