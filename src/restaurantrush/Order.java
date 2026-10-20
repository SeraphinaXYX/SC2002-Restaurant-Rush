package restaurantrush;

/**
 * One customer's order. The price is fixed when the order is taken, so
 * later discounts (or their end) never change it.
 *
 * @author Group XX
 */
public class Order {
    private final Customer customer;
    private final OrderItem item;
    private final int priceCents;
    private final int turnPlaced;
    private OrderStatus status = OrderStatus.PLACED;
    private int prepDone;
    private int turnServed = -1;
    private int turnReadyToPay = -1;

    public Order(Customer customer, OrderItem item, int priceCents, int turnPlaced) {
        this.customer = customer;
        this.item = item;
        this.priceCents = priceCents;
        this.turnPlaced = turnPlaced;
    }

    /** Does one preparation unit. The last unit makes the order READY. */
    public void advancePrep() {
        if (status != OrderStatus.PLACED && status != OrderStatus.COOKING) {
            throw new IllegalStateException("Cannot prepare an order that is " + status);
        }
        prepDone++;
        status = prepDone >= item.getPrepUnits() ? OrderStatus.READY : OrderStatus.COOKING;
    }

    public void markServed(int turn) {
        status = OrderStatus.SERVED;
        turnServed = turn;
    }

    public void markReadyToPay(int turn) {
        status = OrderStatus.READY_TO_PAY;
        turnReadyToPay = turn;
    }

    public void markPaid() {
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status != OrderStatus.PAID) {
            status = OrderStatus.CANCELLED;
        }
    }

    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    /** True while the order is still the kitchen's business (not yet served). */
    public boolean isInKitchen() {
        return status == OrderStatus.PLACED || status == OrderStatus.COOKING || status == OrderStatus.READY;
    }

    public String getProgress() {
        return prepDone + "/" + item.getPrepUnits();
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderItem getItem() {
        return item;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public int getTurnPlaced() {
        return turnPlaced;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getPrepDone() {
        return prepDone;
    }

    public int getTurnServed() {
        return turnServed;
    }

    public int getTurnReadyToPay() {
        return turnReadyToPay;
    }
}
