package online.umbcraft.balls.levels.components.perks.minor;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ExpPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        String message = "You feel an eagerness to grow. (+exp rate)";
        p.sendMessage(ChatColor.GREEN+message);
    }
}
