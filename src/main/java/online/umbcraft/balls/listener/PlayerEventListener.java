package online.umbcraft.balls.listener;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.items.LuckySnows;
import online.umbcraft.balls.levels.components.perks.Perk;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

public class PlayerEventListener implements Listener {

    private final int SNOW_REGEN_DELAY = 600; //2400
    private final int SNOW_COLLECT_TIMEOUT = 3;
    private final int LUCKY_CHANCE = 48;


    private final double ON_DEATH_LOSE = 0.2;
    private final double ON_KILL_WIN = 0.1;
    private final int ON_KILL_WIN_CONST = 100;
    private final Map<UUID, Long> recentSounds = new HashMap<>();
    private final Sound[] sounds = Sound.values();
    private final Map<UUID, Long> increasedDamage = new HashMap<>();
    JingleBall plugin;
    List<UUID> recentSnowballHarvesters = new ArrayList<>();
    List<Location> spawnLocations = new ArrayList<>();

    public PlayerEventListener(JingleBall p) {
        this.plugin = p;
        if (p.getConfig() != null
                && p.getConfig().getList("player-spawns") != null)
            for (Object s : p.getConfig().getList("player-spawns")) {
                System.out.println(s);
                String[] coords = ((String) s).split(",");

                spawnLocations.add(new Location(null,
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


            // don't allow the player to harvest snow too often
            if (recentSnowballHarvesters.contains(e.getPlayer().getUniqueId()))
                return;


            recentSnowballHarvesters.add(e.getPlayer().getUniqueId());
            e.getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL));

            // RANDOM STUFF!! WOO!!!

            boolean hasChancesPerk = plugin.getLevelingManager()
                    .hasPerk(e.getPlayer(), Perk.CHANCES);

            if ((int) (Math.random() * (LUCKY_CHANCE - (hasChancesPerk ? 8 : 0))) == 1)
                LuckySnows.drawRandomItem(e.getPlayer(), e.getClickedBlock());


            Block block = e.getClickedBlock();
            decrementSnow(block.getLocation());


            // allow player to collect snow again after a certain period of time
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                    plugin,
                    () -> recentSnowballHarvesters.remove(e.getPlayer().getUniqueId()),
                    SNOW_COLLECT_TIMEOUT
            );

            // regenerate the harvested snow after a certain period of time
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                    plugin,
                    () -> incrementSnow(block.getLocation()),
                    SNOW_REGEN_DELAY
            );

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

        //ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta) chest.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Snow Suit");
        meta.setColor(c);

        //helm.setItemMeta(meta);
        chest.setItemMeta(meta);
        legs.setItemMeta(meta);
        boots.setItemMeta(meta);

