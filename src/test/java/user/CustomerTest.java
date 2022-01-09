package user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTest {

    @Test
    void constructor() {
    }

    @Test
    void testToString() {
    }

    @Test
    void getFirstName() {
        Customer customer = new Customer(100, "mail@gmail.com", "hash", "first", "last", "0774789456", null  ,null);
        assertEquals("first", customer.getFirstName());
    }

    @Test
    void getLastName() {
        Customer customer = new Customer(100, "mail@gmail.com", "hash", "first", "last", "0774789456", null  ,null);
        assertEquals("last", customer.getLastName());
    }

    @Test
    void getPhoneNumber() {
        Customer customer = new Customer(100, "mail@gmail.com", "hash", "first", "last", "0774789456", null  ,null);
        assertEquals("0774789456", customer.getPhoneNumber());
    }

    @Test
    void getOrders() {
    }

    @Test
    void getCart() {
    }

    @Test
    void setFirstName() {
        Customer customer = new Customer(100, "mail@gmail.com", "hash", "first", "last", "0774789456", null  ,null);
        customer.setFirstName("new_first");
        assertEquals("new_first", customer.getFirstName());
    }

    @Test
    void setLastName() {
        Customer customer = new Customer(100, "mail@gmail.com", "hash", "first", "last", "0774789456", null  ,null);
        customer.setLastName("new_last");
        assertEquals("new_last", customer.getLastName());
    }

    @Test
    void setPhoneNumber() {
        Customer customer = new Customer(100, "mail@gmail.com", "hash", "first", "last", "0774789456", null  ,null);
        customer.setPhoneNumber("0740456213");
        assertEquals("0740456213", customer.getPhoneNumber());
    }

    @Test
    void setOrders() {
    }

    @Test
    void setCart() {
    }
}