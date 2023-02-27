package online.umbcraft.balls.listener;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.Perk;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class LuckyEventListener implements Listener {

    private static JingleBall plugin;
    List<UUID> recent_users = new ArrayList<>();

    public LuckyEventListener(JingleBall p) {
        plugin = p;
    }

    public static void bellBomb(Location loc, Player player) {

        Location spawn = loc.add(new Vector(0, 1, 0));
        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 5 * 20, 4);
        for (Entity e : spawn.getWorld().getNearbyEntities(spawn, 9, 4, 9)) {
            if (e instanceof Player && e != player && !plugin.getSpectators().contains(e.getUniqueId()))
                slow.apply((LivingEntity) e);
        }


        spawn.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, spawn, 25, 3, 1, 3);

        boolean improved = plugin.getLevelingManager().hasPerk(player, Perk.POTENCY);
        int iters = 64;
        if(improved)
            iters = 192;

        for (int i = 0; i < iters; i++) {
            for(int count = 0; count < 3; count++) {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                        plugin, () -> {
                            Snowball snowball = (Snowball) spawn.getWorld().spawnEntity(spawn, EntityType.SNOWBALL);
                            snowball.setVelocity(
                                    new Vector(
                                            1.5 * (Math.random() - 0.5),
                                            1.1 * (Math.random() - 0.1),
                                            1.5 * (Math.random() - 0.5)
                                    ));
                            snowball.setShooter(player);
                        }, i);
            }
        }
    }

    public static void placeIcicleSphere(Item icicle, Player p) {
        if (icicle.isDead())
            return;

        Location center = icicle.getLocation().getBlock().getLocation().add(new Vector(0.5, 0.5, 0.5));

        icicle.remove();

        boolean improved = plugin.getLevelingManager().hasPerk(p, Perk.POTENCY);
        double sphere_radius = 2.5;
        if(improved)
            sphere_radius = 3.5;


        center.getWorld().playSound(center, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 10, 1f);
        int incr = 0;
        for (double i = Math.PI; i >= 0; i -= Math.PI / (Math.PI * sphere_radius * 2)) {
            double circle_radius = Math.sin(i) * sphere_radius * 2;
            double y = Math.cos(i) * sphere_radius * 2;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / (Math.PI * circle_radius * 2)) {
                double x = Math.cos(a) * circle_radius;
                double z = Math.sin(a) * circle_radius;
                Location to_set = center.clone().add(x, y, z);

                if (to_set.getBlock().getType() == Material.AIR) {

                    to_set.getBlock().setType(Material.ICE);
                    to_set.getBlock().setMetadata("spawned_block", new FixedMetadataValue(plugin, "yes"));
                    new BukkitRunnable() {
                        public void run() {
                            to_set.getBlock().setType(Material.AIR);
                            to_set.getWorld().playSound(to_set, Sound.BLOCK_BEEHIVE_DRIP, 1, 1f);
                        }
                    }
                            .runTaskLater(plugin, 800 - (incr * 2));
                    incr++;
                }
            }
        }
    }

    public void addRecentUser(UUID uuid, int tick_amount) {
        recent_users.add(uuid);
        new BukkitRunnable() {
            public void run() {
                recent_users.remove(uuid);
            }
        }
                .runTaskLater(plugin, tick_amount);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onUseItem(PlayerInteractEvent e) {

        if (e.getItem() == null ||
                e.getItem().getType() == Material.SNOWBALL)
            return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Material type = e.getItem().getType();
        if (recent_users.contains(e.getPlayer().getUniqueId()))
            return;


        boolean improved = plugin.getLevelingManager().hasPerk(e.getPlayer(), Perk.POTENCY);

        if (type == Material.IRON_HOE) {
            addRecentUser(e.getPlayer().getUniqueId(), 15);
            e.setCancelled(true);
            if (!e.getPlayer().getInventory().containsAtLeast(new ItemStack(Material.SNOWBALL), 2)) {
                e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.WHITE + "You don't have enough snowballs!");
                return;
            }

            //remove snowball ammo from inventory
            e.getPlayer().getInventory().removeItem(new ItemStack(Material.SNOWBALL, 2));

            //remove durability from gun
            ItemStack gun = e.getItem();
            Damageable new_gun_meta = (Damageable) (gun.getItemMeta());
            int new_damage = new_gun_meta.getDamage() + 25;

            if (new_damage <= 250) {
                new_gun_meta.setDamage(new_damage);
                gun.setItemMeta((ItemMeta) new_gun_meta);
            } else
                e.getPlayer().getInventory().remove(gun);

            float accuracy = 0.4f;
            int num_snowballs = 6;
            int speed = 2;
            if(improved) {
                num_snowballs = 10;
            }
            if (e.getPlayer().isSneaking()) {
                accuracy = 0.025f;
                num_snowballs = 1;
                speed = 3;
                if(improved) {
                    num_snowballs = 3;
                    speed = 4;
                }
            }

            for (int i = 0; i < num_snowballs; i++) {
                Snowball snowball = e.getPlayer().launchProjectile(Snowball.class);
                Vector velocity = e.getPlayer().getLocation().getDirection();
                velocity.add(new Vector(
                        Math.random() * accuracy - (accuracy / 2),
                        Math.random() * accuracy - (accuracy / 2),
                        Math.random() * accuracy - (accuracy / 2)));
                velocity.multiply(speed);
                snowball.setVelocity(velocity);
            }
        }

        if (type == Material.COAL) {
            addRecentUser(e.getPlayer().getUniqueId(), 20);
            e.setCancelled(true);
            e.getItem().setAmount(e.getItem().getAmount() - 1);
            e.getPlayer().sendMessage(ChatColor.GRAY + "A cloud of soot appears, blinding everyone around you!");
            Location l = e.getPlayer().getLocation();

            double circle_radius = 4.5;
            if(improved)
                circle_radius = 7.5;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / (Math.PI * circle_radius)) {
                double x = Math.cos(a) * circle_radius;
                double z = Math.sin(a) * circle_radius;
                Location to_set = l.clone().add(x, 0, z);
                to_set.getWorld().spawnParticle(Particle.SQUID_INK, to_set, 5);
            }

            int r = 5;
            if(improved)
                r = 8;

            Collection<Entity> entities = l.getWorld().getNearbyEntities(l, r, r, r);
            entities.remove(e.getPlayer());

            PotionEffect wither = new PotionEffect(PotionEffectType.WITHER, 4 * 20, 2);
            PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 8 * 20, 1);

            for (Entity ent : entities)
                if (ent instanceof LivingEntity && !plugin.getSpectators().contains(ent.getUniqueId())) {
                    wither.apply((LivingEntity) ent);
                    blindness.apply((LivingEntity) ent);
                }
        }

        if (type == Material.PRISMARINE_SHARD) {
            addRecentUser(e.getPlayer().getUniqueId(), 40);
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.AQUA + "You throw the Icicle, causing it to shatter!");
            e.getItem().setAmount(e.getItem().getAmount() - 1);

            ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD);
            ItemMeta meta = shard.getItemMeta();

            List<String> lore = new LinkedList<String>();
            lore.add(e.getPlayer().getName() + System.currentTimeMillis());
            meta.setLore(lore);

            shard.setItemMeta(meta);

            Item shard_item = e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation()
                    .add(new Vector(0, 1.5, 0)), shard);

            Snowball temp_snowball = e.getPlayer().launchProjectile(Snowball.class);
            Vector item_velocity = e.getPlayer().getLocation().getDirection();
            temp_snowball.remove();

            shard_item.setVelocity(item_velocity.add(new Vector(0, 0.5, 0)).multiply(0.75));
            shard_item.setPickupDelay(10);

            shard_item.setThrower(e.getPlayer().getUniqueId());
            new BukkitRunnable() {
                public void run() {
                    placeIcicleSphere(shard_item, e.getPlayer());
                }
            }
                    .runTaskLater(plugin, 60);
            return;

        }
        if (type == Material.SWEET_BERRIES) {
            int amp = 2;
            if(improved)
                amp = 4;
            addRecentUser(e.getPlayer().getUniqueId(), 20);
            e.setCancelled(true);
            e.getItem().setAmount(e.getItem().getAmount() - 1);
            e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "You eat the berries and feel a sudden burst in energy!");
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6 * 20, amp));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2 * 20, amp));
            return;
        }

        if (type == Material.BELL) {
            addRecentUser(e.getPlayer().getUniqueId(), 20);
            e.setCancelled(true);
            e.getItem().setAmount(e.getItem().getAmount() - 1);
            e.getPlayer().sendMessage(ChatColor.GOLD + "You ring the bell, and it leaps out of your hands!");

            ItemStack bell = new ItemStack(Material.BELL);

            ItemMeta meta = bell.getItemMeta();

            List<String> lore = new LinkedList<>();
            lore.add(e.getPlayer().getName() + System.currentTimeMillis());
            meta.setLore(lore);

            bell.setItemMeta(meta);

            Item bell_item = e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation()
                    .add(new Vector(0, 1.5, 0)), bell);

            Snowball temp_snowball = e.getPlayer().launchProjectile(Snowball.class);
            Vector item_velocity = e.getPlayer().getLocation().getDirection();
            temp_snowball.remove();

            bell_item.setVelocity(item_velocity.add(new Vector(0, 0.4, 0)).multiply(0.95));
            bell_item.setPickupDelay(10000);

            bell_item.setThrower(e.getPlayer().getUniqueId());

            Location sound_loc = e.getPlayer().getLocation();
            sound_loc.getWorld().playSound(sound_loc, Sound.BLOCK_BELL_USE, 20, 1);

            new BukkitRunnable() {
                public void run() {
                    bellBomb(bell_item.getLocation(), e.getPlayer());
                    bell_item.remove();
                }
            }
                    .runTaskLater(plugin, 40);
        }
    }
}
