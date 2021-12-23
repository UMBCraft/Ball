package online.umbcraft.balls.levels.components.perks.minor;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SoundsPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        String message = "The sounds of the forest echo through you. (+sounds)";
        p.sendMessage(ChatColor.GREEN + message);
    }
}
