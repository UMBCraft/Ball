package online.umbcraft.balls.tournament.components;

import online.umbcraft.balls.JingleBall;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimedBossBar {


    final private JingleBall plugin;

    final private BossBar bossBar;
    final private String basename;

    final private long startTime;
    final private long DURATION;
    private boolean running;

    private ScheduledFuture<?> barTask;
    private Runnable onFinish = null;

    public TimedBossBar(JingleBall plugin, BossBar bar, String basename, long milis) {
        this.bossBar = bar;
        this.basename = basename;
        this.plugin = plugin;
        this.DURATION = milis;
        this.startTime = System.currentTimeMillis();
        this.running = true;


        // refresh the boss bar once every second
        // players will still see the bar after the time ends, will need to be removed w/ the runnable
        barTask = plugin.getExecutor().scheduleAtFixedRate(() -> {

            // update time left in title
            long remainingMilis = DURATION - (System.currentTimeMillis() - startTime);

            if (remainingMilis > 0) {
                String remainingTime = milisToHHMMSS(remainingMilis);
                bossBar.setTitle(basename + " (" + remainingTime + ")");

                // update progress bar
                double fillPercent = (double) remainingMilis / DURATION;
                bossBar.setProgress(fillPercent);
            } else {
                bossBar.setProgress(0);
                bossBar.setTitle(basename + " (ENDED)");
                running = false;
                // run custom runnable when bossbar ends
                if (onFinish != null) {
                    onFinish.run();
                }
                barTask.cancel(true);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private static String milisToHHMMSS(long milis) {
        Duration timeLeft = Duration.ofMillis(milis);
        String hhmmss = String.format("%02d:%02d:%02d",
                timeLeft.toHours(), timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
        return hhmmss;
    }

    public boolean isRunning() {
        return running;
    }

    public void addPlayer(Player p) {
        bossBar.addPlayer(p);
    }

    public BossBar getBar() {
        return bossBar;
    }

    public void onFinish(Runnable runnable) {
        onFinish = runnable;
    }
}
