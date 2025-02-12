package server;

public class Profile {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private boolean loggedIn = false;


    public Profile(){}

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password){
        if (this.password == null) {
            return false;
        }
        return this.password.equals(password);
    }



    public String getPassword() {
        return password;
    }
}
