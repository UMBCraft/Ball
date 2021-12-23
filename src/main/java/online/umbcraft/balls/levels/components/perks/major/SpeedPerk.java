package online.umbcraft.balls.levels.components.perks.major;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        p.addPotionEffect(
                new PotionEffect(PotionEffectType.SPEED, 20 * 200000, 0));

        p.sendMessage(ChatColor.GREEN+this.getClass().getSimpleName());
    }

    public void revoke(Player p, JingleBall plugin) {
        p.removePotionEffect(PotionEffectType.SPEED);
    }
}
