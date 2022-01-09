package user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new Admin(-100, "mail@gmail.com", "hash"));
        assertThrows(IllegalArgumentException.class, () -> new Admin(100, "", "hash"));
        assertThrows(IllegalArgumentException.class, () -> new Admin(100, "mail@gmail.com", null));
    }

    @Test
    void getId() {
        Admin admin = new Admin(100, "mail@gmail.com", "hash");
        assertEquals(100, admin.getId());
    }

    @Test
    void getEmail() {
        Admin admin = new Admin(100, "mail@gmail.com", "hash");
        assertEquals("mail@gmail.com", admin.getEmail());
    }

    @Test
    void getPasswordHash() {
        Admin admin = new Admin(100, "mail@gmail.com", "hash");
        assertEquals("hash", admin.getPasswordHash());
    }

    @Test
    void setEmail() {
        Admin admin = new Admin(100, "mail@gmail.com", "hash");
        admin.setEmail("new_mail@yahoo.com");
        assertEquals("new_mail@yahoo.com", admin.getEmail());
    }

    @Test
    void setPasswordHash() {
        Admin admin = new Admin(100, "mail@gmail.com", "hash");
        admin.setPasswordHash("new_hash");
        assertEquals("new_hash", admin.getPasswordHash());
    }

    @Test
    void testToString() {
        Admin admin = new Admin(100, "mail@gmail.com", "hash");
        assertEquals("Admin{ User{id=100, email='mail@gmail.com', passwordHash='hash'} }", admin.toString());
    }
}