package online.umbcraft.balls.tournament;

import online.umbcraft.balls.JingleBall;
import online.umbcraft.balls.tournament.components.TimedBossBar;
import online.umbcraft.balls.tournament.components.Tournament;

import java.util.List;

public class TournamentManager {

    final private JingleBall plugin;
    private Tournament activeTournament;

    public TournamentManager(JingleBall plugin) {
        this.plugin = plugin;
    }

    public boolean hasActive() {
        if(activeTournament != null && activeTournament.isActive())
            return true;
        return false;
    }

    public TimedBossBar[] getBars() {
        return activeTournament.getBossBars();
    }
    public void startTournament() {
        if(activeTournament != null && activeTournament.isActive())
            return;
        clearTournament();

        activeTournament = new Tournament(plugin, "Jingle Ball Bash");
    }

    public void clearTournament() {
        if(activeTournament == null || activeTournament.isActive()) {
            return;
        }

        activeTournament.getBossBars()[0].getBar().removeAll();
        activeTournament = null;

    }
}
