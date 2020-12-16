package online.umbcraft.balls.scoreboard;

import org.bukkit.entity.Player;

import java.util.*;

public class ScoreManager {

    private final int TOP_AMOUNT;
    private final int DEFAULT_SCORE;

    private HashMap<UUID, ScoreNode> unsorted_scores = new HashMap<>();
    private TreeMap<String, ScoreNode> sorted_scores = new TreeMap<>();

    private HashMap<UUID, BallsScoreboard> scoreboards = new HashMap<>();

    private UUID[] top_uuids;
    private int[] top_scores;

    public ScoreManager() {
        TOP_AMOUNT = 10;
        DEFAULT_SCORE = 0;

        top_uuids = new UUID[TOP_AMOUNT];
        top_scores = new int[TOP_AMOUNT];
    }

    public ScoreManager(int top_amount, int default_score) {
        TOP_AMOUNT = top_amount;
        DEFAULT_SCORE = default_score;

        top_uuids = new UUID[TOP_AMOUNT];
        top_scores = new int[TOP_AMOUNT];
    }

    public synchronized UUID getTopPlayer() {
        return sorted_scores.firstEntry().getValue().getUUID();
    }

    public synchronized void addPlayer(Player p) {
        UUID uuid = p.getUniqueId();
        String name = p.getName();
        scoreboards.put(uuid, new BallsScoreboard(uuid));
        p.setScoreboard(scoreboards.get(uuid).board);

        if (unsorted_scores.containsKey(uuid))
            return;

        ScoreNode new_node = new ScoreNode(uuid, name, DEFAULT_SCORE);

        unsorted_scores.put(uuid, new_node);
        sorted_scores.put(createKey(uuid, new_node), new_node);
        giveBoardsTop(uuid);
        updateBoardsTop();
    }

    public synchronized void removePlayer(UUID uuid) {
        scoreboards.remove(uuid).remove();
        ScoreNode to_rem = unsorted_scores.remove(uuid);
        sorted_scores.remove(createKey(uuid, to_rem));
        updateBoardsTop();
        return;
    }

    private synchronized void updateBoardsTop() {
        int rank = 1;
        for (String s : sorted_scores.descendingKeySet()) {
            if (rank >= TOP_AMOUNT)
                return;

            ScoreNode sn = sorted_scores.get(s);

            if (sn != null) {
                System.out.println("rank "+rank+" is not null");
                if (top_uuids[rank - 1] == null
                        || !top_uuids[rank - 1].equals(sn.getUUID())
                        || top_scores[rank - 1] != (sn.getScore())) {
                    System.out.println("updating scoreboard - rank "+rank+" is now "+sn.getName()+" with score "+sn.getScore());
                    for (BallsScoreboard scb : scoreboards.values()) {
                        scb.setTopRanked(sn.getName(), rank, sn.getScore());
                    }
                    top_uuids[rank-1] = sn.getUUID();
                    top_scores[rank-1] = sn.getScore();
                }
            } else {
                System.out.println("rank "+rank+" IS null");
                if (top_uuids[rank - 1] != null) {

                    System.out.println("updating scoreboard - rank "+rank+" is now null");
                    for (BallsScoreboard scb : scoreboards.values())
                        scb.setTopRanked(null, rank, -1);
                    top_uuids[rank-1] = null;
                }
            }
            rank++;
        }

        while(rank < TOP_AMOUNT) {
            if (top_uuids[rank - 1] != null) {

                System.out.println("updating scoreboard - rank "+rank+" is now null");
                for (BallsScoreboard scb : scoreboards.values())
                    scb.setTopRanked(null, rank, -1);
                top_uuids[rank-1] = null;
            }
            rank++;
        }

    }

    public synchronized void giveBoardsTop(UUID player) {

        BallsScoreboard scb = scoreboards.get(player);

        int rank = 1;
        for (String s : sorted_scores.descendingKeySet()) {
            if (rank >= TOP_AMOUNT)
                return;
            ScoreNode sn = sorted_scores.get(s);
            scb.setTopRanked(sn.getName(), rank, sn.getScore());
            rank++;
        }
    }

    public synchronized int adjustPlayerScore(UUID uuid, int amount_by) {

        ScoreNode p_score = unsorted_scores.get(uuid);
        sorted_scores.remove(createKey(uuid, p_score));

        p_score.adjustScore(amount_by);
        sorted_scores.put(createKey(uuid, p_score), p_score);

        scoreboards.get(uuid).setPersonalScore(p_score.getScore());
        updateBoardsTop();
        return p_score.getScore();
    }

    public synchronized void setPlayerScore(UUID uuid, int to_set) {

        ScoreNode p_score = unsorted_scores.get(uuid);
        adjustPlayerScore(uuid, -p_score.getScore());

    }

    public synchronized int getPlayerScore(UUID uuid) {
        return unsorted_scores.get(uuid).getScore();
    }

    public synchronized int getTopAmount() {
        return TOP_AMOUNT;
    }

    public String toString() {

        String to_return = "Top scores: -----(largest at spot 0)-----\n";
        int iter = 0;

        for (String s : sorted_scores.descendingKeySet()) {
            ScoreNode sn = sorted_scores.get(s);
            to_return += ("top queue[" + iter + "]: " + sn.getName() + " -> " + sn.getScore() + " -> " + s + "\n");
            iter++;
        }

        return to_return + "-------------------------";
    }

    private String createKey(UUID uuid, ScoreNode score) {
        return String.format("%010d|%s", score.getScore(), uuid.toString());
    }

    private class ScoreNode implements Comparable<ScoreNode> {

        private int score;
        private String name;
        private UUID uuid;

        private ScoreNode(UUID uuid, String name) {
            this.name = name;
            this.uuid = uuid;
            this.score = 0;
        }

        private ScoreNode(UUID uuid, String name, int score) {
            this.uuid = uuid;
            this.name = name;
            this.score = score;
        }

        private int adjustScore(int by) {
            score += by;
            return score;
        }

        private int getScore() {
            return score;
        }

        private String getName() {
            return name;
        }

        private UUID getUUID() {
            return uuid;
        }

        @Override
        public int compareTo(ScoreNode o) {
            if (!(o instanceof ScoreNode))
                return 0;

            return score - ((ScoreNode) o).score;
        }

        @Override
        public String toString() {
            return "\"" + name + ": " + score;
        }

    }
}


