package ReqRes;

import java.util.Locale;

/**
 * Request to register new user
 */
public class RegisterRequest {
    /**
     * username to register
     */
    private String username;
    /**
     * password to register
     */
    private String password;
    /**
     * email to register
     */
    private String email;
    /**
     * first name to register
     */
    private String firstName;
    /**
     * last name to register
     */
    private String lastName;
    /**
     * gender to register
     */
    private String gender;
    /**
     * personID to register
     */
    private String personID;


    public RegisterRequest(String username, String password, String email, String firstName,
                           String lastName, String gender, String personID){

        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
