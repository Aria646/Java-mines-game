package mines.view;

import mines.controller.Controller;

/**
 * An abstract view of the fine minery game,
 * A view can be started with a game controller to display and control the game.
 */
public interface View {

    /**
     * Initialise the view, and pair it with a {@link mines.controller.GameController}.
     *
     * @param controller the controller for the game.
     */
    void start(Controller controller);
}
