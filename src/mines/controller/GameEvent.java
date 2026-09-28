package mines.controller;

/**
 * An interface for events that are triggered whenever there is a change of state in the game.
 */
public interface GameEvent {

    /**
     * The method that is to be called when the game state changes.
     */
    void trigger();
}
