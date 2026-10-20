package restaurantrush;

/**
 * Outcome of a staff action. A failed result carries the reason, so the
 * manager is told why and can pick again without losing the staff task.
 *
 * @author Group XX
 */
public final class ActionResult {
    private final boolean success;
    private final String message;

    private ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ActionResult ok(String message) {
        return new ActionResult(true, message);
    }

    public static ActionResult fail(String reason) {
        return new ActionResult(false, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
