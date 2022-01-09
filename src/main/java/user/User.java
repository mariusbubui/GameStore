package user;

public abstract class User {
    protected final int id;
    protected String email;
    protected String passwordHash;

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

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' + '}';
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
