package online.umbcraft.balls.levels;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.components.ExperienceBar;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class LevelingManager {

    final private JingleBall plugin;
    final private ExperienceBar experienceBar;

    public LevelingManager(JingleBall plugin) {
        this.plugin = plugin;
        this.experienceBar = new ExperienceBar(10);
    }

    public ExperienceBar getBar() {
        return experienceBar;
    }

    public void setExp(Player p, int amount) {
        experienceBar.resetExp(p);
        addExp(p, amount);
    }
    public void addExp(Player p, double amount) {
        boolean gainedLevel = experienceBar.incrementExp(p, amount);
    }
    public void handleDeath(PlayerDeathEvent e) {
        experienceBar.resetExp(e);
    }

}
