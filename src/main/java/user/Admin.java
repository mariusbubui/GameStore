package user;

/**
 * Admin is an entity derived from User that has greater privileges.
 *
 * @author      Marius Bubui
 * @version     1.0
 */
public class Admin extends User {
    /**
     * Class constructor specifying the user id, email and password hash.
     * It calls User's constructor.
     *
     * @param id admin's id
     * @param email admin's email
     * @param passwordHash admin's password hash
     */
    public Admin(int id, String email, String passwordHash) {
        super(id, email, passwordHash);
    }

    /**
     * Transforms the object into a string.
     */
    @Override
    public String toString() {
        return "Admin{ " + super.toString() + " }";
    }
}
