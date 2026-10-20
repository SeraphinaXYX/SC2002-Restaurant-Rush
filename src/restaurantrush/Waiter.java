package restaurantrush;

import java.util.Random;

/**
 * The Waiter does at most one task per turn: seat, take an order or serve.
 *
 * @author Group XX
 */
public class Waiter extends Staff {

    /** The tasks the manager can give the Waiter. */
    public enum Action { SEAT, TAKE_ORDER, SERVE, WAIT }

    public Waiter(String name) {
        super(name);
    }

    @Override
    public String getRole() {
        return "Waiter";
    }

    @Override
    public String getDuty() {
        return "one task: seat, take an order or serve a READY dish";
    }

    public ActionResult seat(Customer customer, Table table, Restaurant restaurant) {
        ActionResult result = restaurant.seatAt(customer, table);
        if (!result.isSuccess()) {
            return result;
        }
        return ActionResult.ok(getLabel() + " seats " + customer.getName() + " at Table " + table.getId() + ".");
    }

    /**
     * The customer picks the food; the price is worked out and locked here.
     * happyHour may be null when the stage has no Happy Hour.
     */
    public ActionResult takeOrder(Customer customer, Menu menu, HappyHour happyHour, Random rng, int turn,
                                  Restaurant restaurant) {
        if (customer.getState() != CustomerState.SEATED) {
            if (customer.getOrder() != null) {
                return ActionResult.fail(customer.getName() + " has already ordered "
                        + customer.getOrder().getItem().getName() + ".");
            }
            return ActionResult.fail(customer.getName() + " is not seated at a table.");
        }
        OrderItem item = customer.chooseItem(menu, rng);
        int price = customer.applyDiscount(item.getPriceCents()); // customer discount first (VIP 10%)
        if (happyHour != null) {
            price = happyHour.apply(price); // then Happy Hour 20%, only while active
        }
        Order order = new Order(customer, item, price, turn);
        customer.placeOrder(order);
        restaurant.addOrder(order);
        return ActionResult.ok(getLabel() + " takes " + customer.getName() + "'s order: "
                + item.getName() + " " + describePrice(item, price) + ".");
    }

    /** Only a dish that was already READY when the Waiter acts can be served. */
    public ActionResult serve(Order order, int turn) {
        String dish = order.getItem().getName() + " for " + order.getCustomer().getName();
        if (!order.isReady()) {
            if (order.getStatus() == OrderStatus.PLACED || order.getStatus() == OrderStatus.COOKING) {
                return ActionResult.fail(dish + " is not READY yet (" + order.getProgress() + " units done).");
            }
            return ActionResult.fail(dish + " cannot be served, it is " + order.getStatus() + ".");
        }
        Customer customer = order.getCustomer();
        int before = customer.getSatisfaction();
        order.markServed(turn);
        customer.receiveDish(turn - order.getTurnPlaced());
        String msg = getLabel() + " serves " + dish + ".";
        if (customer.getSatisfaction() != before) {
            msg += " " + customer.getName() + " reacts: satisfaction " + before + " -> "
                    + customer.getSatisfaction() + ".";
        }
        return ActionResult.ok(msg);
    }

    private static String describePrice(OrderItem item, int price) {
        if (price == item.getPriceCents()) {
            return "(" + MenuItem.formatPrice(price) + ")";
        }
        return "(list " + MenuItem.formatPrice(item.getPriceCents()) + ", charged "
                + MenuItem.formatPrice(price) + ")";
    }
}
