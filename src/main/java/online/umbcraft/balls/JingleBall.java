package online.umbcraft.balls;

import online.umbcraft.balls.listener.JingleCommands;
import online.umbcraft.balls.listener.LuckyEventListener;
import online.umbcraft.balls.listener.PlayerEventListener;
import online.umbcraft.balls.listener.WandEventListener;
import online.umbcraft.balls.scoreboard.ScoreManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class JingleBall extends JavaPlugin {

    final private ScoreManager scores;
    final private Set<UUID> spectators;
    final private Set<UUID> attemptedSpectators;

    public JingleBall() {
        super();
        spectators = new HashSet<>();
        attemptedSpectators = new HashSet<>();
        scores = new ScoreManager(10,0);
    }

    public Set<UUID> getSpectators() {
        return spectators;
    }

    public Set<UUID> getAttemptedSpectators() {
        return spectators;
    }

    public boolean isSpectator(UUID player) {
        return spectators.contains(player);
    }

    @Override
    public void onEnable() {

        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists())
            this.saveDefaultConfig();

        PlayerEventListener player_listener = new PlayerEventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(player_listener, this);

        WandEventListener wand_listener = new WandEventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(wand_listener, this);

        LuckyEventListener special_listener = new LuckyEventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(special_listener, this);

        this.getCommand("jingle").setExecutor(new JingleCommands(this));
    }

    public ScoreManager getScores() {
        return scores;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
