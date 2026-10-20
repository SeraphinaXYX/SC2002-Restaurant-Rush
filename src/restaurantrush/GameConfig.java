package restaurantrush;

import java.util.List;

/**
 * The base setting from the brief, plus the stage and seed for this run.
 *
 * @author Group XX
 */
public class GameConfig {
    public static final int MAX_TURNS = 18;
    public static final int TABLE_COUNT = 2;
    public static final List<Integer> ARRIVAL_TURNS = List.of(1, 4, 7, 10, 13, 16);
    public static final int TARGET_PAID = 4;
    public static final int TARGET_REVENUE_CENTS = 4500;
    public static final int TARGET_AVG_SATISFACTION = 60;

    private final int stage;
    private final long seed;

    public GameConfig(int stage, long seed) {
        this.stage = stage;
        this.seed = seed;
    }

    public int getStage() {
        return stage;
    }

    public boolean hasHost() {
        return stage >= 3;
    }

    public boolean hasHappyHour() {
        return stage >= 3;
    }

    public boolean hasCombos() {
        return stage >= 3;
    }

    public long getSeed() {
        return seed;
    }

    /**
     * The customer arriving at the start of this turn, or null if nobody arrives.
     * From Stage 2 the turn 7 and turn 13 arrivals are a VIP and a Critic,
     * so there are still six arrivals in total.
     */
    public Customer createArrival(int turn, int id) {
        if (!ARRIVAL_TURNS.contains(turn)) {
            return null;
        }
        if (stage >= 2 && turn == 7) {
            return new VIPCustomer(id);
        }
        if (stage >= 2 && turn == 13) {
            return new CriticCustomer(id);
        }
        return new RegularCustomer(id);
    }
}
