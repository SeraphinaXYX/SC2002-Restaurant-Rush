package restaurantrush;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Runs the 18 turns. Each turn follows the six steps in the brief; the
 * actual rules live in the staff and domain classes.
 *
 * @author Group XX
 */
public class GameController {
    private final GameConfig config;
    private final GameView view;
    private final Restaurant restaurant;
    private final Menu menu;
    private final Random rng;
    private final Waiter waiter = new Waiter("Wendy");
    private final Chef chef = new Chef("Carlos");
    private final Cashier cashier = new Cashier("Cathy");
    private final Host host;            // null below Stage 3
    private final HappyHour happyHour;  // null below Stage 3
    private int turn;
    private int nextCustomerId = 1;

    public GameController(GameConfig config, GameView view) {
        this.config = config;
        this.view = view;
        this.restaurant = new Restaurant(GameConfig.TABLE_COUNT);
        this.menu = new Menu(config.hasCombos());
        this.rng = new Random(config.getSeed());
        this.host = config.hasHost() ? new Host("Hana") : null;
        this.happyHour = config.hasHappyHour() ? new HappyHour() : null;
    }

    public void run() {
        view.showIntro(config, menu);
        for (turn = 1; turn <= GameConfig.MAX_TURNS; turn++) {
            playTurn();
        }
        result();
    }

    private void playTurn() {
        view.showTurnHeader(turn, restaurant);
        arrivals();
        happyHourStart();
        view.showState(restaurant, staffOnDuty(), happyHour);
        managerChoices();
        eatingAndPayment();
        waitingAndAbandonment();
        if (happyHour != null) {
            happyHour.endTurn();
        }
        view.showTurnEnd(turn, restaurant);
    }

    private List<Staff> staffOnDuty() {
        List<Staff> staff = new ArrayList<>();
        if (host != null) {
            staff.add(host);
        }
        staff.add(waiter);
        staff.add(chef);
        staff.add(cashier);
        return staff;
    }

    // Step 1
    private void arrivals() {
        Customer customer = config.createArrival(turn, nextCustomerId);
        if (customer != null) {
            nextCustomerId++;
            restaurant.admit(customer);
            view.showAction(customer.getName() + " arrives and joins the queue (satisfaction "
                    + customer.getSatisfaction() + ").");
        }
    }

    /**
     * Happy Hour can be switched on once, at the start of a turn, before any staff act.
     * It runs before the state is printed so the manager sees post-recovery satisfaction.
     * While active, only customers still waiting for a table or food recover; paid
     * customers keep the satisfaction recorded when they paid.
     */
    private void happyHourStart() {
        if (happyHour == null) {
            return;
        }
        if (happyHour.isAvailable() && view.askActivateHappyHour()) {
            happyHour.activate();
            view.showAction("Happy Hour is ON for this turn and the next: 20% off new orders.");
        }
        if (happyHour.isActive()) {
            for (Customer customer : restaurant.getCustomers()) {
                if (customer.isWaitingForService()) {
                    int before = customer.getSatisfaction();
                    customer.recoverSatisfaction(HappyHour.RECOVERY);
                    if (customer.getSatisfaction() != before) {
                        view.showRecovery(customer, before, customer.getSatisfaction());
                    }
                }
            }
        }
    }

    // Steps 2 and 3: Host (Stage 3) first, and the Waiter always acts before the Chef
    private void managerChoices() {
        if (host != null) {
            hostTurn();
        }
        waiterTurn();
        chefTurn();
    }

    // The Host is asked once per turn, which is what limits it to one seat
    private void hostTurn() {
        while (true) {
            if (restaurant.getQueue().isEmpty()) {
                view.showAction(host.getLabel() + " has nobody to seat.");
                return;
            }
            if (restaurant.getFreeTables().isEmpty()) {
                view.showAction(host.getLabel() + " has no free table to use.");
                return;
            }
            Customer customer = view.chooseCustomer(host.getLabel() + " - seat which customer?",
                    restaurant.getQueue().getAll(), "Wait");
            if (customer == null) {
                view.showAction(host.getLabel() + " waits this turn.");
                return;
            }
            Table table = view.chooseTable("At which table?", restaurant.getTables());
            if (table == null) {
                continue;
            }
            ActionResult result = host.seat(customer, table, restaurant);
            if (result.isSuccess()) {
                view.showAction(result.getMessage());
                return;
            }
            view.showError(result.getMessage());
        }
    }

