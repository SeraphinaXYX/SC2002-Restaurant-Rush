package restaurantrush;

/**
 * Happy Hour pricing policy. The manager can switch it on once per game at
 * the start of a turn; it covers that turn and the next one.
 *
 * @author Group XX
 */
public class HappyHour {
    private static final int DURATION_TURNS = 2;
    private static final int PRICE_PERCENT = 80;
    public static final int RECOVERY = 15;

    private boolean used;
    private int turnsRemaining;

    public boolean isAvailable() {
        return !used;
    }

    public boolean isActive() {
        return turnsRemaining > 0;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /** Returns false if Happy Hour was already used this game. */
    public boolean activate() {
        if (used) {
            return false;
        }
        used = true;
        turnsRemaining = DURATION_TURNS;
        return true;
    }

    /** Called once at the end of every turn. */
    public void endTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    /** 20% off a price that already includes any customer discount. */
    public int apply(int priceCents) {
        if (!isActive()) {
            return priceCents;
        }
        return (priceCents * PRICE_PERCENT + 50) / 100;
    }
}
