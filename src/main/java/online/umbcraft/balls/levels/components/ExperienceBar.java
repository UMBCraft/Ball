package online.umbcraft.balls.levels.components;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;


public class ExperienceBar {

    final private int MAX_LEVEL;

    public ExperienceBar(int maxLevel) {
        this.MAX_LEVEL = maxLevel;
    }

    // returns the amount of exp needed to fill the given level
    private double expCostFunction(int level) {
        if (level <= 0)
            return 0;
        return 10 + level * 5;
    }

    // returns the amount of exp needed to go from lvl 0 to the given level
    // uses the integral of the cost function for fast calculation
    private double expCumulativeCost(int level) {
        return level * 25 + level * level * 5;
    }

    // returns the amount of exp needed to go from lvl 0 to the given level
    // computes manually from the cost function; no need to get the integral
    private double expCumulativeCostManual(int level) {
        double total = 0;
        for (int i = 1; i < level; i++)
            total += expCostFunction(i);

        return total;
    }

    // reset a players xp bar
    public void resetExp(Player p) {
        p.setExp(0);
        p.setLevel(1);
    }

    public void resetExp(PlayerDeathEvent e) {
        System.out.println("handing death??");
        e.setNewExp(0);
        e.setNewLevel(1);
        e.setDroppedExp(0);
    }

    // increase a players xp bar by feeding them game xp
    // returns true if the player increased in level
    public int incrementExp(Player p, double amount) {

        int level = p.getLevel();

        if (level >= MAX_LEVEL)
            return 0;

        float barPercent = p.getExp();
        double levelCost = expCostFunction(level);
        double stillNeed = (1 - barPercent) * levelCost;


        // if the player did not gain enough xp to level up, update the xp bar
        if (amount < stillNeed) {
            float gained = (float) (amount / levelCost);
            p.setExp(barPercent + gained);
            return 0;
        }

        // if player gained enough xp to level up, recursively try to level them again
        p.setLevel(level + 1);
        p.setExp(0);
        return 1 + incrementExp(p, amount - stillNeed);

    }
}
