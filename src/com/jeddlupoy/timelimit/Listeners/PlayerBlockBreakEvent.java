package com.jeddlupoy.timelimit.Listeners;

import com.jeddlupoy.timelimit.GameManager;
import com.jeddlupoy.timelimit.TimeLimit;
import com.jeddlupoy.timelimit.TimeManager;
import com.jeddlupoy.timelimit.Utility.SendTitle;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBlockBreakEvent implements Listener {
    TimeLimit timeLimit;
    TimeManager timeManager;
    SendTitle titleUtility = new SendTitle();

    public PlayerBlockBreakEvent(TimeLimit pl) {
        timeLimit = pl;
        timeManager = new TimeManager(timeLimit);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final int BLOCK_BREAK_DURATION_TICKS = 50;
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!GameManager.gameInProgress) return;
        if (block.getType() == Material.GOLD_BLOCK) {
            event.setDropItems(false);
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0F, 1.0F);
            titleUtility.sendTitle(player, null, "&6+1 Minute", BLOCK_BREAK_DURATION_TICKS);
            timeManager.addTime(player, 1);
            GameManager.blocksCollected.put(player, GameManager.blocksCollected.get(player) + 1);
            return;
        }
        if (!GameManager.woolColours.contains(block.getType())) {
//        if (block.getType() != Material.WHITE_WOOL) {
            event.setCancelled(true);
            return;
        }
        GameManager.placedBlocks.remove(block.getLocation());
    }
}
