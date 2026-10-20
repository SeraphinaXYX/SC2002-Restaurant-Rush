package restaurantrush;

/**
 * A table seats one customer until that customer pays or leaves.
 *
 * @author Group XX
 */
public class Table {
    private final int id;
    private Customer customer;

    public Table(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public boolean isFree() {
        return customer == null;
    }

    public void seat(Customer customer) {
        this.customer = customer;
    }

    public void release() {
        customer = null;
    }
}
