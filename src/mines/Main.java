package mines;

import mines.controller.GameController;
import mines.model.Game;
import mines.view.TextView;

/**
 * Main entry point for the program.
 */
public class Main {

    /**
     * Main entry point method for the program.
     *
     * @param args args given to the program when it is run.
     */
    public static void main(String[] args) {
        new TextView().start(new GameController(new Game()));
    }
}
