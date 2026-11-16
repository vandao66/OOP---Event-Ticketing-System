package services;

import database.DatabaseManager;
import exception.AuthenticationException;
import model.*;

public class Auth {
    private final DatabaseManager db;

    public Auth(DatabaseManager db) {
        this.db = db;
        seedDefaultUsers();
    }
    // 3 tài khoản mẫu
    private void seedDefaultUsers() {
        if (!db.userExists("admin")) db.saveUser(new Admin("admin", "123"));
        if (!db.userExists("staff")) db.saveUser(new Staff("staff", "123"));
        if (!db.userExists("customer")) db.saveUser(new Customer("customer", "123"));
    }

   public User login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty())
            throw new AuthenticationException("Tên đăng nhập không được để trống!");

        if (password == null || password.trim().isEmpty())
            throw new AuthenticationException("Mật khẩu không được để trống!");

        User user = db.getUser(username.trim());
        if (user == null || !user.checkPassword(password))
            throw new AuthenticationException("Tài khoản hoặc mật khẩu không chính xác!");

        return user;
    }

    public Customer register(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty())
            throw new AuthenticationException("Tên đăng nhập không được để trống!");

        String cleanUsername = username.trim();
        if (db.userExists(cleanUsername))
            throw new AuthenticationException("Tên tài khoản này đã tồn tại!");

        try {
            Customer customer = new Customer(cleanUsername, password);
            db.saveUser(customer);
            return customer;
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }
}