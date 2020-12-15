package online.umbcraft.balls.scoreboard;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IndividualScoreboard {

    protected Scoreboard board;
    protected UUID player;
    protected Objective objective;
    protected List<IndividualScore> scores;

    public IndividualScoreboard(UUID player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.board = manager.getNewScoreboard();
        this.player = player;
        scores = new ArrayList<IndividualScore>();

        objective = board.registerNewObjective(
                StringUtils.substring(player.toString(),0,15)
                ,"dummy","placeholder");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setTitle(String to_set) {
        objective.setDisplayName(to_set);
    }

    public String getContentOf(String tag) {
        return getByTag(tag).getContent();
    }

    public IndividualScore insertRow(String content, int index, String tag) {
        IndividualScore new_score = new IndividualScore(board,objective,content,index,tag);
        scores.add(new_score);
        return new_score;
    }

    public IndividualScore getByTag(String tag) {
        for(IndividualScore s : scores) {
            if(s.matchesTag(tag))
                return s;
        }
        return null;
    }

    public void remove() {
        objective.unregister();
    }
}