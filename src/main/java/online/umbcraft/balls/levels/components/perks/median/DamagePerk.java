package online.umbcraft.balls.levels.components.perks.median;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DamagePerk extends PerkImplementation {
    @Override
    public void apply(Player p, JingleBall plugin) {
        String message = "Your arms feel stronger... time to chunk a snowball. (+damage to snowballs)";
        p.sendMessage(ChatColor.GREEN+message);
    }
}
