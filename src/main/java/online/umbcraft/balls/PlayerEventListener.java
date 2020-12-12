package online.umbcraft.balls;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

public class PlayerEventListener implements Listener {

    Balls plugin;
    List<UUID> recent_collectors = new ArrayList<UUID>();
    List<Location> player_spawns = new ArrayList<Location>();

    public PlayerEventListener(Balls p) {
        this.plugin = p;
        if (p.getConfig() != null
                && p.getConfig().getList("player-spawns") != null)
            for (Object s : p.getConfig().getList("player-spawns")) {
                System.out.println(s);
                String[] coords = ((String) s).split(",");

                player_spawns.add(new Location(null,
                        Double.parseDouble(coords[0]),
                        Double.parseDouble(coords[1]),
                        Double.parseDouble(coords[2])
                ));
            }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCollectSnowball(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK
                && e.getClickedBlock().getType() == Material.SNOW
                && !e.getPlayer().getInventory().containsAtLeast(new ItemStack(Material.SNOWBALL), 16)) {

            if (recent_collectors.contains(e.getPlayer().getUniqueId()))
                return;
            recent_collectors.add(e.getPlayer().getUniqueId());

            new BukkitRunnable() {
                public void run() {
                    recent_collectors.remove(e.getPlayer().getUniqueId());
                }
            }
                    .runTaskLater(plugin, 4);

            e.getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL));
            Block block = e.getClickedBlock();
            decrementSnow(block.getLocation());

            new BukkitRunnable() {
                public void run() {
                    incrementSnow(block.getLocation(), e.getPlayer());
                }
            }
                    .runTaskLater(plugin, 240); //2400 = 2 mins
            return;
        }
    }

    // always adds snow to bottommost block
    private void incrementSnow(Location loc, Player p) {
        Block original = loc.getBlock();
        final double TP_AMNT = 0.175;
        Block to_set;
        if (original.getType() == Material.AIR) {
            while ((to_set = original.getLocation().add(0, -1, 0).getBlock()).getType() == Material.AIR) {
                original = to_set;
            }
            original = original.getLocation().add(0, -1, 0).getBlock();

            if (original.getType() != Material.SNOW || ((Snow) original.getBlockData()).getLayers() == 8) {
                loc = original.getLocation().add(0, 1, 0);
                loc.getBlock().setType(Material.SNOW);
                for (Entity e : loc.getWorld().getNearbyEntities(loc, 0.5, 1, 0.5))
                    e.teleport(e.getLocation().add(0, TP_AMNT, 0));
                return;
            }
        } else {
            while (original.getType() == Material.SNOW
                    && ((Snow) original.getBlockData()).getLayers() == 8)
                original = original.getLocation().add(0, 1, 0).getBlock();

            if (original.getType() == Material.AIR) {
                original.setType(Material.SNOW);
                loc = original.getLocation();
                for (Entity e : loc.getWorld().getNearbyEntities(loc, 0.5, 1, 0.5))
                    e.teleport(e.getLocation().add(0, TP_AMNT, 0));
                return;
            }

        }
        Snow snow = (Snow) original.getBlockData();
        snow.setLayers(snow.getLayers() + 1);
        original.setBlockData(snow);
        loc = original.getLocation();
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 0.5, 1, 0.5))
            e.teleport(e.getLocation().add(0, TP_AMNT, 0));
    }

    // always removes snow from topmost block
    private void decrementSnow(Location loc) {

        Block original = loc.getBlock();
        Block to_set;

        while ((to_set = original.getLocation().add(0, 1, 0).getBlock()).getType() == Material.SNOW)
            original = to_set;

        Snow snow = (Snow) original.getBlockData();
        if (snow.getLayers() > 1) {
            snow.setLayers(snow.getLayers() - 1);
            original.setBlockData(snow);
        } else
            original.setType(Material.AIR);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTryBreakBlock(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTryTossItem(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent e) {
        if(player_spawns.size() > 0) {
            Location random_loc = player_spawns.get((int) (Math.random() * player_spawns.size()));
            e.setRespawnLocation(new Location(e.getPlayer().getWorld(),
                    random_loc.getX() + 0.5,
                    random_loc.getY() + 1.5,
                    random_loc.getZ() + 0.5));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerSpawnLocationEvent e) {
        e.getPlayer().setFoodLevel(20);
        e.getPlayer().setHealth(20);

        if(player_spawns.size() > 0) {
            Location random_loc = player_spawns.get((int) (Math.random() * player_spawns.size()));
            e.setSpawnLocation(new Location(e.getPlayer().getWorld(),
                    random_loc.getX() + 0.5,
                    random_loc.getY() + 1.5,
                    random_loc.getZ() + 0.5));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTakingDamage(EntityDamageByEntityEvent e) {

        if (e.getDamager().getType() == EntityType.SNOWBALL) {

            Snowball snowball = (Snowball) e.getDamager();
            Entity shooter = (Entity) snowball.getShooter();
            e.getEntity().setVelocity(shooter.getLocation().getDirection().setY(0).normalize().multiply(1.25));

            e.setDamage(3);
            return;
        }
        e.setDamage(0);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

}
