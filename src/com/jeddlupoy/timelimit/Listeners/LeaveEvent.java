package com.jeddlupoy.timelimit.Listeners;

import com.jeddlupoy.timelimit.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!GameManager.gameInProgress) return;

        GameManager.players.remove(player);
        GameManager.playerSpawnAreas.remove(player);
        GameManager.eliminatedPlayers.remove(player);
    }
}
