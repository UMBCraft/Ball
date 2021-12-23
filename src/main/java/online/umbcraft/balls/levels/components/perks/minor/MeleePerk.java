package online.umbcraft.balls.levels.components.perks.minor;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MeleePerk extends PerkImplementation {
    @Override
    public void apply(Player p, JingleBall plugin) {
        String message = "You realize fists speak as loudly as snowballs. (+melee)";
        p.sendMessage(ChatColor.GREEN + message);
    }
}
