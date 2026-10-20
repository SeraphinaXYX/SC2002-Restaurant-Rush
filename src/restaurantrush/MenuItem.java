package restaurantrush;

/**
 * The three base dishes. Prices are kept in cents so we never deal with
 * floating point money.
 *
 * @author Group XX
 */
public enum MenuItem implements OrderItem {
    SALAD("Salad", 900, 1),
    BURGER("Burger", 1500, 1),
    PASTA("Pasta", 1800, 2);

    private final String name;
    private final int priceCents;
    private final int prepUnits;

    MenuItem(String name, int priceCents, int prepUnits) {
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPriceCents() {
        return priceCents;
    }

    @Override
    public int getPrepUnits() {
        return prepUnits;
    }

    /** Formats cents as dollars, e.g. 2430 becomes "$24.30". */
    public static String formatPrice(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
}
