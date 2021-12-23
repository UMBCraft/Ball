package online.umbcraft.balls.levels.components.perks.major;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GroupedPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {

        String message = "You feel bold... you want more enemies!! (+def * enemies)";
        p.sendMessage(ChatColor.GREEN+message);
    }
}
