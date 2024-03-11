package com.jeddlupoy.timelimit.Utility;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SendActionBar {
    /*
    Sends a formatted action bar message in Minecraft.
     */
    public void sendActionBar(Player player, String message) {
        String formattedMessage = message != null ? ChatColor.translateAlternateColorCodes('&', message) : "";
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedMessage));
    }
}
