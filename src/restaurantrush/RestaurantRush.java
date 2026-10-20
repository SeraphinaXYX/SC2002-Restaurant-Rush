package restaurantrush;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Entry point. Run with --echo when feeding moves from a file.
 *
 * @author Group XX
 */
public class RestaurantRush {
    public static void main(String[] args) {
        GameView view = new GameView(new Scanner(System.in), Arrays.asList(args).contains("--echo"));
        int stage = view.askStage(2);
        long seed = view.askSeed();
        new GameController(new GameConfig(stage, seed), view).run();
    }
}
