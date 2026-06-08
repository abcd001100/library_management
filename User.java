public class User {
    private String name;
    private String address;
    private String contactNumber;
    private String email;
    private String username;
    private String password;

    public User(String name, String address, String contactNumber,
                String email, String username, String password) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getName()          { return name; }
    public String getAddress()       { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail()         { return email; }
    public String getUsername()      { return username; }
    public String getPassword()      { return password; }

    // Setters (for update personal info)
    public void setAddress(String address)             { this.address = address; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setEmail(String email)                 { this.email = email; }
    public void setPassword(String password)           { this.password = password; }

    @Override
    public String toString() {
        return "Name: " + name + " | Username: " + username +
               " | Email: " + email + " | Contact: " + contactNumber;
    }
}