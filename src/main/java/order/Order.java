package order;

import game.Game;

import java.time.LocalDate;
import java.util.Map;

/**
 * Oder is an entity that is used to store
 * an abstract representation of a customer's order.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public class Order {
        /**
         * The order id from the database.
         */
        private final int id;

        /**
         * The actual order represented by a map.
         */
        private Map<Game, Integer> order;

        /**
         * The total price of the order.
         */
        private double totalPrice;

        /**
         * Variable representing whether the
         * order is discounted or not.
         */
        private boolean discounted = false;

        /**
         * The date of the order.
         */
        private final LocalDate orderDate;

        /**
         * Class constructor specifying the order id, items, quantity and date.
         *
         * @param id id of the order
         * @param order map representing the order
         * @param orderDate date of the order
         */
        public Order(int id, Map<Game, Integer> order, LocalDate orderDate) {
                this.id = id;
                this.order = order;

                for (Map.Entry<Game, Integer> entry : this.order.entrySet()) {
                        totalPrice += entry.getKey().getPrice() * entry.getValue();
                }

                this.orderDate = orderDate;
        }

        /**
         * Transforms the object into a string.
         */
        @Override
        public String toString() {
                return "order.Order{" + "id=" + id + ", order=" + order +
                        ", totalPrice=" + totalPrice + ", orderDate=" + orderDate + '}';
        }

        /**
         * Getter for the id.
         * @return the id of the order
         */
        public int getId() {
                return id;
        }

        /**
         * Getter fot the order
         * @return map representing the order
         */
        public Map<Game, Integer> getOrder() {
                return order;
        }

        /**
         * Getter for the total price.
         * @return the total price
         */
        public double getTotalPrice() {
                return totalPrice;
        }

        /**
         * Setter for the order
         */
        public void setOrder(Map<Game, Integer> order) {
                this.order = order;
        }

        /**
         * Setter for the total price.
         */
        public void setTotalPrice(double totalPrice) {
                this.totalPrice = totalPrice;
        }

        /**
         * Setter for the discount.
         */
        public void setDiscounted(boolean discounted) {
                this.discounted = discounted;
        }
}
