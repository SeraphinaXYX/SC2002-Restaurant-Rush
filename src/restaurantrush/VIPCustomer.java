package restaurantrush;

import java.util.Random;

/**
 * VIP guest: more patient (loses 5 satisfaction per waiting turn) and
 * gets 10% off the bill.
 *
 * @author Group XX
 */
public class VIPCustomer extends Customer {
    private static final int DISCOUNT_PERCENT = 10;

    public VIPCustomer(int id) {
        super(id);
    }

    @Override
    public int getSatisfactionLoss() {
        return 5;
    }

    @Override
    public int applyDiscount(int priceCents) {
        // integer maths, rounded to the nearest cent
        return (priceCents * (100 - DISCOUNT_PERCENT) + 50) / 100;
    }

    @Override
    public OrderItem chooseItem(Menu menu, Random rng) {
        return pickDefault(menu, rng);
    }

    @Override
    public String getTypeLabel() {
        return "VIP";
    }
}
