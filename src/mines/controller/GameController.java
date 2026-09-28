package mines.controller;

import mines.model.Game;
import mines.model.GemPool;
import mines.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * A Controller for the Fine Minery game.
 */
public class GameController implements Controller {

    /**
     * The game model.
     */
    private final Game game;

    /**
     * A list of events to trigger whenever the game state changes.
     */
    private final List<GameEvent> observers;

    /**
     * Create a new Game controller for a specified game.
     *
     * @param game the game this controller controls.
     */
    public GameController(Game game) {
        this.game = game;
        this.observers = new ArrayList<>();
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void addObserver(GameEvent event) {
        observers.add(event);
    }

    @Override
    public boolean collect(GemPool gems) {
        boolean success = game.attemptCollectCoins(gems);
        if (success) {
            observers.forEach(GameEvent::trigger);
        }
        return success;
    }

    @Override
    public boolean purchase(Position position) {
        boolean success = game.attemptPurchase(position);
        if (success) {
            observers.forEach(GameEvent::trigger);
        }
        return success;
    }
}
