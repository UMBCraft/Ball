package online.umbcraft.balls;

import online.umbcraft.balls.listener.BallsCommands;
import online.umbcraft.balls.listener.LuckyEventListener;
import online.umbcraft.balls.listener.PlayerEventListener;
import online.umbcraft.balls.listener.WandEventListener;
import online.umbcraft.balls.scoreboard.ScoreManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class JingleBall extends JavaPlugin {

    private ScoreManager scores;

    @Override
    public void onEnable() {

        scores = new ScoreManager(10,0);
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists())
            this.saveDefaultConfig();

        PlayerEventListener player_listener = new PlayerEventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(player_listener, this);

        WandEventListener wand_listener = new WandEventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(wand_listener, this);

        LuckyEventListener special_listener = new LuckyEventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(special_listener, this);

        this.getCommand("jingle").setExecutor(new BallsCommands(this));
    }

    public ScoreManager getScores() {
        return scores;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
