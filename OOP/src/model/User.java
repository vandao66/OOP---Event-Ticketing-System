package model;

public abstract class User {
    private final String username;
    private final String password;

    public User(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống!");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống!");
        }

        this.username = username.trim();
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public abstract String role();

    @Override
    public String toString() {
        return String.format("User[username=%s, role=%s]", username, role());
    }
}