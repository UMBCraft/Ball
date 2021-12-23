package online.umbcraft.balls.tournament.components;

import online.umbcraft.balls.JingleBall;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tournament {

    final private JingleBall plugin;

    final private long TOURNEY_DURATION = 36000/* 00 */;

    final private TimedBossBar tournamentBar;
    private double multiplier;
    private TimedBossBar multiplierBar;
    private boolean isActive;

    public Tournament(JingleBall plugin, String name) {
        this.plugin = plugin;
        this.multiplier = 1;
        this.isActive = true;
        BossBar plainBar = plugin.getServer().createBossBar(
                name,
                BarColor.GREEN,
                BarStyle.SOLID
        );

        tournamentBar = new TimedBossBar(plugin, plainBar, name, TOURNEY_DURATION);
        tournamentBar.onFinish(() -> {
            isActive = false;
        });

        for (Player p : plugin.getServer().getOnlinePlayers())
            tournamentBar.addPlayer(p);




        // 5 minute 2x multi, 45 minutes away from the finish
        plugin.getExecutor().schedule(
                () -> {
                    String multTitle = "2x Point Multiplier";
                    BossBar plainMultBar = plugin.getServer().createBossBar(
                            multTitle,
                            BarColor.RED,
                            BarStyle.SOLID
                    );
                    multiplierBar = new TimedBossBar(plugin, plainMultBar, multTitle, 3000/* 00 */);
                    multiplier = 2;
                    multiplierBar.onFinish(() -> {
                        multiplierBar.getBar().removeAll();
                        multiplier = 1;
                    });
                    for (Player p : plugin.getServer().getOnlinePlayers())
                        multiplierBar.addPlayer(p);

                    // run 45 minutes from completion
                }, TOURNEY_DURATION - 27000/* 00 */, TimeUnit.MILLISECONDS);



        // 5 minute 2x multi, 25 minutes away from the finish
        plugin.getExecutor().schedule(
                () -> {
                    String multTitle = "2x Point Multiplier";
                    BossBar plainMultBar = plugin.getServer().createBossBar(
                            multTitle,
                            BarColor.RED,
                            BarStyle.SOLID
                    );
                    multiplierBar = new TimedBossBar(plugin, plainMultBar, multTitle, 3000/* 00 */);
                    multiplier = 2;
                    multiplierBar.onFinish(() -> {
                        multiplierBar.getBar().removeAll();
                        multiplier = 1;
                    });
                    for (Player p : plugin.getServer().getOnlinePlayers())
                        multiplierBar.addPlayer(p);

                    // run 25 minutes from completion
                }, TOURNEY_DURATION - 15000/* 00 */, TimeUnit.MILLISECONDS);


        // 5 minute 2.5x multi, 5 minutes away from the finish
        plugin.getExecutor().schedule(
                () -> {
                    String multTitle = "2.5x Point Multiplier";
                    BossBar plainMultBar = plugin.getServer().createBossBar(
                            multTitle,
                            BarColor.YELLOW,
                            BarStyle.SOLID
                    );
                    multiplierBar = new TimedBossBar(plugin, plainMultBar, multTitle, 3000/* 00 */);
                    multiplier = 2.5;
                    multiplierBar.onFinish(() -> {
                        multiplierBar.getBar().removeAll();
                        multiplier = 1;
                    });
                    for (Player p : plugin.getServer().getOnlinePlayers())
                        multiplierBar.addPlayer(p);

                    // run 12 minutes from completion
                }, TOURNEY_DURATION - 3000/* 00 */, TimeUnit.MILLISECONDS);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public boolean isActive() {
        return isActive;
    }

    public TimedBossBar[] getBossBars() {
        TimedBossBar[] bars = new TimedBossBar[]{tournamentBar, multiplierBar};
        return bars;
    }
}
