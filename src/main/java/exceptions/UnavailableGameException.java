package exceptions;

import game.Game;

/**
 * Exception that is used when a game
 * that is unavailable is tried to be ordered.
 */
public class UnavailableGameException extends Exception{
    /**
     * The game in question.
     */
    private final Game game;

    /**
     * Constructor specifying the game
     * @param game the game in question
     */
    public UnavailableGameException(Game game) {
        this.game = game;
    }

    /**
     * Getter for the game
     * @return the game
     */
    public Game getGame() {
        return game;
    }
}
