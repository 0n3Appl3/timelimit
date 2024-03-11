package com.jeddlupoy.timelimit.Listeners;

import com.jeddlupoy.timelimit.GameManager;
import com.jeddlupoy.timelimit.Utility.SendActionBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEvent implements Listener {
    SendActionBar actionBarUtility = new SendActionBar();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!GameManager.gameInProgress) return;
        if (!GameManager.players.containsKey(player)) return;
        if (!GameManager.gameStarted) {
            actionBarUtility.sendActionBar(player, "&cYou cannot move because the game has not yet started!");
            event.setCancelled(true);
        } else if (GameManager.gamePaused) {
            actionBarUtility.sendActionBar(player, "&cYou cannot move because the game is paused!");
            event.setCancelled(true);
        }
    }
}
