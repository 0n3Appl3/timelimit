package com.jeddlupoy.timelimit;

import com.jeddlupoy.timelimit.Utility.SendActionBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TimeManager {
    private BukkitTask gameTimeChecker;

    TimeLimit timeLimit;
    SendActionBar actionBarUtility;

    public TimeManager(TimeLimit pl) {
        timeLimit = pl;
        actionBarUtility = new SendActionBar();
    }

    public void startGameTimer(GameManager gm) {
        gameTimeChecker = new BukkitRunnable() {
            public void run() {
                for (Player player : GameManager.players.keySet()) {
                    if (!GameManager.eliminatedPlayers.contains(player)) {
                        if (hasRunOutOfTime(player))
                            gm.eliminatePlayer(player);
                        else
                            actionBarUtility.sendActionBar(player, "&aTime Left &7► &f" + getTimeRemainingFormatted(player, true));
                    }
                }
//                if (gm.getPlayersRemaining() <= 1)
//                    gm.stopGame();
            }
        }.runTaskTimer(timeLimit, 0L, 20L);
    }

    public void stopGameTimer() {
        gameTimeChecker.cancel();
    }

    public void addTime(Player player, int minutes) {
        int newTime = GameManager.players.get(player) + minutes;
        GameManager.players.put(player, newTime);
    }

    public void removeTime(Player player, int minutes) {
        int newTime = Math.max(GameManager.players.get(player) - minutes, 0);
        GameManager.players.put(player, newTime);
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - GameManager.gameStartTime;
    }

    public long getTimeRemaining(Player player) {
        return Math.max(GameManager.players.get(player) * 60000 - getTimeElapsed(), 0);
    }

    public String getTimeRemainingFormatted(Player player, boolean shortForm) {
        long minutes = (getTimeRemaining(player) / 1000) / 60;
        long seconds = (getTimeRemaining(player) / 1000) % 60;
        String secondsShortForm = Long.toString(seconds);
        if (shortForm && seconds < 10)
            secondsShortForm = "0" + seconds;
        return shortForm ? minutes + ":" + secondsShortForm : minutes + " minutes, " + seconds + " seconds";
    }

    public boolean hasRunOutOfTime(Player player) {
        return getTimeElapsed() >= GameManager.players.get(player) * 60000;
    }
}
