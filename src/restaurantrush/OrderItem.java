package restaurantrush;

/**
 * Anything a customer can order. Order only depends on this interface,
 * so it does not care whether the item is a single dish or something bigger.
 *
 * @author Group XX
 */
public interface OrderItem {
    String getName();

    int getPriceCents();

    int getPrepUnits();
}
