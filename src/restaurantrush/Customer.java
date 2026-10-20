package restaurantrush;

import java.util.List;
import java.util.Random;

/**
 * Base class for every customer. The controller only talks to this type;
 * how a customer loses satisfaction, gets a discount and chooses food is
 * decided by the subclass.
 *
 * @author Group XX
 */
public abstract class Customer {
    private static final int MAX_SATISFACTION = 100;

    private final int id;
    private final int arrivalTurn;
    private int satisfaction = MAX_SATISFACTION;
    private CustomerState state = CustomerState.WAITING;
    private Table table;
    private Order order;
    private int recordedSatisfaction = -1;

    protected Customer(int id, int arrivalTurn) {
        this.id = id;
        this.arrivalTurn = arrivalTurn;
    }

    /** Satisfaction lost for every turn spent waiting for a table or food. */
    public abstract int getSatisfactionLoss();

    /** Applies this customer type's discount to a list price (in cents). */
    public abstract int applyDiscount(int priceCents);

    /** The customer, not the manager, decides what to eat. */
    public abstract OrderItem chooseItem(Menu menu, Random rng);

    public abstract String getTypeLabel();

    /**
     * Called right after the dish is served. Does nothing by default; a
     * subclass can react to how long the food took.
     */
    public void onServed(int waitTurns) {
    }

    /** Default ordering rule: a seeded random pick from the dishes. */
    protected OrderItem pickDefault(Menu menu, Random rng) {
        List<OrderItem> dishes = menu.getDishes();
        return dishes.get(rng.nextInt(dishes.size()));
    }

    public String getName() {
        return getTypeLabel() + " #" + id;
    }

    /** Waiting for a table or for food - the only states that cost satisfaction. */
    public boolean isWaitingForService() {
        return state == CustomerState.WAITING || state == CustomerState.SEATED
                || state == CustomerState.ORDERED;
    }

    /** End-of-turn loss. Returns true when satisfaction has hit 0. */
    public boolean loseSatisfaction() {
        adjustSatisfaction(-getSatisfactionLoss());
        return satisfaction == 0;
    }

    protected void adjustSatisfaction(int delta) {
        satisfaction = Math.max(0, Math.min(MAX_SATISFACTION, satisfaction + delta));
    }

    public void seatAt(Table table) {
        this.table = table;
        state = CustomerState.SEATED;
    }

    public void placeOrder(Order order) {
        this.order = order;
        state = CustomerState.ORDERED;
    }

    public void receiveDish(int waitTurns) {
        state = CustomerState.EATING;
        onServed(waitTurns);
    }

    public void finishEating() {
        state = CustomerState.READY_TO_PAY;
    }

    /** Freezes the satisfaction used for the final average. */
    public void recordSatisfaction() {
        recordedSatisfaction = satisfaction;
    }

    public void markPaid() {
        state = CustomerState.PAID;
        table = null;
    }

    public void leave() {
        state = CustomerState.LEFT;
        if (order != null) {
            order.cancel();
        }
        table = null;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerState getState() {
        return state;
    }

    public Table getTable() {
        return table;
    }

    public Order getOrder() {
        return order;
    }

    public int getRecordedSatisfaction() {
        return recordedSatisfaction;
    }
}
