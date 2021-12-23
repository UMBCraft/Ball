package online.umbcraft.balls.levels.components.perks.median;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SlowPerk extends PerkImplementation {
    @Override
    public void apply(Player p, JingleBall plugin) {

        String message = "You feel an urge to rip and gouge throats. (+slow on punch)";
        p.sendMessage(ChatColor.GREEN+message);
    }
}
