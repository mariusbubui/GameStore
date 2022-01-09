package user;

import order.Order;
import order.ShoppingCart;

import java.util.List;

public class Customer extends User {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int numberOfOrders;
    private List<Order> orders;
    private ShoppingCart shoppingCart;

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

    @Override
    public String toString() {
        return "Customer{" + "email='" + email + '\'' + ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' + ", phoneNumber='" + phoneNumber + '\'' +
                ", orders=" + orders + ", cart=" + shoppingCart + '}';
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;

        if(orders != null)
            this.numberOfOrders = orders.size();
        else
            this.numberOfOrders = 0;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }
}