        //rp.setHelmet(helm);
        p.setChestplate(chest);
        p.setLeggings(legs);
        p.setBoots(boots);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent e) {

        if (spawnLocations.size() > 0) {
            Location random_loc = spawnLocations.get((int) (Math.random() * spawnLocations.size()));
            e.setRespawnLocation(new Location(e.getPlayer().getWorld(),
                    random_loc.getX() + 0.5,
                    random_loc.getY() + 1.5,
                    random_loc.getZ() + 0.5));
        }

        e.getPlayer().setFoodLevel(20);
        e.getPlayer().setHealth(20);
        e.getPlayer().getInventory().clear();
        applyPlayerArmor(e.getPlayer().getInventory(), Color.WHITE);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerSpawnLocationEvent e) {

        // add player to scoreboard in 1 second
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                plugin,
                () -> plugin.getScoreManager().addPlayer(e.getPlayer()),
                20
        );
        Player p = e.getPlayer();
        p.setFoodLevel(20);
        p.setHealth(20);
        p.getInventory().clear();

        if(p.getActivePotionEffects().size() > 0)
            for(PotionEffect effect: e.getPlayer().getActivePotionEffects())
                p.removePotionEffect(effect.getType());

        applyPlayerArmor(p.getInventory(), Color.WHITE);

        if (spawnLocations.size() > 0) {
            Location random_loc = spawnLocations.get((int) (Math.random() * spawnLocations.size()));
            e.setSpawnLocation(new Location(p.getWorld(),
                    random_loc.getX() + 0.5,
                    random_loc.getY() + 1.5,
                    random_loc.getZ() + 0.5));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeave(PlayerQuitEvent e) {
        plugin.getScoreManager().removePlayer(e.getPlayer().getUniqueId());
        plugin.getSpectators().remove(e.getPlayer().getUniqueId());
        plugin.getAttemptedSpectators().remove(e.getPlayer().getUniqueId());

    }

    // perks:
    // feather
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTakingDamage(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Player)
                || e.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        Player p = (Player) e.getEntity();

        System.out.println("has feather perk? "+plugin.getLevelingManager().hasPerk(p, Perk.FEATHER));
        if (plugin.getLevelingManager().hasPerk(p, Perk.FEATHER)) {
            double newdamage = e.getDamage() - 2;
            if(newdamage <= 0)
                e.setCancelled(true);
            e.setDamage(newdamage);

        }
    }


    // perks:
    // sound
    // glowing
    // slow
    // melee
    // target
    // grouped
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTakingDamage(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof Player))
            return;

        Player hurtee = (Player) e.getEntity();
        double finalDamage = 0;

        // if they were damaged by a snowball
        if (e.getDamager().getType() == EntityType.SNOWBALL) {
            Snowball snowball = (Snowball) e.getDamager();
            Entity shooter = (Entity) snowball.getShooter();
            e.getEntity().setVelocity(shooter.getLocation().getDirection().setY(0).normalize().multiply(1.25));
            finalDamage = 4;
            if (shooter instanceof Player) {
                Player playerShooter = (Player) shooter;
                playerShooter.playSound(shooter.getLocation(), Sound.ENTITY_COD_FLOP, 1, 1f);
                playerShooter.stopSound(Sound.ENTITY_PLAYER_HURT);

                // perks!
                if (plugin.getLevelingManager().hasPerk(playerShooter, Perk.GLOWING)) {
                    ((Player) e.getEntity()).addPotionEffect(
                            new PotionEffect(PotionEffectType.GLOWING, 20 * 5, 0));
                }
                if (plugin.getLevelingManager().hasPerk(playerShooter, Perk.GLOWING)) {
                    finalDamage += 0.75;
                }
            }

            // if they were damaged by a player punch
        } else if (e.getDamager() instanceof Player) {
            // perks!
            Player hurter = (Player) e.getDamager();
            if (plugin.getLevelingManager().hasPerk(hurter, Perk.SLOW)) {
                hurtee.addPotionEffect(
                        new PotionEffect(PotionEffectType.SLOW, 20 * 2, 0));
            }
            finalDamage = (plugin.getLevelingManager().hasPerk(hurter, Perk.MELEE) ? 2 : 0);
        }

        // stack the damage if they were targeted
        long curTime = System.currentTimeMillis();
        finalDamage = (curTime - increasedDamage.getOrDefault(hurtee.getUniqueId(), 0L) < 2000)
                ? finalDamage * 1.2 : finalDamage;
        if (plugin.getLevelingManager().hasPerk(hurtee, Perk.TARGET))
            increasedDamage.put(hurtee.getUniqueId(), curTime);


        // reduce the damage if they are grouped
        if (plugin.getLevelingManager().hasPerk(hurtee, Perk.GROUPED)) {
            finalDamage = finalDamage *
                    Math.pow(0.9, hurtee.getNearbyEntities(7, 4, 7)
                            .stream().filter(i -> i instanceof Player
                                    && !plugin.getSpectators().contains(i))
                            .count() - 1);
        }

        e.setDamage(finalDamage);

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

        // regenerate snow after a certain period
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                plugin,
                () -> incrementSnow(b.getLocation()),
                SNOW_REGEN_DELAY
        );

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
    public void onMove(PlayerMoveEvent e) {
        if (!plugin.getLevelingManager().hasPerk(e.getPlayer(), Perk.SOUNDS))
            return;

        UUID uid = e.getPlayer().getUniqueId();

        long lastTime = recentSounds.getOrDefault(uid, 0L);
        long curTime = System.currentTimeMillis();

        if (curTime - lastTime > 2000) {
            recentSounds.put(uid, curTime);
            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(),
                    sounds[(int) (Math.random() * sounds.length)], 10, 1);
        }
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

        Material[] keep = {
                Material.BELL,
                Material.COAL,
                Material.SWEET_BERRIES,
                Material.PRISMARINE_SHARD,
                Material.IRON_HOE};

        for (ItemStack i : e.getDrops()) {
            Arrays.stream(keep).forEach(k ->  {
                if(!i.getType().equals(k))
                    i.setType(Material.AIR);
            });
        }

        Player whoDied = e.getEntity();
        int deadsScore = plugin.getScoreManager().getPlayerScore(whoDied.getUniqueId());
        Player killer = whoDied.getKiller();

        double multiplier = plugin.getTournamentManager().getActiveMultiplier();

        int lostPoints = (int) (deadsScore * ON_DEATH_LOSE);
        plugin.getScoreManager().setPlayerScore(whoDied.getUniqueId(), lostPoints);

        // allow the player to get a custom death message if they kill themselves
        if (killer != null)
            e.setDeathMessage(ChatColor.RED + getDeathMessage(whoDied.getName(), killer.getName()));
        else
            e.setDeathMessage(null);
        // send a message to the players if one died to another, and
        // do something different if the player died to nobody or to themselves
        if (killer != null && !killer.getUniqueId().equals(whoDied.getUniqueId())) {

            int gainedPoints = (int) (multiplier * (ON_KILL_WIN_CONST + (int) (deadsScore * ON_KILL_WIN)));
            plugin.getScoreManager().adjustPlayerScore(killer.getUniqueId(), gainedPoints);
            killer.sendMessage(ChatColor.GREEN + "You gained " + gainedPoints + " points for killing " + whoDied.getName());
            whoDied.sendMessage(ChatColor.GOLD + "You lost " + lostPoints + " points for dying to " + killer.getName());
        } else {
            whoDied.sendMessage(ChatColor.GOLD + "You lost " + lostPoints + " points for dying");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamageItem(PlayerItemDamageEvent e) {
        e.setCancelled(true);
    }

    private String getDeathMessage(String died, String killer) {

        String[] deathMessages = {
                "{died} took {killer}'s balls to the face!",
                "{killer} pushed {died} into some yellow snow!",
                "{died} had too much of {killer}'s eggnog!",
                "{killer} let {died} die of frostbite!",
                "{killer} gave {died} a run for their money!",
                "{died} ate pavement thanks to {killer}!",
                "{died} couldn't outrun {killer}!",
                "{killer} brought {died} to tears!",
                "{died} wasn't fast enough to avoid {killer}!",
                "{killer} stomped on {died}'s snowman!",
                "{killer} mercilessly slaughtered {died}!"
        };

        String randomMessage = deathMessages[(int) (deathMessages.length * Math.random())];
        randomMessage = randomMessage.replaceAll("\\{died\\}", died);
        randomMessage = randomMessage.replaceAll("\\{killer\\}", killer);

        return randomMessage;
    }
}
