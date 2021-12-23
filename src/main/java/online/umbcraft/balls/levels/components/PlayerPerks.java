package online.umbcraft.balls.levels.components;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.perks.Perk;
import online.umbcraft.balls.levels.components.perks.PerkImplementation;
import online.umbcraft.balls.levels.components.perks.PerkType;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerPerks {

    final static public Set<Perk> PERKS;
    final static public Set<Perk> MAJOR_PERKS;
    final static public Set<Perk> MEDIAN_PERKS;
    final static public Set<Perk> MINOR_PERKS;

    static {
        MAJOR_PERKS = EnumSet.allOf(Perk.class).stream()
                .filter(i -> i.type == PerkType.MAJOR)
                .collect(Collectors.toSet());
        MEDIAN_PERKS = EnumSet.allOf(Perk.class).stream()
                .filter(i -> i.type == PerkType.MEDIAN)
                .collect(Collectors.toSet());
        MINOR_PERKS = EnumSet.allOf(Perk.class).stream()
                .filter(i -> i.type == PerkType.MINOR)
                .collect(Collectors.toSet());

        PERKS = EnumSet.allOf(Perk.class);
    }

    final private JingleBall plugin;

    final private Set<Perk> perks;

    public PlayerPerks(JingleBall plugin) {
        this.plugin = plugin;
        this.perks = EnumSet.noneOf(Perk.class);
    }

    public Set<Perk> getPerks() {
        return perks;
    }

    public boolean hasPerk(Perk perk) {
        return perks.contains(perk);
    }

    public void applyPerk(Player p, Perk perk) {
        perks.add(perk);
        perk.getImplementation().apply(p, plugin);
    }

    public void revokePerk(Player p, Perk perk) {
        perk.getImplementation().revoke(p, plugin);
        perks.remove(perk);
    }

    public void revokePerks(Player player) {
        for(Perk perk: perks) {
            perk.getImplementation().revoke(player, plugin);
        }
        perks.clear();
    }

}
