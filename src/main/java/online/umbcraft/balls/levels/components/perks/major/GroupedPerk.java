package online.umbcraft.balls.levels.components.perks.major;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GroupedPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        p.sendMessage(ChatColor.GREEN+this.getClass().getSimpleName());
    }
}
