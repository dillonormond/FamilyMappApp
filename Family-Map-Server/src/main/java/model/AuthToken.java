package model;

/**
 * model for authToken object
 */
public class AuthToken {
    /**
     * unique token that is paired with each user
     */
    private String authtoken;
    /**
     * unique username
     */
    private String username;

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
