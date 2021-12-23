package online.umbcraft.balls;

import online.umbcraft.balls.commands.JingleCommands;
import online.umbcraft.balls.levels.LevelingManager;
import online.umbcraft.balls.listener.*;
import online.umbcraft.balls.scoreboard.ScoreManager;
import online.umbcraft.balls.tournament.TournamentManager;
import online.umbcraft.balls.tournament.components.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class JingleBall extends JavaPlugin {

    final private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    final private ScoreManager scoreManager;
    final private LevelingManager levelManager;
    final private TournamentManager tournamentManager;

    final private Set<UUID> spectators;
    final private Set<UUID> attemptedSpectators;

    public JingleBall() {
        super();

        spectators = new HashSet<>();
        attemptedSpectators = new HashSet<>();

        scoreManager = new ScoreManager(10, 0);
        levelManager = new LevelingManager(this);
        tournamentManager = new TournamentManager(this);
    }

    public TournamentManager getTournamentManager() {
        return tournamentManager;
    }

    public LevelingManager getLevelingManager() {
        return levelManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }

    public Set<UUID> getSpectators() {
        return spectators;
    }

    public Set<UUID> getAttemptedSpectators() {
        return attemptedSpectators;
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

        ExpEventListener exp_listener = new ExpEventListener(this, levelManager);
        Bukkit.getServer().getPluginManager().registerEvents(exp_listener, this);

        BossBarEventListener bossbar_listener = new BossBarEventListener(this, tournamentManager);
        Bukkit.getServer().getPluginManager().registerEvents(bossbar_listener, this);

        this.getCommand("jingle").setExecutor(new JingleCommands(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
