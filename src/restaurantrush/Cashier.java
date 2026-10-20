package restaurantrush;

/**
 * The Cashier works automatically: one payment per turn, oldest first.
 *
 * @author Group XX
 */
public class Cashier extends Staff {

    public Cashier(String name) {
        super(name);
    }

    @Override
    public String getRole() {
        return "Cashier";
    }

    @Override
    public String getDuty() {
        return "automatic, collects one payment per turn";
    }

    /** Collects the next payment if there is one. Returns the paid order or null. */
    public Order collectNext(Restaurant restaurant, int turn) {
        Order order = restaurant.oldestReadyToPay(turn);
        if (order == null) {
            return null;
        }
        Customer customer = order.getCustomer();
        order.markPaid();
        restaurant.addRevenue(order.getPriceCents());
        customer.recordSatisfaction();
        restaurant.releaseTable(customer);
        customer.markPaid();
        return order;
    }
}
