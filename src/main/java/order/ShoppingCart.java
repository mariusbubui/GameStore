package order;

import game.Game;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private Map<Game, Integer> cart;
    private double totalPrice = 0.0;

    public ShoppingCart() {
        cart = new HashMap<>();
    }

    public ShoppingCart(Map<Game, Integer> cart) {
        this.cart = cart;

        for (Map.Entry<Game, Integer> entry : this.cart.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }
    }

    @Override
    public String toString() {
        return "ShoppingCart{" + "games=" + cart + ", totalPrice=" + totalPrice + '}';
    }

    public Map<Game, Integer> getCart() {
        return cart;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void addGame(Game game, int quantity){
        cart.put(game, cart.containsKey(game) ? cart.get(game) + quantity : quantity);
        totalPrice += game.getPrice() * quantity;
    }

    public void removeGame(Game game, int quantity) {
        totalPrice -= game.getPrice() * Math.min(quantity, cart.get(game));

        if(cart.get(game) <= quantity){
            cart.remove(game);
        } else {
            cart.put(game, cart.get(game) - quantity);
        }
    }

    public void setCart(Map<Game, Integer> cart) {
        this.cart = cart;

        for (Map.Entry<Game, Integer> entry : this.cart.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }
    }

}
