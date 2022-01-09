package exceptions;

import game.Game;

public class UnavailableGameException extends Exception{
    private final Game game;

    public UnavailableGameException(Game game) {
        this.game = game;
    }

    public UnavailableGameException(String message, Game game) {
        super(message);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
