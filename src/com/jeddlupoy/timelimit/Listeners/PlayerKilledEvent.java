package com.jeddlupoy.timelimit.Listeners;

import com.jeddlupoy.timelimit.GameManager;
import com.jeddlupoy.timelimit.TimeLimit;
import com.jeddlupoy.timelimit.TimeManager;
import com.jeddlupoy.timelimit.Utility.SendMessage;
import com.jeddlupoy.timelimit.Utility.SendTitle;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKilledEvent implements Listener {
    TimeLimit timeLimit;
    GameManager gm;
    TimeManager timeManager;
    SendTitle titleUtility = new SendTitle();
    SendMessage messageUtility = new SendMessage();

    String deathMessagePrefix = "&7[&c☠&7]&r ";

    public PlayerKilledEvent(TimeLimit pl) {
        timeLimit = pl;
        gm = new GameManager(timeLimit);
        timeManager = new TimeManager(timeLimit);
    }

    @EventHandler
    public void onPlayerKilled(PlayerDeathEvent event) {
        final int KILL_TITLE_DURATION_TICKS = 50;
        Player player = event.getEntity();

        if (!GameManager.gameInProgress) return;

        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0F, 1.0F);
        titleUtility.sendTitle(player, null, "&c-5 Minutes", KILL_TITLE_DURATION_TICKS);
        gm.timeManager.removeTime(player, 5);
        GameManager.deaths.put(player, GameManager.deaths.get(player) + 1);
        event.getDrops().clear();

        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            killer.playSound(killer.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 1.0F);
            titleUtility.sendTitle(killer, null, "&6+2 Minutes", KILL_TITLE_DURATION_TICKS);
            gm.timeManager.addTime(killer, 2);
            event.setDeathMessage(messageUtility.formatMessage(deathMessagePrefix + player.getDisplayName() + " &7was killed by " + killer.getDisplayName() + " &7and has &6" + timeManager.getTimeRemainingFormatted(player, true) + " &7left to live."));
            GameManager.kills.put(killer, GameManager.kills.get(killer) + 1);
            gm.addToTimeline("K" + gm.timeManager.getTimeRemaining(player) / 1000, player.getName() + "-" + killer.getName());
        } else {
            event.setDeathMessage(messageUtility.formatMessage(deathMessagePrefix + player.getDisplayName() + " &7was killed and has &6" + timeManager.getTimeRemainingFormatted(player, true) + " &7left to live."));
            gm.addToTimeline("D" + gm.timeManager.getTimeRemaining(player) / 1000, player.getName());
        }

        if (!gm.timeManager.hasRunOutOfTime(player)) return;
        gm.eliminatePlayer(player);
        gm.addToTimeline("0", player.getName());
        Bukkit.getScheduler().runTaskLater(timeLimit, () -> {
            if (gm.getPlayersRemaining() <= 1)
                gm.stopGame();
        }, 5L);
    }
}
