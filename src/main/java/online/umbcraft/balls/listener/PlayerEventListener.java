package online.umbcraft.balls.listener;

import online.umbcraft.balls.Balls;
import online.umbcraft.balls.LuckySnows;
import online.umbcraft.balls.listener.LuckyEventListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

public class PlayerEventListener implements Listener {

    private final int SNOW_REGEN_DELAY = 600; //2400

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

            if ((int) (Math.random() * 64) == 1) // RANDOM STUFF!! WOO!!!
                LuckySnows.drawItem(e.getPlayer(), e.getClickedBlock());


            Block block = e.getClickedBlock();
            decrementSnow(block.getLocation());

            new BukkitRunnable() {
                public void run() {
                    incrementSnow(block.getLocation());
                }
            }
                    .runTaskLater(plugin, SNOW_REGEN_DELAY);
            return;
        }
    }


    // always adds snow to bottommost block
    private void incrementSnow(Location loc) {
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

        if (original.getType() == Material.ICE &&
        original.getMetadata("placed_block").contains("yes")) {
            original.setType(Material.SNOW);
            return;
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


    private void applyPlayerArmor(PlayerInventory p, Color c) {

        ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+"Snow Suit");
        meta.setColor(c);

        helm.setItemMeta(meta);
        chest.setItemMeta(meta);
        legs.setItemMeta(meta);
        boots.setItemMeta(meta);

        p.setHelmet(helm);
        p.setChestplate(chest);
        p.setLeggings(legs);
        p.setBoots(boots);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent e) {

        if (player_spawns.size() > 0) {
            Location random_loc = player_spawns.get((int) (Math.random() * player_spawns.size()));
            e.setRespawnLocation(new Location(e.getPlayer().getWorld(),
                    random_loc.getX() + 0.5,
                    random_loc.getY() + 1.5,
                    random_loc.getZ() + 0.5));
        }
        applyPlayerArmor(e.getPlayer().getInventory(), Color.WHITE);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerSpawnLocationEvent e) {

        new BukkitRunnable() {
            public void run() {
                plugin.getScores().addPlayer(e.getPlayer());
            }
        }
                .runTaskLater(plugin, 20);

        e.getPlayer().setFoodLevel(20);
        e.getPlayer().setHealth(20);
        e.getPlayer().getInventory().clear();
        applyPlayerArmor(e.getPlayer().getInventory(), Color.WHITE);

        if (player_spawns.size() > 0) {
            Location random_loc = player_spawns.get((int) (Math.random() * player_spawns.size()));
            e.setSpawnLocation(new Location(e.getPlayer().getWorld(),
                    random_loc.getX() + 0.5,
                    random_loc.getY() + 1.5,
                    random_loc.getZ() + 0.5));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeave(PlayerQuitEvent e) {
        plugin.getScores().removePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTakingDamage(EntityDamageByEntityEvent e) {

        if (e.getDamager().getType() == EntityType.SNOWBALL) {

            Snowball snowball = (Snowball) e.getDamager();
            Entity shooter = (Entity) snowball.getShooter();
            e.getEntity().setVelocity(shooter.getLocation().getDirection().setY(0).normalize().multiply(1.25));
            if (shooter instanceof Player) {
                ((Player) shooter).playSound(shooter.getLocation(), Sound.ENTITY_COD_FLOP, 1, 1f);
                ((Player) shooter).stopSound(Sound.ENTITY_PLAYER_HURT);
            }
            e.setDamage(4);
            return;
        }
        e.setDamage(0);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSnowballHitSNow(ProjectileHitEvent e) {

        if (!(e.getEntity() instanceof Snowball))
            return;

        Block b = e.getHitBlock();
        if (b == null)
            return;

        if (b.getType() != Material.SNOW)
            return;

        decrementSnow(b.getLocation());
        new BukkitRunnable() {
            public void run() {
                incrementSnow(b.getLocation());
            }
        }
                .runTaskLater(plugin, SNOW_REGEN_DELAY);

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTryCrafting(InventoryClickEvent e) {
        if (e.getAction() != InventoryAction.DROP_ALL_CURSOR
                && e.getAction() != InventoryAction.DROP_ONE_SLOT
                && e.getAction() != InventoryAction.DROP_ONE_CURSOR
                && e.getAction() != InventoryAction.PICKUP_ALL
                && e.getAction() != InventoryAction.PICKUP_HALF
                && e.getAction() != InventoryAction.PLACE_ALL
                && e.getAction() != InventoryAction.PLACE_ONE
                && e.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
            return;
        }

        if (e.getSlotType() == InventoryType.SlotType.CONTAINER ||
                e.getSlot() == -999)
            return;
        if (e.getSlotType() == InventoryType.SlotType.QUICKBAR
                && e.getSlot() != 40)
            return;

        e.getWhoClicked().closeInventory();
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onUseOffhand(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTryOpenChest(InventoryOpenEvent e) {

        if (e.getInventory().getType() == InventoryType.PLAYER ||
                e.getInventory().getLocation() == null)
            return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTryBreakBlock(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTryTossItem(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() == Material.SNOWBALL)
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlaceBlock(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPickUpItem(EntityPickupItemEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) {
            e.setCancelled(true);
            return;
        }

        Player p = (Player) e.getEntity();
        if (e.getItem().getItemStack().getType() == Material.PRISMARINE_SHARD) {

            if (e.getItem().getItemStack().getItemMeta() != null
                    && e.getItem().getItemStack().getItemMeta().getLore() != null) {

                e.setCancelled(true);
                LuckyEventListener.placeIcicleSphere(e.getItem());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(PlayerDeathEvent e) {
        for (ItemStack i : e.getDrops()) {
            if (i.getType() == Material.SNOWBALL ||
            i.getType().name().contains("LEATHER"))
                i.setType(Material.AIR);
        }

        Player who_died = e.getEntity();

        int ded_score = plugin.getScores().getPlayerScore(who_died.getUniqueId());
        plugin.getScores().setPlayerScore(who_died.getUniqueId(),0);
        Player killer = who_died.getKiller();

        if (killer != null && killer instanceof Player) {
            plugin.getScores().adjustPlayerScore(killer.getUniqueId(),25 + ded_score/2);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamageItem(PlayerItemDamageEvent e){
        e.setCancelled(true);
    }
}
