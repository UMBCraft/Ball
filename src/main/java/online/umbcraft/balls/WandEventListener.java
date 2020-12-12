package online.umbcraft.balls;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class WandEventListener implements Listener {

    Balls plugin;
    public WandEventListener(Balls p) {
        plugin = p;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onUseWand(PlayerInteractEvent e) {
        if(!e.getItem().getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE+"Magic Wand"))
            return;
        e.setCancelled(true);
        Location l = e.getClickedBlock().getLocation();
        List<String> spawn_coords = plugin.getConfig().getStringList("player-spawns");
        if(spawn_coords.contains(l.getX()+","+l.getY()+","+l.getZ())) {
            spawn_coords.remove(l.getX()+","+l.getY()+","+l.getZ());
            plugin.getConfig().set("player-spawns",spawn_coords);
            plugin.saveConfig();
            e.getPlayer().sendMessage(ChatColor.RED+"Removed spawn location "+l.getX()+","+l.getY()+","+l.getZ());
        }
        else {
            spawn_coords.add(l.getX()+","+l.getY()+","+l.getZ());
            plugin.getConfig().set("player-spawns",spawn_coords);
            plugin.saveConfig();
            e.getPlayer().sendMessage(ChatColor.GREEN+"Added spawn location "+l.getX()+","+l.getY()+","+l.getZ());
        }
    }
}
