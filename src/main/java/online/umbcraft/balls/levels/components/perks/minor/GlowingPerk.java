package online.umbcraft.balls.levels.components.perks.minor;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GlowingPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        String message = "Enemies you face reveal themselves to you. (+glow on snowballs)";
        p.sendMessage(ChatColor.GREEN+message);
    }
}
