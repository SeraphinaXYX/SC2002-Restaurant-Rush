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

    public String getName() {
        return name;
    }

    public String getLabel() {
        return getRole() + " " + name;
    }

    public abstract String getRole();

    /** One-line description of what this staff member can do in a turn. */
    public abstract String getDuty();
}
