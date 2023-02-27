package online.umbcraft.balls.levels;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.ExperienceBar;
import online.umbcraft.balls.levels.components.PlayerPerks;
import online.umbcraft.balls.levels.components.perks.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;
import java.util.stream.Collectors;

public class LevelingManager {

    final private JingleBall plugin;
    final private ExperienceBar experienceBar;
    final private Map<UUID, PlayerPerks> allPlPerks;

    final private static Set<Perk>[] perkOrdering = new Set[] {
            PlayerPerks.MINOR_PERKS,
            PlayerPerks.MEDIAN_PERKS,
            PlayerPerks.MINOR_PERKS,
            PlayerPerks.MAJOR_PERKS,
            PlayerPerks.MINOR_PERKS,
            PlayerPerks.MEDIAN_PERKS};

    public LevelingManager(JingleBall plugin) {
        this.plugin = plugin;
        this.experienceBar = new ExperienceBar(7);
        this.allPlPerks = new HashMap<>();
    }

    public void expAll() {
        for(Player p : plugin.getServer().getOnlinePlayers()) {
            if(!plugin.getSpectators().contains(p)) {
                addExp(p, 0.06);
            }
        }
    }

    public boolean hasPerk(Player player, Perk perk) {
        return allPlPerks.containsKey(player.getUniqueId())
                && allPlPerks.get(player.getUniqueId()).hasPerk(perk);
    }

    public ExperienceBar getBar() {
        return experienceBar;
    }

    // adds a perk for every level the player gained
    public void addExp(Player p, double amount) {
        int gainedLevels = experienceBar.incrementExp(p, amount);
        int level = p.getLevel() - (gainedLevels-1);
        PlayerPerks perks = allPlPerks.get(p.getUniqueId());
        if(perks == null) {
            perks = new PlayerPerks(plugin);
            allPlPerks.put(p.getUniqueId(), perks);
        }
        while(gainedLevels > 0) {

            Set<Perk> perkset = perkOrdering[level-2];

            PlayerPerks finalPerks = perks;
            List<Perk> dontHave = perkset.stream()
                    .filter(i -> !finalPerks.hasPerk(i))
                    .collect(Collectors.toList());

            if(dontHave.size() > 0)
                perks.applyPerk(p,dontHave
                    .get((int)(Math.random() * dontHave.size() ) ));

            gainedLevels--;
            level++;
        }
    }

    public void addPerk(Player player, Perk perk) {
        System.out.println("applying "+perk+" to player "+player.getName());
        PlayerPerks perks = allPlPerks.get(player.getUniqueId());
        if(perks == null) {
            perks = new PlayerPerks(plugin);
            allPlPerks.put(player.getUniqueId(), perks);
        }
        perks.applyPerk(player, perk);
    }

    public void removePerk(Player player, Perk perk) {
        allPlPerks.get(player.getUniqueId()).revokePerk(player, perk);
    }

    public void resetExp(Player p) {
        experienceBar.resetExp(p);
        allPlPerks.put(p.getUniqueId(), new PlayerPerks(plugin));
        for(Perk perk: PlayerPerks.PERKS)
            perk.getImplementation().revoke(p, plugin);

    }
    public void handleDeath(PlayerDeathEvent e) {
        e.setNewExp(0);
        e.setNewLevel(1);
        e.setDroppedExp(0);
        allPlPerks.put(e.getEntity().getUniqueId(), new PlayerPerks(plugin));
        for(Perk perk: PlayerPerks.PERKS)
            perk.getImplementation().revoke(e.getEntity(), plugin);
    }

}