    private void waiterTurn() {
        while (true) {
            ActionResult result = switch (view.chooseWaiterAction(waiter)) {
                case SEAT -> waiterSeat();
                case TAKE_ORDER -> waiterTakeOrder();
                case SERVE -> waiterServe();
                case WAIT -> ActionResult.ok(waiter.getLabel() + " waits this turn.");
            };
            if (result == null) {
                continue; // manager picked "Back"
            }
            if (result.isSuccess()) {
                view.showAction(result.getMessage());
                return;
            }
            view.showError(result.getMessage());
        }
    }

    private ActionResult waiterSeat() {
        if (restaurant.getQueue().isEmpty()) {
            return ActionResult.fail("Nobody is waiting in the queue.");
        }
        Customer customer = view.chooseCustomer("Seat which customer?", restaurant.getQueue().getAll(), "Back");
        if (customer == null) {
            return null;
        }
        Table table = view.chooseTable("At which table?", restaurant.getTables());
        if (table == null) {
            return null;
        }
        return waiter.seat(customer, table, restaurant);
    }

    private ActionResult waiterTakeOrder() {
        if (!restaurant.hasSeatedCustomer()) {
            return ActionResult.fail("No customer is seated yet.");
        }
        Table table = view.chooseTable("Take the order at which table?", restaurant.getTables());
        if (table == null) {
            return null;
        }
        if (table.isFree()) {
            return ActionResult.fail("Table " + table.getId() + " is empty.");
        }
        return waiter.takeOrder(table.getCustomer(), menu, happyHour, rng, turn, restaurant);
    }

    private ActionResult waiterServe() {
        List<Order> kitchen = restaurant.getKitchenOrders();
        if (kitchen.isEmpty()) {
            return ActionResult.fail("There are no orders in the kitchen.");
        }
        Order order = view.chooseOrder("Serve which dish?", kitchen, "Back");
        if (order == null) {
            return null;
        }
        return waiter.serve(order, turn);
    }

    private void chefTurn() {
        if (!restaurant.hasCookableOrder()) {
            view.showAction(chef.getLabel() + " has nothing to prepare.");
            return;
        }
        while (true) {
            Order order = view.chooseChefAction(chef, restaurant.getKitchenOrders());
            if (order == null) {
                view.showAction(chef.getLabel() + " waits this turn.");
                return;
            }
            ActionResult result = chef.prepare(order);
            if (result.isSuccess()) {
                view.showAction(result.getMessage());
                return;
            }
            view.showError(result.getMessage());
        }
    }

    // Step 4: the Cashier goes first, so whoever finishes eating now pays next turn at the earliest
    private void eatingAndPayment() {
        Order paid = cashier.collectNext(restaurant, turn);
        if (paid != null) {
            view.showPayment(cashier, paid);
        }
        for (Order order : restaurant.getOrders()) {
            if (order.getStatus() == OrderStatus.SERVED && order.getTurnServed() < turn) {
                order.markReadyToPay(turn);
                order.getCustomer().finishEating();
                view.showAction(order.getCustomer().getName() + " finishes eating and is READY_TO_PAY.");
            }
        }
    }

    // Step 5
    private void waitingAndAbandonment() {
        for (Customer customer : restaurant.getCustomers()) {
            if (customer.isWaitingForService() && customer.loseSatisfaction()) {
                restaurant.depart(customer);
                view.showAction(customer.getName() + " runs out of patience and leaves without paying.");
            }
        }
    }

    // Step 6
    private void result() {
        Double avg = restaurant.getAverageSatisfaction();
        boolean paidOk = restaurant.getPaidCount() >= GameConfig.TARGET_PAID;
        boolean revenueOk = restaurant.getRevenueCents() >= GameConfig.TARGET_REVENUE_CENTS;
        boolean satisfactionOk = avg != null && avg >= GameConfig.TARGET_AVG_SATISFACTION;
        view.showFinalResult(restaurant, paidOk, revenueOk, satisfactionOk);
    }
}
