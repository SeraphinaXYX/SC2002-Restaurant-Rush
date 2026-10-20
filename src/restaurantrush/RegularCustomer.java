package restaurantrush;

import java.util.Random;

/**
 * Ordinary customer: loses 8 satisfaction per waiting turn and pays full price.
 *
 * @author Group XX
 */
public class RegularCustomer extends Customer {

    public RegularCustomer(int id) {
        super(id);
    }

    @Override
    public int getSatisfactionLoss() {
        return 8;
    }

    @Override
    public int applyDiscount(int priceCents) {
        return priceCents;
    }

    @Override
    public OrderItem chooseItem(Menu menu, Random rng) {
        return pickDefault(menu, rng);
    }

    @Override
    public String getTypeLabel() {
        return "Regular";
    }
}
