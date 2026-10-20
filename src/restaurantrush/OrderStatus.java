package restaurantrush;

/**
 * Life cycle of an order. SERVED means the customer is eating.
 *
 * @author Group XX
 */
public enum OrderStatus {
    PLACED,
    COOKING,
    READY,
    SERVED,
    READY_TO_PAY,
    PAID,
    CANCELLED
}
