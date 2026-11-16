package model;

public class Customer extends User {

    public Customer(String username, String password) {
        super(username, password);
    }

    @Override
    public String role() {
        return "Customer";
    }
}