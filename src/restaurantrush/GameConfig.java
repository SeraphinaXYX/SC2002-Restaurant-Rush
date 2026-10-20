package restaurantrush;

import java.util.List;

/**
 * The base setting from the brief, plus the seed for this run.
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

    private final long seed;

    public GameConfig(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    /** The customer arriving at the start of this turn, or null if nobody arrives. */
    public Customer createArrival(int turn, int id) {
        if (!ARRIVAL_TURNS.contains(turn)) {
            return null;
        }
        return new RegularCustomer(id, turn);
    }
}
