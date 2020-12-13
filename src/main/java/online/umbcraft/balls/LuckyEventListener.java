package online.umbcraft.balls;

import org.bukkit.*;
import org.bukkit.entity.Entity;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class LuckyEventListener implements Listener {

    List<UUID> recent_users = new ArrayList<UUID>();
    private Balls plugin;

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
        if (e.getItem().getType() == Material.SNOWBALL)
            return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Material type = e.getItem().getType();
        if (recent_users.contains(e.getPlayer().getUniqueId()))
            return;



        if (type == Material.COAL) {
            addRecentUser(e.getPlayer().getUniqueId(), 20);
            e.setCancelled(true);
            e.getItem().setAmount(e.getItem().getAmount() - 1);
            e.getPlayer().sendMessage(ChatColor.GRAY + "A cloud of soot appears, blinding everyone around you!");
            Location l = e.getPlayer().getLocation();

            l.getWorld().spawnParticle(Particle.SQUID_INK, l, 100);

            Collection<Entity> entities = l.getWorld().getNearbyEntities(l, 5, 5, 3);
            entities.remove(e.getPlayer());

            for (Entity ent : entities) {
                if (ent instanceof LivingEntity) {
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 2));
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 1));
                }
            }
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
    }
}
