package online.umbcraft.balls.levels.components.perks.median;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class HealthPerk extends PerkImplementation {

    @Override
    public void apply(Player p, JingleBall plugin) {
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(22);
        p.sendMessage(ChatColor.GREEN+this.getClass().getSimpleName());
    }

    @Override
    public void revoke(Player p, JingleBall plugin) {
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
    }
}
