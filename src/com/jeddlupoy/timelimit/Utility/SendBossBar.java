package com.jeddlupoy.timelimit.Utility;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class SendBossBar {
    private BossBar bar;
    SendMessage messageUtility = new SendMessage();

    public void createBossBar() {
        bar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
    }

    public void showBossBar() {
        bar.setVisible(true);
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            bar.addPlayer(player);
    }

    public void hideBossBar() {
        bar.removeAll();
//        bar.setTitle("");
//        bar.setProgress(0);
//        bar.setVisible(false);
    }

    public void updateBossBar(int timeLeft, int timePeriod) {
        double progressValue = (double) timeLeft / timePeriod;
        long minutes = timeLeft / 60;
        long seconds = timeLeft % 60;
        String secondsShortForm = Long.toString(seconds);
        if (seconds < 10)
            secondsShortForm = "0" + seconds;
        bar.setTitle(messageUtility.formatMessage("&6Gold blocks regenerate in &f" + minutes + ":" + secondsShortForm));
        bar.setProgress(progressValue);
    }
}
