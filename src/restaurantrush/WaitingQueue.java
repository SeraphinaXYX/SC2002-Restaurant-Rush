package restaurantrush;

import java.util.LinkedList;
import java.util.List;

/**
 * Customers who have arrived but do not have a table yet, in arrival order.
 *
 * @author Group XX
 */
public class WaitingQueue {
    private final LinkedList<Customer> customers = new LinkedList<>();

    public void add(Customer customer) {
        customers.addLast(customer);
    }

    public void remove(Customer customer) {
        customers.remove(customer);
    }

    public boolean isEmpty() {
        return customers.isEmpty();
    }

    public int size() {
        return customers.size();
    }

    /** Returns a copy so callers cannot modify the queue directly. */
    public List<Customer> getAll() {
        return List.copyOf(customers);
    }
}
