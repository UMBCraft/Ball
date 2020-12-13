package online.umbcraft.balls;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;

public class LuckyEventListener implements Listener {

    Balls plugin;
    public LuckyEventListener(Balls p) {
        plugin = p;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onUseItem(PlayerInteractEvent e) {
        if(e.getItem().getType() == Material.SNOWBALL)
            return;

        if(e.getAction() != Action.RIGHT_CLICK_AIR
        && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Material type = e.getItem().getType();
        e.getItem().setAmount(e.getItem().getAmount()-1);

        if(type == Material.COAL) {
            e.getPlayer().sendMessage(ChatColor.GRAY+"A cloud of soot appears, blinding everyone around you!");
            Location l = e.getPlayer().getLocation();

            l.getWorld().spawnParticle(Particle.SQUID_INK, l , 100);

            Collection<Entity> entities = l.getWorld().getNearbyEntities(l,5,5,3);
            entities.remove(e.getPlayer());

            for(Entity ent: entities) {
                if(ent instanceof LivingEntity) {
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.WITHER,5*20,2));
                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,10*20,1));
                }
            }
        }
        if(type == Material.SWEET_BERRIES) {
            e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE+"You eat the berries and feel a sudden burst in energy!");
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,6*20,2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,2*20,2));
            return;
        }
    }
}
