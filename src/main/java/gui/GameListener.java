package gui;

import game.Game;

/**
 * GameListener is an interface that contains the
 * header of the methode that is a listener for
 * a game.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public interface GameListener {
    /**
     * Methode that  acts as a listener
     * for a game.
     *
     * @param game the game that it listens for
     */
    void onClickListener(Game game);
}
