package restaurantrush;

/**
 * The Chef completes one preparation unit per turn.
 *
 * @author Group XX
 */
public class Chef extends Staff {

    public Chef(String name) {
        super(name);
    }

    @Override
    public String getRole() {
        return "Chef";
    }

    @Override
    public String getDuty() {
        return "one preparation unit on one order";
    }

    public ActionResult prepare(Order order) {
        String dish = order.getItem().getName() + " for " + order.getCustomer().getName();
        switch (order.getStatus()) {
            case PLACED:
            case COOKING:
                order.advancePrep();
                String msg = getLabel() + " works on " + dish + " (" + order.getProgress() + ")";
                if (order.isReady()) {
                    msg += " - READY, can be served from next turn";
                }
                return ActionResult.ok(msg + ".");
            case READY:
                return ActionResult.fail(dish + " is already READY; it just needs serving.");
            default:
                return ActionResult.fail(dish + " cannot be prepared, it is " + order.getStatus() + ".");
        }
    }
}
