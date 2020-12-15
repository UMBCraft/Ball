package online.umbcraft.balls.scoreboard;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class IndividualScore {

    private Objective objective;
    private Scoreboard board;
    private Score score;
    private int index;
    private String content;
    private String tag;

    private final int MAX_CHAR_LIM = 40;

    public IndividualScore(Scoreboard brd,Objective obj,String cnt,int ndx,String tag) {
        this.board = brd;
        this.objective = obj;
        this.content = cnt;
        this.index = ndx;
        this.tag = tag;
        this.score = obj.getScore(cnt.substring(0,Math.min(MAX_CHAR_LIM, cnt.length())));
        this.score.setScore(index);

    }
    public void setContent(String to_set) {

        to_set = to_set.substring(0,Math.min(MAX_CHAR_LIM, to_set.length()));

        if(content.contentEquals(to_set))
            return;

        board.resetScores(content);
        score = objective.getScore(to_set);
        score.setScore(index);
        content = to_set;
    }
    public String getContent() {
        return content;
    }
    public void setIndex(int to_set) {
        index = to_set;
        score.setScore(to_set);
    }
    public String getTag() {
        return tag;
    }
    public boolean matchesTag(String to_match) {
        return tag.contentEquals(to_match);
    }
    public void remove() {
        board.resetScores(content);
    }
}
