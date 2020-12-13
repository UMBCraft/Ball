package online.umbcraft.balls;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class LuckyEventListener implements Listener {

    List<UUID> recent_users = new ArrayList<UUID>();
    private static Balls plugin;

    public LuckyEventListener(Balls p) {
        plugin = p;
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
            int new_damage = new_gun_meta.getDamage() + 10;

            if (new_damage <= 250) {
                new_gun_meta.setDamage(new_gun_meta.getDamage() + 10);
                gun.setItemMeta((ItemMeta) new_gun_meta);
            } else
                e.getPlayer().getInventory().remove(gun);

            float accuracy = 0.4f;
            int num_snowballs = 5;
            if(e.getPlayer().isSneaking()) {
                accuracy = 0.025f;
                num_snowballs = 3;
            }

            for (int i = 0; i < num_snowballs; i++) {
                Snowball snowball = e.getPlayer().launchProjectile(Snowball.class);
                Vector velocity = e.getPlayer().getLocation().getDirection();
                velocity.add(new Vector(
                        Math.random() * accuracy - (accuracy / 2),
                        Math.random() * accuracy - (accuracy / 2),
                        Math.random() * accuracy - (accuracy / 2)));
                velocity.multiply(2);
                snowball.setVelocity(velocity);
            }
        }

        if (type == Material.COAL) {
            addRecentUser(e.getPlayer().getUniqueId(), 20);
            e.setCancelled(true);
            e.getItem().setAmount(e.getItem().getAmount() - 1);
            e.getPlayer().sendMessage(ChatColor.GRAY + "A cloud of soot appears, blinding everyone around you!");
            Location l = e.getPlayer().getLocation();

            double circle_radius = 2.5;
            for (double a = 0; a < Math.PI*2; a+= Math.PI / (Math.PI * circle_radius)) {
                double x = Math.cos(a) * circle_radius;
                double z = Math.sin(a) * circle_radius;
                Location to_set = l.clone().add(x, 0, z);
                to_set.getWorld().spawnParticle(Particle.SQUID_INK, to_set, 100);
            }

            Collection<Entity> entities = l.getWorld().getNearbyEntities(l, 5, 5, 5);
            entities.remove(e.getPlayer());

            for (Entity ent : entities) {
                if (ent instanceof LivingEntity) {
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 2));
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 1));
                }
            }
        }

        if (type == Material.PRISMARINE_SHARD) {
            addRecentUser(e.getPlayer().getUniqueId(), 20);
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.BLUE + "You throw the Icicle, causing it to shatter!");
            e.getItem().setAmount(e.getItem().getAmount() - 1);

            ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD);
            ItemMeta meta = shard.getItemMeta();

            List<String> lore = new LinkedList<String>();
            lore.add(e.getPlayer().getName()+System.currentTimeMillis());
            meta.setLore(lore);

            shard.setItemMeta(meta);

            Item shard_item = e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation()
                    .add(new Vector(0,1.5,0)), shard);

            Snowball temp_snowball = e.getPlayer().launchProjectile(Snowball.class);
            Vector item_velocity = e.getPlayer().getLocation().getDirection();
            temp_snowball.remove();

            shard_item.setVelocity(item_velocity.add(new Vector(0,0.5,0)).multiply(0.75));
            shard_item.setPickupDelay(2);

            shard_item.setThrower(e.getPlayer().getUniqueId());
            new BukkitRunnable() {
                public void run() {
                    placeIcicleSphere(shard_item);
                }
            }
                    .runTaskLater(plugin, 100);
            return;

        }

        if (type == Material.SWEET_BERRIES) {

            addRecentUser(e.getPlayer().getUniqueId(), 20);
            e.setCancelled(true);
            e.getItem().setAmount(e.getItem().getAmount() - 1);
            e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "You eat the berries and feel a sudden burst in energy!");
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6 * 20, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2 * 20, 2));
            return;
        }
    }


    public static void placeIcicleSphere(Item icicle) {
        if(icicle == null)
            return;

        Location center = icicle.getLocation().getBlock().getLocation().add(new Vector(0.5,0.5,0.5));
        //plugin.getServer().broadcastMessage("center is at "+center.toString());

        icicle.remove();
        double sphere_radius = 2.5;
        center.getWorld().playSound(center,Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 20, 1f);
        int incr = 0;
        for (double i = 0; i <= Math.PI; i += Math.PI / (Math.PI * sphere_radius * 2)) {
            double circle_radius = Math.sin(i) * sphere_radius*2;
            double y = Math.cos(i) * sphere_radius*2;
            for (double a = 0; a < Math.PI*2; a+= Math.PI / (Math.PI * circle_radius * 2)) {
                double x = Math.cos(a) * circle_radius;
                double z = Math.sin(a) * circle_radius;
                Location to_set = center.clone().add(x, y, z);

                if(to_set.getBlock().getType() == Material.AIR) {

                    to_set.getBlock().setType(Material.ICE);
                    new BukkitRunnable() {
                        public void run() {
                            to_set.getBlock().setType(Material.AIR);
                            to_set.getWorld().playSound(to_set,Sound.BLOCK_BEEHIVE_DRIP, 10, 1f);
                        }
                    }
                    .runTaskLater(plugin, 400+(incr*2));
                    incr++;
                }
            }
        }



    }
}
