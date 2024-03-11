package com.jeddlupoy.timelimit.Listeners;

import com.jeddlupoy.timelimit.GameManager;
import com.jeddlupoy.timelimit.TimeLimit;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnEvent implements Listener {
    TimeLimit timeLimit;
    GameManager gm;

    public RespawnEvent(TimeLimit pl) {
        timeLimit = pl;
        gm = new GameManager(timeLimit);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
//        if (!GameManager.gameInProgress) {
        if (!GameManager.gameInProgress || gm.timeManager.hasRunOutOfTime(player)) {
            Bukkit.getScheduler().runTaskLater(timeLimit, () -> {
                player.teleport(timeLimit.config.getLobby());
            }, 1L);
            return;
        }
//        if (gm.timeManager.hasRunOutOfTime(player)) return;
        Bukkit.getScheduler().runTaskLater(timeLimit, () -> {
            player.teleport(timeLimit.config.getSpawn(GameManager.playerSpawnAreas.get(player)));
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 1.0F);
        }, 1L);
        gm.getGameItems(player);
    }
}
