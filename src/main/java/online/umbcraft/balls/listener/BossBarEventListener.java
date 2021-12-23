package online.umbcraft.balls.listener;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.tournament.TournamentManager;
import online.umbcraft.balls.tournament.components.TimedBossBar;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BossBarEventListener implements Listener {

    final private JingleBall plugin;
    final private TournamentManager manager;

    public BossBarEventListener(JingleBall plugin, TournamentManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent e) {
        if(manager.hasActive()) {
            // always add main bar if it is active
            TimedBossBar[] bars = manager.getBars();
            bars[0].addPlayer(e.getPlayer());

            // add multiplier bar if it is currently running
            if(bars[1] != null && bars[1].isRunning())
                bars[1].addPlayer(e.getPlayer());
        }
    }

}
