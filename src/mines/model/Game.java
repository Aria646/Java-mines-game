package mines.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A game of Fine Minery.
 * This class manages the player state, the board state, and provides a source of randomness for
 * the mine
 * generation.
 */
public class Game {
    /**
     * the width of grid
     */
    private static final int GRID_WIDTH = 4;
    /**
     * the height of grid
     */
    private static final int GRID_HEIGHT = 3;
    /**
     * the count of player
     */
    private static final int PLAYER_COUNT = 4;
    /**
     * the threshold of victory points of win
     */
    private static final int WIN_VP_THRESHOLD = 15;

    /**
     * the list of player
     */
    private final List<Player> players;
    /**
     * the grid of mines
     */
    private final Mine[][] mineGrid;
    /**
     * the random source for mine generation
     */
    private final Random random;
    /**
     * the index of current player
     */
    private int currentPlayerIndex;
    /**
     * the count of turn
     */
    private int turnCount;
    /**
     * the flag of game over
     */
    private boolean gameOver;

    /**
     * Initialise a new game with 4 players (0 to 3), and a 4 by 3 grid of mines randomly generated
     * with Mine.generate(java.util.Random).
     * Each player begins with 0 gems, 0 mines, and 0 VP. Player 0 is the first player to play.
     */
    public Game() {
        random = new Random();
        currentPlayerIndex = 0;
        players = new ArrayList<>();
        for (int i = 0; i < PLAYER_COUNT; i++) {
            players.add(new Player(i));
        }
        mineGrid = new Mine[GRID_WIDTH][GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                mineGrid[x][y] = Mine.generate(random);
            }
        }
        turnCount = 0;
        gameOver = false;
    }

    /**
     * Attempt to have the current player collect some gems.
     * If this action is successful the turn passes to the next player,
     * or if the game is over it moves to the game over state.
     *
     * @param gems the gems to collect
     * @return whether the gems were successfully collected.
     */
    public boolean attemptCollectCoins(GemPool gems) {
        if (gameOver) {
            return false;
        }

        Player currentPlayer = getCurrentPlayer();
        boolean collectSuccess = currentPlayer.addGems(gems);
        if (collectSuccess) {
            nextPlayerTurn();
            checkGameOver();
        }
        return collectSuccess;
    }

    /**
     * Attempt to have the current player purchase the mine available at a particular position.
     * If the purchase was successful a randomly generated replacement mine is added to the board,
     * then the turn passes to the next player, or if the game is over it moves to the game over
     * state.
     *
     * @param position the position on the game board of the mine.
     * @return whether the purchase was successful.
     */
    public boolean attemptPurchase(Position position) {
        if (gameOver || position == null) {
            return false;
        }
        int x = position.x();
        int y = position.y();
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return false;
        }
        Mine targetMine = mineGrid[x][y];
        Player currentPlayer = getCurrentPlayer();
        boolean purchaseSuccess = currentPlayer.purchase(targetMine);
        if (purchaseSuccess) {
            mineGrid[x][y] = Mine.generate(random);
            nextPlayerTurn();
            checkGameOver();
        }
        return purchaseSuccess;
    }

    /**
     * Get the current player (whose turn it is).
     * If the game is over the current player is the winner.
     *
     * @return the player.
     */
    public Player getCurrentPlayer() {
        if (gameOver) {
            return getWinner();
        }
        return players.get(currentPlayerIndex);
    }

    /**
     * Get the mine at a particular position.
     *
     * @param position the position of the mine to get.
     * @return the mine at that position, or null if the position is out of bounds.
     */
    public Mine getMine(Position position) {
        if (position == null) {
            return null;
        }
        int x = position.x();
        int y = position.y();
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return null;
        }
        return mineGrid[x][y];
    }

    /**
     * Get the mines currently available to purchase.
     * Note: modifying the returned list does not modify this game.
     *
     * @return a copy of the mines available to purchase.
     */
    public List<Mine> getMines() {
        List<Mine> mineList = new ArrayList<>();
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                mineList.add(mineGrid[x][y]);
            }
        }
        return new ArrayList<>(mineList);
    }

    /**
     * Get the players playing this game.
     * The players are in order of play starting with the player who takes the first turn.
     * Note: modifying the returned list does not modify this game.
     * However, modifying the players in the list WILL modify the game.
     *
     * @return a copy of the players.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Return whether the game is over.
     * The game finishes when all the following conditions have been met:
     * 1. At least 1 player has 15 or more Victory Points.
     * 2. There is only 1 player who has the highest score. (no ties)
     * 3. Each player has had the same number of turns. (i.e. it is after player 3, and before
     * player 0s turn)
     *
     * @return whether the game is over.
     */
    public boolean isOver() {
        return gameOver;
    }

    /**
     * change to next player
     */
    private void nextPlayerTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % PLAYER_COUNT;
        turnCount++;
    }

    /**
     * Check if the game meets the end criteria, and if so, set gameOver to true
     */
    private void checkGameOver() {
        boolean allPlayersHadSameTurns = (turnCount % PLAYER_COUNT) == 0;
        if (!allPlayersHadSameTurns) {
            return;
        }

        boolean hasPlayerWithEnoughVictoryPoints = players.stream()
                .anyMatch(p -> p.getVictoryPoints() >= WIN_VP_THRESHOLD);
        if (!hasPlayerWithEnoughVictoryPoints) {
            return;
        }

        int maxVictoryPoints = players.stream()
                .mapToInt(Player::getVictoryPoints)
                .max()
                .orElse(0);

        long topPlayersCount = players.stream()
                .filter(p -> p.getVictoryPoints() == maxVictoryPoints)
                .count();

        if (topPlayersCount == 1) {
            gameOver = true;
        }
    }

    /**
     * Get the winner of the game (only called at the end of the game)
     *
     * @return winner
     */
    private Player getWinner() {
        int maxVictoryPoints = -1;
        Player winner = null;
        for (Player player : players) {
            int vp = player.getVictoryPoints();
            if (vp > maxVictoryPoints) {
                maxVictoryPoints = vp;
                winner = player;
            }
        }
        return winner;
    }
}
