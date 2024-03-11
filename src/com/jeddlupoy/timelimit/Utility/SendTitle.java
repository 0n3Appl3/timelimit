package com.jeddlupoy.timelimit.Utility;

import com.jeddlupoy.timelimit.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SendTitle {
    /*
    Sends a formatted title message to the player.
     */
    public void sendTitle(Player player, String title, String subtitle, int duration) {
        final String formattedTitle = title != null ? ChatColor.translateAlternateColorCodes('&', title) : "";
        final String formattedSubtitle = subtitle != null ? ChatColor.translateAlternateColorCodes('&', subtitle) : "";
        int fadeIn = duration >= 20 ? 10 : 5;
        int fadeOut = duration >= 20 ? 20 : 10;
        player.sendTitle(formattedTitle, formattedSubtitle, fadeIn, duration, fadeOut);
    }

    /*
    Sends a global formatted title to the server chat.
     */
    public void sendGlobalTitle(String title, String subtitle, int duration) {
        final String formattedTitle = title != null ? ChatColor.translateAlternateColorCodes('&', title) : "";
        final String formattedSubtitle = subtitle != null ? ChatColor.translateAlternateColorCodes('&', subtitle) : "";
        int fadeIn = duration >= 20 ? 10 : 5;
        int fadeOut = duration >= 20 ? 20 : 10;

        for (Player player : GameManager.players.keySet()) {
            player.sendTitle(formattedTitle, formattedSubtitle, fadeIn, duration, fadeOut);
        }
    }
}
