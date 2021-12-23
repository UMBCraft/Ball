package online.umbcraft.balls.scoreboard.components;

import org.bukkit.ChatColor;

import java.util.UUID;

public class BallsScoreboard extends IndividualScoreboard {

    public BallsScoreboard(UUID player) {
        super(player);

        String r = ChatColor.RED + "";
        String g = ChatColor.GREEN + "";
        setTitle(r + "J" + g + "i" + r + "n" + g + "g" + r + "l" + g + "e " + r + "B" + g + "a" + r + "l" + g + "l" + r + "s" + g + "!");

        int index = 99;
        insertRow(ChatColor.GRAY+"-------------------- ", index--, "first-empty");
        insertRow(ChatColor.GOLD + "" + ChatColor.BOLD + "Top Scores", index--, "subtitle");

        for (int i = 1; i <= 10; i++) {
            insertRow(ChatColor.GREEN + "" + i + ". ", index--, "score-" + i);
        }

        insertRow("                    ", index--, "empty-bot1");
        insertRow(ChatColor.YELLOW + "" + ChatColor.BOLD + "Your Score: "+ChatColor.WHITE+"0", index--, "your-score");
        insertRow(ChatColor.GRAY+"--------------------", index--, "last-empty");
    }

    public void setTitle(String new_title) {
        super.setTitle(new_title);
    }

    public void setTopRanked(String name, int rank, int new_score) {
        String name_content;
        if (name != null)
            name_content = ChatColor.GREEN + "" + rank + ". " + ChatColor.WHITE+ name + " - "+ChatColor.DARK_AQUA + new_score;
        else
            name_content = ChatColor.GREEN + "" + rank+".";

        getByTag("score-" + rank).setContent(name_content);
    }

    public void setPersonalScore(int new_score) {
        getByTag("your-score").setContent(ChatColor.YELLOW + "" + ChatColor.BOLD + "Your Score: "+ChatColor.WHITE+new_score);
    }

}
