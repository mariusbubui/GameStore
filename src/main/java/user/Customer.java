package user;

import order.Order;
import order.ShoppingCart;

import java.util.List;

/**
 * Customer is an entity derived from User that offers the
 * possibility to interact with the store.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public class Customer extends User {
    /**
     * Customer's first name.
     */
    private String firstName;

    /**
     * Customer's last name.
     */
    private String lastName;

    /**
     * Customer's phone number.
     */
    private String phoneNumber;

    /**
     * Customer's number of orders.
     */
    private int numberOfOrders;

    /**
     * Customer's orders represented as a list.
     */
    private List<Order> orders;

    /**
     * Customer's shopping cart represented as a map.
     */
    private ShoppingCart shoppingCart;

    /**
     * Class constructor specifying the customer id, email, password hash, first and last name,
     * phone number, orders and shopping cart.
     *
     * @param id customer id
     * @param email customer email
     * @param password customer's password hash
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param phoneNumber customer's phone number
     * @param orders customer's orders
     * @param shoppingCart customer's shopping cart
     */
    public Customer(int id, String email, String password, String firstName, String lastName, String phoneNumber, List<Order> orders, ShoppingCart shoppingCart) {
        super(id, email, password);

        if(!firstName.matches("^[a-zA-Z]{2,}$") || !lastName.matches("^[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Invalid name!");
        this.firstName = firstName;
        this.lastName = lastName;

        if(!phoneNumber.matches("^[0-9]{5,}$"))
            throw new IllegalArgumentException("Invalid phone number!");
        this.phoneNumber = phoneNumber;

        this.orders = orders;
        if(orders != null)
            this.numberOfOrders = orders.size();
        else
            this.numberOfOrders = 0;

        this.shoppingCart = shoppingCart;
    }

    /**
     * Transforms the object into a string.
     */
    @Override
    public String toString() {
        return "Customer{" + "email='" + email + '\'' + ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' + ", phoneNumber='" + phoneNumber + '\'' +
                ", orders=" + orders + ", cart=" + shoppingCart + '}';
    }

    /**
     * Getter for the first name.
     * @return customer's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for the last name.
     * @return customer's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for the phone number.
     * @return customer's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Getter for the orders.
     * @return customer's orders
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Getter for the shopping cart.
     * @return customer's shopping cart
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Getter for the number of orders.
     * @return customer's number of orders
     */
    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    /**
     * Setter for the first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Setter for the last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Setter for the phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Setter for the orders.
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;

        if(orders != null)
            this.numberOfOrders = orders.size();
        else
            this.numberOfOrders = 0;
    }

    /**
     * Setter for the shopping cart.
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    /**
     * Setter for the number of orders.
     */
    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }
}
