package user;

public class Admin extends User {

    public Admin(int id, String email, String password) {
        super(id, email, password);
    }

    @Override
    public String toString() {
        return "Admin{ " + super.toString() + " }";
    }
}
