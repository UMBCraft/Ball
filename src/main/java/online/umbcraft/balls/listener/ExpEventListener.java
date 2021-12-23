package online.umbcraft.balls.listener;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.levels.LevelingManager;
import online.umbcraft.balls.levels.components.perks.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ExpEventListener implements Listener {


    final private JingleBall plugin;
    final private LevelingManager lm;

    final private double XP_PER_KILL = 5.1;
    final private double XP_PER_LEVEL = 1.55 ;

    public ExpEventListener(JingleBall plugin, LevelingManager lm) {
        this.plugin = plugin;
        this.lm = lm;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNaturalXPChange(PlayerExpChangeEvent e) {
        e.setAmount(0);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent e) {
        lm.resetExp(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent e) {
        //lm.setExp(e.getPlayer(), 0);       <- doesn't work!??!? oh well, don't need it

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKillPlayer(PlayerDeathEvent e) {

        System.out.println("calling death event for exp listener");

        Player died = e.getEntity();
        Player killer = died.getKiller();

        lm.handleDeath(e);

        if(killer == null)
            return;


        double toGain = XP_PER_KILL + (died.getLevel()-1) * XP_PER_LEVEL;
        if(plugin.getLevelingManager().hasPerk(killer, Perk.EXP))
            toGain *= 1.25;
        lm.addExp(killer, toGain);
    }
}
