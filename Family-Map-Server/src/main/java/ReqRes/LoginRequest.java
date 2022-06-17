package ReqRes;
import dao.*;

/**
 * request to login to user account given a username and password
 */
public class LoginRequest {
    /**
     * username to be used in login attempt
     */
    String username;
    /**
     * password to be used in login attempt
     */
    String password;

    public LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
    }

    /**
     * attempts to login to account
     * @return authToken
     */
    public String login(){
        //return authtoken
        return null;
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
}
