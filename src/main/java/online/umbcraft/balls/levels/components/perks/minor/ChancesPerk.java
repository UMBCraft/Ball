package online.umbcraft.balls.levels.components.perks.minor;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChancesPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {

        String message = "You see a rainbow. You feel... luckier? (+luck)";
        p.sendMessage(ChatColor.GREEN+message);
    }

}
