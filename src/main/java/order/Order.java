package order;

import game.Game;

import java.time.LocalDate;
import java.util.Map;

public class Order {
        private final int id;
        private Map<Game, Integer> order;
        private double totalPrice;
        private boolean discounted;
        private LocalDate orderDate;

        public Order(int id, Map<Game, Integer> order, double totalPrice, LocalDate orderDate) {
                this.id = id;

                this.order = order;

                if (totalPrice < 0)
                        throw new IllegalArgumentException("order.Order price can't be negative!");
                this.totalPrice = totalPrice;

                if (orderDate.compareTo(LocalDate.now()) > 0)
                        throw new IllegalArgumentException("Order date must be a past date!");
                this.orderDate = orderDate;
        }

        public Order(int id, Map<Game, Integer> order, LocalDate orderDate) {
                this.id = id;
                this.order = order;

                for (Map.Entry<Game, Integer> entry : this.order.entrySet()) {
                        totalPrice += entry.getKey().getPrice() * entry.getValue();
                }

                this.orderDate = orderDate;
        }

        @Override
        public String toString() {
                return "order.Order{" + "id=" + id + ", order=" + order +
                        ", totalPrice=" + totalPrice + ", orderDate=" + orderDate + '}';
        }

        public int getId() {
                return id;
        }

        public Map<Game, Integer> getOrder() {
                return order;
        }

        public double getTotalPrice() {
                return totalPrice;
        }

        public LocalDate getOrderDate() {
                return orderDate;
        }

        public void setOrder(Map<Game, Integer> order) {
                this.order = order;
        }

        public void setTotalPrice(double totalPrice) {
                this.totalPrice = totalPrice;
        }

        public void setOrderDate(LocalDate orderDate) {
                this.orderDate = orderDate;
        }

        public void setDiscounted(boolean discounted) {
                this.discounted = discounted;
        }
}
