package model.entities;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String role; // "CUSTOMER", "SECRETARY", "ADMIN"

    public User() {}

    public User(int userId, String username, String passwordHash, String role) {
        this.userId       = userId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
    }

    public int getUserId()          { return userId; }
    public String getUsername()     { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole()         { return role; }

    public void setUserId(int userId)             { this.userId = userId; }
    public void setUsername(String username)       { this.username = username; }
    public void setPasswordHash(String ph)         { this.passwordHash = ph; }
    public void setRole(String role)               { this.role = role; }

    @Override
    public String toString() {
        return username + " [" + role + "]";
    }
}
