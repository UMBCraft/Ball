package online.umbcraft.balls.levels.components.perks.major;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ImmunePerk extends PerkImplementation {
    @Override
    public void apply(Player p, JingleBall plugin) {
        String message = "Sickness leaves your body. You are safe. (+immunity to effects)";
        p.sendMessage(ChatColor.GREEN+this.getClass().getSimpleName());
    }
}
