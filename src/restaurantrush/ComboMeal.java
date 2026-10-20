package restaurantrush;

/**
 * A combo made of two existing menu items. It takes as long as both dishes
 * together and costs 90% of their combined price, rounded to the nearest cent.
 *
 * @author Group XX
 */
public class ComboMeal implements OrderItem {
    private static final int COMBO_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String getName() {
        return first.getName() + " + " + second.getName() + " Combo";
    }

    @Override
    public int getPriceCents() {
        int total = first.getPriceCents() + second.getPriceCents();
        return (total * COMBO_PERCENT + 50) / 100; // nearest cent, e.g. 2700 -> 2430
    }

    @Override
    public int getPrepUnits() {
        return first.getPrepUnits() + second.getPrepUnits();
    }
}
