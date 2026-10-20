package restaurantrush;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the state of the dining room: tables, queue, customers, orders and
 * revenue. Staff act on it; the view only reads it.
 *
 * @author Group XX
 */
public class Restaurant {
    private final List<Table> tables = new ArrayList<>();
    private final WaitingQueue queue = new WaitingQueue();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private int revenueCents;

    public Restaurant(int tableCount) {
        for (int i = 1; i <= tableCount; i++) {
            tables.add(new Table(i));
        }
    }

    /** A new arrival joins the back of the queue. */
    public void admit(Customer customer) {
        customers.add(customer);
        queue.add(customer);
    }

    public void removeFromQueue(Customer customer) {
        queue.remove(customer);
    }

    /** Moves a queued customer to a free table, or explains why it cannot. */
    public ActionResult seatAt(Customer customer, Table table) {
        if (customer.getState() != CustomerState.WAITING) {
            return ActionResult.fail(customer.getName() + " is not waiting in the queue.");
        }
        if (!table.isFree()) {
            return ActionResult.fail("Table " + table.getId() + " is occupied by "
                    + table.getCustomer().getName() + ".");
        }
        removeFromQueue(customer);
        table.seat(customer);
        customer.seatAt(table);
        return ActionResult.ok(customer.getName() + " sits at Table " + table.getId() + ".");
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void addRevenue(int cents) {
        revenueCents += cents;
    }

    public void releaseTable(Customer customer) {
        Table table = customer.getTable();
        if (table != null) {
            table.release();
        }
    }

    /** A customer at 0 satisfaction leaves: order cancelled, table or queue spot freed. */
    public void depart(Customer customer) {
        releaseTable(customer);
        removeFromQueue(customer);
        customer.leave();
    }

    /**
     * Oldest customer who was already READY_TO_PAY before this turn, or null.
     * Someone who became READY_TO_PAY this turn has to wait until the next one.
     */
    public Order oldestReadyToPay(int turn) {
        Order oldest = null;
        for (Order order : orders) {
            if (order.getStatus() != OrderStatus.READY_TO_PAY || order.getTurnReadyToPay() >= turn) {
                continue;
            }
            if (oldest == null
                    || order.getTurnReadyToPay() < oldest.getTurnReadyToPay()
                    || (order.getTurnReadyToPay() == oldest.getTurnReadyToPay()
                        && order.getCustomer().getId() < oldest.getCustomer().getId())) {
                oldest = order;
            }
        }
        return oldest;
    }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public List<Table> getFreeTables() {
        List<Table> free = new ArrayList<>();
        for (Table t : tables) {
            if (t.isFree()) {
                free.add(t);
            }
        }
        return free;
    }

    public boolean hasSeatedCustomer() {
        return getFreeTables().size() < tables.size();
    }

    public WaitingQueue getQueue() {
        return queue;
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    /** Orders taken but not served yet (PLACED, COOKING or READY). */
    public List<Order> getKitchenOrders() {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.isInKitchen()) {
                result.add(o);
            }
        }
        return result;
    }

    public boolean hasCookableOrder() {
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.PLACED || o.getStatus() == OrderStatus.COOKING) {
                return true;
            }
        }
        return false;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public int getPaidCount() {
        return countInState(CustomerState.PAID);
    }

    public int getDepartedCount() {
        return countInState(CustomerState.LEFT);
    }

    /** Customers whose food has been served (eating, ready to pay or paid). */
    public int getServedCount() {
        return countInState(CustomerState.EATING) + countInState(CustomerState.READY_TO_PAY)
                + getPaidCount();
    }

    /** Average of the satisfaction recorded at payment, or null if nobody has paid. */
    public Double getAverageSatisfaction() {
        int total = 0;
        int paid = 0;
        for (Customer c : customers) {
            if (c.getState() == CustomerState.PAID) {
                total += c.getRecordedSatisfaction();
                paid++;
            }
        }
        return paid == 0 ? null : (double) total / paid;
    }

    private int countInState(CustomerState state) {
        int count = 0;
        for (Customer c : customers) {
            if (c.getState() == state) {
                count++;
            }
        }
        return count;
    }
}
