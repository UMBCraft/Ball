package online.umbcraft.balls;

import java.util.*;

public class ScoreManager {

    private final int TOP_AMOUNT;
    private final int DEFAULT_SCORE;
    private Map<UUID, ScoreNode> all_scores;
    private LinkedList<ScoreNode> top_scores;
    private PriorityQueue<ScoreNode> other_scores;


    public ScoreManager() {
        TOP_AMOUNT = 10;
        DEFAULT_SCORE = 0;

        all_scores = new HashMap<>(TOP_AMOUNT*2);
        top_scores = new LinkedList<ScoreNode>();
        other_scores = new PriorityQueue<>();
    }

    public ScoreManager(int top_amount, int default_score) {
        TOP_AMOUNT = top_amount;
        DEFAULT_SCORE = default_score;

        all_scores = new HashMap<>(TOP_AMOUNT * 2);
        top_scores = new LinkedList<ScoreNode>();
        other_scores = new PriorityQueue<>();
    }


    public synchronized void addPlayer(UUID uuid, String name) {
        ScoreNode new_node = new ScoreNode(name, DEFAULT_SCORE);
        all_scores.put(uuid, new_node);
        mergeToQueue(new_node);
    }

    public synchronized void removePlayer(UUID uuid) {
        ScoreNode p_score = all_scores.get(uuid);

        if (!p_score.isTop())
            other_scores.remove(p_score);
        else {
            outOfTop(p_score);
            if (other_scores.peek() != null) {
                intoTop(other_scores.poll());
            }
        }
    }

    public synchronized int adjustPlayerScore(UUID uuid, int amount_by) {

        ScoreNode p_score = all_scores.get(uuid);
        int result = p_score.adjustScore(amount_by);
        if (p_score.is_top) {
            outOfTop(p_score);

            ScoreNode replacement = other_scores.poll();
            if (replacement != null)
                intoTop(replacement);

        } else {
            other_scores.remove(p_score);
        }

        mergeToQueue(p_score);
        return result;
    }

    public synchronized int getTopAmount() {
        return TOP_AMOUNT;
    }

    public String toString() {

        String to_return = "Top scores: -----(smallest at spot 0)-----\n";
        int iter = 0;

        for (Iterator<ScoreNode> s = top_scores.iterator(); s.hasNext(); ) {
            ScoreNode sn = s.next();
            to_return += ("top queue[" + iter + "]: " + sn.getName() + " -> " + sn.getScore() + " " + sn.isTop() + "\n");
            iter++;
        }
        iter = 0;
        to_return += "Other scores: -----(largest at spot 0)-----\n";
        for (ScoreNode sn : other_scores) {
            to_return += ("other queue[" + iter + "]: " + sn.getName() + " - " + sn.getScore() + " " + sn.isTop() + "\n");
            iter++;
        }
        return to_return + "-------------------------";
    }

    private synchronized void outOfTop(ScoreNode to_rem) {
        if (top_scores.remove(to_rem))
            to_rem.setTopStatus(false);
    }

    private synchronized void intoTop(ScoreNode to_add) {
        int index = 0;
        for (ScoreNode node : top_scores)
            if (to_add.compareTo(node) >= 0) {
                top_scores.add(index, to_add);
                return;
            }
        top_scores.add(to_add);
        to_add.setTopStatus(false);
    }

    private synchronized boolean mergeToQueue(ScoreNode to_merge) {

        intoTop(to_merge);

        if (top_scores.size() <= TOP_AMOUNT) { // if the top scores aren't at capacity, defaults to top score
            to_merge.setTopStatus(true);
            return true;
        }

        ScoreNode demoted = top_scores.pop(); // move new lowest top score to the remaining scores queue
        other_scores.add(demoted);

        demoted.setTopStatus(false);

        if (demoted == to_merge)
            return false;

        // update status for whether score is a top score or not
        to_merge.setTopStatus(true);

        return true;
    }

    private class ScoreNode implements Comparable<ScoreNode> {

        private int score;
        private String name;
        private boolean is_top;

        private ScoreNode(String name) {
            this.name = name;
            this.score = 0;
        }

        private ScoreNode(String name, int score) {
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

        private boolean isTop() {
            return is_top;
        }

        private void setTopStatus(boolean top_status) {
            is_top = top_status;
        }

        @Override
        public int compareTo(ScoreNode o) {
            if (!(o instanceof ScoreNode))
                return 0;

            return ((ScoreNode) o).score - score;
        }

        @Override
        public String toString() {
            return "\"" + name + ": " + score + " (" + is_top + ")\"";
        }

    }
}


