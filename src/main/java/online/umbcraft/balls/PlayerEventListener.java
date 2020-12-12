package online.umbcraft.balls;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PlayerEventListener implements Listener {

    Balls plugin;
    public PlayerEventListener(Balls p) {
        this.plugin = p;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCollectSnowball(PlayerInteractEvent e) {
        e.getPlayer().sendMessage("collecting...!");
        if(e.getAction() == Action.LEFT_CLICK_BLOCK
                && e.getClickedBlock().getType() == Material.SNOW
                && !e.getPlayer().getInventory().containsAtLeast(new ItemStack(Material.SNOWBALL), 16)) {
            e.getPlayer().sendMessage("good!");
            e.getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL));
            Block block = e.getClickedBlock();

            // always removes snow from topmost layer
            Block to_set;
            while((to_set = block.getLocation().add(0,1,0).getBlock()).getType() == Material.SNOW)
                block = to_set;

            Snow snow = (Snow) block.getBlockData();
            if(snow.getLayers() > 1) {
                snow.setLayers(snow.getLayers() - 1);
                block.setBlockData(snow);
            }
            else
                block.setType(Material.AIR);
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTryBreakBlock(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTryTossItem(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }
}
