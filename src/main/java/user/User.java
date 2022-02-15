package user;

/**
 * User is the main entity that is used to acces the application.
 * It is an abstract class.
 * @author      Marius Bubui
 * @version     1.0
 */
public abstract class User {
    /**
     * User id from the database.
     */
    protected final int id;

    /**
     * User's personal email.
     */
    protected String email;

    /**
     * User's encrypted password hash.
     */
    protected String passwordHash;

    /**
     * Class constructor specifying the user id, email and password hash.
     *
     * @param id user id
     * @param email user email
     * @param passwordHash user's password hash
     */
    public User(int id, String email, String passwordHash) {
        if (id < 0)
            throw new IllegalArgumentException("Invalid ID!");
        this.id = id;

        if(!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"))
            throw new IllegalArgumentException("Invalid email!");
        this.email = email;

        if(passwordHash == null || passwordHash.length() == 0)
            throw new IllegalArgumentException("Password hash can't be empty!");

        this.passwordHash = passwordHash;
    }

    /**
     * Transforms the object into a string.
     */
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' + '}';
    }

    /**
     * Getter for the user id.
     * @return user's id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the user email.
     * @return user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for the password hash.
     * @return user's password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Setter for email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter for password hash.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
