package restaurantrush;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * What customers can choose from.
 *
 * @author Group XX
 */
public class Menu {
    private final List<OrderItem> dishes;

    public Menu() {
        dishes = List.copyOf(Arrays.asList(MenuItem.values()));
    }

    public List<OrderItem> getDishes() {
        return Collections.unmodifiableList(dishes);
    }
}
