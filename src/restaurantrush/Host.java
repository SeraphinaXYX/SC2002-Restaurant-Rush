package restaurantrush;

/**
 * Stage 3: the Host greets the queue and can seat one customer per turn,
 * on top of whatever the Waiter does. The controller asks the Host only
 * once per turn, which is what enforces the one-seat limit.
 *
 * @author Group XX
 */
public class Host extends Staff {

    public Host(String name) {
        super(name);
    }

    @Override
    public String getRole() {
        return "Host";
    }

    @Override
    public String getDuty() {
        return "seat one waiting customer (in addition to the Waiter)";
    }

    public ActionResult seat(Customer customer, Table table, Restaurant restaurant) {
        return seatCustomer(customer, table, restaurant);
    }
}
