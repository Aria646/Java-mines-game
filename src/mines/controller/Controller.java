package mines.controller;

import mines.model.Game;
import mines.model.GemPool;
import mines.model.Position;

/**
 * An interface for the game's controller.
 */
public interface Controller {
    /**
     * Get the game instance that is being controlled.
     *
     * @return the game instance.
     */
    Game getGame();

    /**
     * Add an event that will be triggered whenever the game state changes.
     *
     * @param event the event to be triggered.
     */
    void addObserver(GameEvent event);

    /**
     * Command the game to attempt to have the current player collect the specified gems.
     *
     * @param gems the gems to be collected.
     * @return true if the command succeeded, false otherwise.
     */
    boolean collect(GemPool gems);

    /**
     * Command the game to attempt to have the current player purchase the specified mine.
     *
     * @param position the position of the mine tp be purchased.
     * @return true if the command succeeded, false otherwise.
     */
    boolean purchase(Position position);
}
