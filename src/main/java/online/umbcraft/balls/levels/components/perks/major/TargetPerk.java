package online.umbcraft.balls.levels.components.perks.major;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TargetPerk extends PerkImplementation {
    @Override
    public void apply(Player p, JingleBall plugin) {

        String message = "Your precise aim lets you take on the largest foes (+damage mult. on snowballs)";
        p.sendMessage(ChatColor.GREEN+message);
    }
}
