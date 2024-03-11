package com.jeddlupoy.timelimit.Listeners;

import com.jeddlupoy.timelimit.GameManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerBlockPlaceEvent implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (!GameManager.gameInProgress) return;
        if (!GameManager.woolColours.contains(block.getType())) {
//        if (block.getType() != Material.WHITE_WOOL) {
            event.setCancelled(true);
            return;
        }
        GameManager.placedBlocks.add(block.getLocation());
    }
}
