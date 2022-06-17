package ReqRes;

/**
 * Result of the Login attempt
 */
public class LoginResult {
    /**
     * username of user after login
     */
    private String username;
    /**
     * personID for user after login
     */
    private String personID;
    /**
     * auth token after user login
     */
    private String authtoken;
    /**
     * message to be output in body
     */
    private String message;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;

    /**
     * body of a successful login
     * @param username username of user
     * @param personID personID of user
     * @param authToken authToken of user
     */
    public void successBody(String username, String personID,String authToken){
        this.username = username;
        this.personID = personID;
        this.authtoken = authToken;
        success = true;
    }

    /**
     * body of failed login
     * @param message message to be printed
     */
    public void failBody(String message){
        this.message = message;
        success = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAuthToken() {
        return authtoken;
    }

    public void setAuthToken(String authToken) {
        this.authtoken = authToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
