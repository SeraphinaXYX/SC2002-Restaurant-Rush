package restaurantrush;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console input and output. It shows the state and returns the manager's
 * choices; it never changes the game itself.
 *
 * @author Group XX
 */
public class GameView {
    private static final String LINE = "=".repeat(72);
    private static final String THIN = "-".repeat(72);

    private final Scanner in;
    private final boolean echoInput;

    /**
     * @param echoInput print each line read, so a run fed from a file still
     *                  shows what was chosen
     */
    public GameView(Scanner in, boolean echoInput) {
        this.in = in;
        this.echoInput = echoInput;
    }

    public long askSeed() {
        while (true) {
            System.out.print("Random seed (press Enter for 42) > ");
            String line = readLine();
            if (line == null || line.isEmpty()) {
                return 42;
            }
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("  ! \"" + line + "\" is not a whole number. Try again.");
            }
        }
    }

    public void showIntro(GameConfig config, Menu menu) {
        System.out.println(LINE);
        System.out.println("  RESTAURANT RUSH            (seed " + config.getSeed() + ")");
        System.out.println(LINE);
        System.out.println("You are the manager: " + GameConfig.TABLE_COUNT + " tables, "
                + GameConfig.MAX_TURNS + " turns, customers arrive on turns " + GameConfig.ARRIVAL_TURNS + ".");
        System.out.printf("To win: at least %d paid customers, revenue at least %s and average%n"
                        + "satisfaction of paid customers at least %d.%n",
                GameConfig.TARGET_PAID, MenuItem.formatPrice(GameConfig.TARGET_REVENUE_CENTS),
                GameConfig.TARGET_AVG_SATISFACTION);
        System.out.println("Menu:");
        for (OrderItem item : menu.getDishes()) {
            System.out.printf("  %-26s %8s   %d prep unit(s)%n", item.getName(),
                    MenuItem.formatPrice(item.getPriceCents()), item.getPrepUnits());
        }
        System.out.println("(The customer arriving on turn 16 cannot finish paying in time - that is expected.)");
    }

    public void showTurnHeader(int turn, Restaurant restaurant) {
        System.out.println();
        System.out.println(LINE);
        System.out.printf(" TURN %d / %d     Revenue: %s     Avg satisfaction (paid): %s%n", turn,
                GameConfig.MAX_TURNS, MenuItem.formatPrice(restaurant.getRevenueCents()),
                formatAverage(restaurant.getAverageSatisfaction()));
        System.out.println(LINE);
    }

    public void showState(Restaurant restaurant, List<Staff> staff) {
        System.out.println(" Waiting queue:");
        if (restaurant.getQueue().isEmpty()) {
            System.out.println("   (empty)");
        }
        for (Customer c : restaurant.getQueue().getAll()) {
            System.out.println("   " + describe(c));
        }
        System.out.println(" Tables:");
        for (Table t : restaurant.getTables()) {
            if (t.isFree()) {
                System.out.printf("   Table %d: (free)%n", t.getId());
                continue;
            }
            Customer c = t.getCustomer();
            System.out.printf("   Table %d: %s%n", t.getId(), describe(c));
            if (c.getOrder() != null) {
                System.out.println("            order: " + describe(c.getOrder()));
            }
        }
        System.out.println(" Staff on duty:");
        for (Staff s : staff) {
            System.out.printf("   %-16s %s%n", s.getLabel(), s.getDuty());
        }
        System.out.println(THIN);
    }

    public Waiter.Action chooseWaiterAction(Waiter waiter) {
        List<String> labels = List.of("Seat a waiting customer", "Take an order", "Serve a READY dish", "Wait");
        return switch (choose(waiter.getLabel() + " - choose one task:", labels)) {
            case 0 -> Waiter.Action.SEAT;
            case 1 -> Waiter.Action.TAKE_ORDER;
            case 2 -> Waiter.Action.SERVE;
            default -> Waiter.Action.WAIT;
        };
    }

    /** Returns the chosen order, or null if the Chef should wait. */
    public Order chooseChefAction(Chef chef, List<Order> orders) {
        return chooseOrder(chef.getLabel() + " - prepare one unit of which order?", orders, "Wait");
    }

    /** Returns the chosen customer, or null for the last option (Back / Wait). */
    public Customer chooseCustomer(String title, List<Customer> customers, String lastOption) {
        List<String> labels = new ArrayList<>();
        for (Customer c : customers) {
            labels.add(describe(c));
        }
        labels.add(lastOption);
        int choice = choose(title, labels);
        return choice < customers.size() ? customers.get(choice) : null;
    }

    /** Lists every table, free or not, so a bad pick can be explained. Null means Back. */
    public Table chooseTable(String title, List<Table> tables) {
        List<String> labels = new ArrayList<>();
        for (Table t : tables) {
            labels.add("Table " + t.getId() + " - " + (t.isFree() ? "free" : describe(t.getCustomer())));
        }
        labels.add("Back");
        int choice = choose(title, labels);
        return choice < tables.size() ? tables.get(choice) : null;
    }

    public Order chooseOrder(String title, List<Order> orders, String lastOption) {
        List<String> labels = new ArrayList<>();
        for (Order o : orders) {
            labels.add(o.getItem().getName() + " for " + o.getCustomer().getName() + " - " + describe(o));
        }
        labels.add(lastOption);
        int choice = choose(title, labels);
        return choice < orders.size() ? orders.get(choice) : null;
    }

    public void showAction(String message) {
        System.out.println("  * " + message);
    }

    public void showError(String reason) {
        System.out.println("  ! Not possible: " + reason + " Choose again.");
    }

    public void showPayment(Cashier cashier, Order order) {
        Customer c = order.getCustomer();
        showAction(cashier.getLabel() + " collects " + MenuItem.formatPrice(order.getPriceCents()) + " from "
                + c.getName() + " (satisfaction recorded: " + c.getRecordedSatisfaction() + "). Table freed.");
    }

    public void showTurnEnd(int turn, Restaurant restaurant) {
        System.out.println(THIN);
        System.out.printf(" End of turn %d: served %d | unhappy departures %d | revenue %s%n", turn,
                restaurant.getServedCount(), restaurant.getDepartedCount(),
                MenuItem.formatPrice(restaurant.getRevenueCents()));
        System.out.printf("                 avg satisfaction (paid) %s | turns used %d/%d%n",
                formatAverage(restaurant.getAverageSatisfaction()), turn, GameConfig.MAX_TURNS);
    }

    public void showFinalResult(Restaurant restaurant, boolean paidOk, boolean revenueOk, boolean satisfactionOk) {
        boolean victory = paidOk && revenueOk && satisfactionOk;
        System.out.println();
        System.out.println(LINE);
        System.out.println(victory ? "  VICTORY! The restaurant hit every target."
                : "  DEFEAT. At least one target was missed.");
        System.out.println(LINE);
        System.out.printf("  Paid customers    %-8d (need %d)   %s%n", restaurant.getPaidCount(),
                GameConfig.TARGET_PAID, mark(paidOk));
        System.out.printf("  Revenue           %-8s (need %s)  %s%n", MenuItem.formatPrice(restaurant.getRevenueCents()),
                MenuItem.formatPrice(GameConfig.TARGET_REVENUE_CENTS), mark(revenueOk));
        System.out.printf("  Avg satisfaction  %-8s (need %d)  %s%n",
                formatAverage(restaurant.getAverageSatisfaction()), GameConfig.TARGET_AVG_SATISFACTION,
                mark(satisfactionOk));
        System.out.printf("  Unhappy departures: %d%n", restaurant.getDepartedCount());
    }

    private int choose(String title, List<String> labels) {
        System.out.println(" " + title);
        for (int i = 0; i < labels.size(); i++) {
            System.out.printf("   %d. %s%n", i + 1, labels.get(i));
        }
        while (true) {
            System.out.print(" Choice > ");
            String line = readLine();
            if (line == null) {
                return labels.size() - 1; // input ran out: take the last option (Wait / Back)
            }
            try {
                int n = Integer.parseInt(line);
                if (n >= 1 && n <= labels.size()) {
                    return n - 1;
                }
            } catch (NumberFormatException ignored) {
                // falls through to the message below
            }
            System.out.println("  ! Please enter a number from 1 to " + labels.size() + ".");
        }
    }

    private String readLine() {
        if (!in.hasNextLine()) {
            System.out.println();
            return null;
        }
        String line = in.nextLine().trim();
        if (echoInput) {
            System.out.println(line);
        }
        return line;
    }

    private static String describe(Customer c) {
        return String.format("%s [satisfaction %d, %s]", c.getName(), c.getSatisfaction(), c.getState());
    }

    private static String describe(Order o) {
        return String.format("%s %s units, %s", o.getStatus(), o.getProgress(), MenuItem.formatPrice(o.getPriceCents()));
    }

    private static String formatAverage(Double avg) {
        return avg == null ? "N/A" : String.format("%.1f", avg);
    }

    private static String mark(boolean ok) {
        return ok ? "[met]" : "[missed]";
    }
}
