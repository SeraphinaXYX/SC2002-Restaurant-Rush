package restaurantrush;

import java.util.Random;

/**
 * Food critic: impatient (loses 12 satisfaction per waiting turn) and pays
 * full price. Always orders the most expensive dish on the menu, and reacts
 * to how long the dish took after ordering: served within 2 turns gives +5,
 * 3 or more turns gives -10.
 *
 * @author Group XX
 */
public class CriticCustomer extends Customer {
    private static final int FAST_SERVICE_TURNS = 2;
    private static final int FAST_BONUS = 5;
    private static final int SLOW_PENALTY = 10;

    public CriticCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLoss() {
        return 12;
    }

    @Override
    public int applyDiscount(int priceCents) {
        return priceCents;
    }

    // A critic judges the kitchen on its priciest single dish, so never a combo.
    // Ties keep the earlier dish, so the choice does not depend on the seed.
    @Override
    public OrderItem chooseItem(Menu menu, Random rng) {
        OrderItem best = null;
        for (OrderItem item : menu.getDishes()) {
            if (best == null || item.getPriceCents() > best.getPriceCents()) {
                best = item;
            }
        }
        return best;
    }

    @Override
    public void onServed(int waitTurns) {
        if (waitTurns <= FAST_SERVICE_TURNS) {
            adjustSatisfaction(FAST_BONUS);
        } else {
            adjustSatisfaction(-SLOW_PENALTY);
        }
    }

    @Override
    public String getTypeLabel() {
        return "Critic";
    }
}
