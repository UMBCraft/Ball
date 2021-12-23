package online.umbcraft.balls.levels.components.perks.median;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisibilityPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        p.addPotionEffect(
                new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 200000, 0));

        String message = "Your body grows cold as you blend in with the snow. (+invisibility)";
        p.sendMessage(ChatColor.GREEN + message);

    }

    public void revoke(Player p, JingleBall plugin) {
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
}