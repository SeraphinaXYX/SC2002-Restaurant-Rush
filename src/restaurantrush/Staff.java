package restaurantrush;

/**
 * Common base for restaurant staff.
 *
 * @author Group XX
 */
public abstract class Staff {
    private final String name;

    protected Staff(String name) {
        this.name = name;
    }

    public String getLabel() {
        return getRole() + " " + name;
    }

    public abstract String getRole();

    /** One-line description of what this staff member can do in a turn. */
    public abstract String getDuty();

    /** Shared by Waiter and Host: the seating rule itself lives in Restaurant.seatAt. */
    protected ActionResult seatCustomer(Customer customer, Table table, Restaurant restaurant) {
        ActionResult result = restaurant.seatAt(customer, table);
        if (!result.isSuccess()) {
            return result;
        }
        return ActionResult.ok(getLabel() + " seats " + customer.getName() + " at Table " + table.getId() + ".");
    }
}
