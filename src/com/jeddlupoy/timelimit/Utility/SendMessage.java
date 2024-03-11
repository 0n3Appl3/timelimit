package com.jeddlupoy.timelimit.Utility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SendMessage {
    /*
    Sends a formatted message to the server console.
     */
    public void sendConsoleMessage(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    /*
    Converts ampersands into symbols used for colour coding in Minecraft.
     */
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    /*
    Sends a global formatted message to the server chat.
     */
    public void sendGlobalMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
