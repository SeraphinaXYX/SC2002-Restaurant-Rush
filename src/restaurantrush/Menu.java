package restaurantrush;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * What customers can choose from. With combos switched on (Stage 3) the menu
 * also lists every pair of base dishes, including the same dish twice.
 *
 * @author Group XX
 */
public class Menu {
    private final List<OrderItem> dishes;
    private final List<ComboMeal> combos;

    public Menu(boolean includeCombos) {
        dishes = List.copyOf(Arrays.asList(MenuItem.values()));
        List<ComboMeal> pairs = new ArrayList<>();
        if (includeCombos) {
            MenuItem[] base = MenuItem.values();
            for (int i = 0; i < base.length; i++) {
                for (int j = i; j < base.length; j++) {
                    pairs.add(new ComboMeal(base[i], base[j]));
                }
            }
        }
        combos = List.copyOf(pairs);
    }

    /** The single dishes only. */
    public List<OrderItem> getDishes() {
        return Collections.unmodifiableList(dishes);
    }

    public List<ComboMeal> getCombos() {
        return combos;
    }

    public boolean hasCombos() {
        return !combos.isEmpty();
    }

    /** Dishes followed by combos. */
    public List<OrderItem> getItems() {
        List<OrderItem> all = new ArrayList<>(dishes);
        all.addAll(combos);
        return Collections.unmodifiableList(all);
    }
}
