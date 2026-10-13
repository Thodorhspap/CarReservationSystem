package model.entities;

public class Customer {
    private int customerId;
    private int userId;
    private String fullName;
    private String email;
    private String phone;

    public Customer() {}

    public Customer(int customerId, int userId, String fullName,
                    String email, String phone) {
        this.customerId = customerId;
        this.userId     = userId;
        this.fullName   = fullName;
        this.email      = email;
        this.phone      = phone;
    }

    public int getCustomerId()      { return customerId; }
    public int getUserId()          { return userId; }
    public String getFullName()     { return fullName; }
    public String getEmail()        { return email; }
    public String getPhone()        { return phone; }

    public void setCustomerId(int customerId)   { this.customerId = customerId; }
    public void setUserId(int userId)           { this.userId = userId; }
    public void setFullName(String fullName)    { this.fullName = fullName; }
    public void setEmail(String email)          { this.email = email; }
    public void setPhone(String phone)          { this.phone = phone; }

    @Override
    public String toString() {
        return fullName + " | " + email + " | " + phone;
    }
}