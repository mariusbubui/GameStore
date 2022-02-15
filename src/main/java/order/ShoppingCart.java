package order;

import game.Game;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * ShoppingCart is an entity that is used to store
 * an abstract representation of a shopping cart.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public class ShoppingCart {
    /**
     * Cart items and the corresponding quantities.
     */
    private Map<Game, Integer> cart;

    /**
     * Cart total price.
     */
    private double totalPrice = 0.0;

    /**
     * Default constructor that initializes the cart with an empty map.
     */
    public ShoppingCart() {
        cart = new HashMap<>();
    }

    /**
     * Class constructor specifying the cart items and quantities.
     *
     * @param cart map representing the cart
     */
    public ShoppingCart(Map<Game, Integer> cart) {
        this.cart = cart;

        for (Map.Entry<Game, Integer> entry : this.cart.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }
    }

    /**
     * Transforms the object into a string.
     */
    @Override
    public String toString() {
        return "ShoppingCart{" + "games=" + cart + ", totalPrice=" + totalPrice + '}';
    }

    /**
     * Getter for the cart.
     * @return map representing the cart
     */
    public Map<Game, Integer> getCart() {
        return cart;
    }

    /**
     * Getter for the total price.
     * @return the total price
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Methode that provides the ability to
     * add a game to the cart.
     *
     * @param game the game to be added to the cart
     * @param quantity the number of games
     */
    public void addGame(Game game, int quantity) {
        totalPrice = BigDecimal.valueOf(game.getPrice() * quantity).add(BigDecimal.valueOf(totalPrice)).doubleValue();

        cart.put(game, cart.containsKey(game) ? cart.get(game) + quantity : quantity);
    }

    /**
     * Methode that provides the ability to
     * remove a game from the cart.
     *
     * @param game the game to be removed from the cart
     * @param quantity the number of games
     */
    public void removeGame(Game game, int quantity) {
        totalPrice = BigDecimal.valueOf(totalPrice).subtract(BigDecimal.valueOf(game.getPrice() * Math.min(quantity, cart.get(game)))).doubleValue();

        if (cart.get(game) <= quantity) {
            cart.remove(game);
        } else {
            cart.put(game, cart.get(game) - quantity);
        }
    }

    /**
     * Setter for the cart
     */
    public void setCart(Map<Game, Integer> cart) {
        this.cart = cart;

        for (Map.Entry<Game, Integer> entry : this.cart.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }
    }
}
