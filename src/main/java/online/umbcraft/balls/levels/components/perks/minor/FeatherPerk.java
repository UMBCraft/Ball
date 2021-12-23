package online.umbcraft.balls.levels.components.perks.minor;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FeatherPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {

        String message = "You find yourself floating as you fall. (-fall dmg)";
        p.sendMessage(ChatColor.GREEN+message);
    }
}
