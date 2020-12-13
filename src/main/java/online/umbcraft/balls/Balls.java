package online.umbcraft.balls;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Balls extends JavaPlugin {

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

        this.getCommand("balls").setExecutor(new BallsCommands(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
