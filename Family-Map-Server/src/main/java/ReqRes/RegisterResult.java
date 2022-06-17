package ReqRes;

import model.AuthToken;
import service.RegisterService;

/**
 * result of register request
 */
public class RegisterResult {
    /**
     * auth token generated after user registers
     */
    private String authtoken;
    /**
     * username of registered user
     */
    private String username;
    /**
     * personID of registered user
     */
    private String personID;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;
    /**
     * error message to print
     */
    private String message;

    /**
     * body for successful request
     * @param authToken authToken of user
     * @param username username of user
     * @param personID personID of user
     */
    public void successBody(String authToken, String username, String personID){
        this.authtoken = authToken;
        this.username = username;
        this.personID = personID;
        success = true;
    }

    /**
     * body for failed request
     * @param errorMessage error message to print
     */
    public void failBody(String errorMessage){
        message = errorMessage;
        success = false;
    }

    public String getAuthToken() {
        return authtoken;
    }

    public void setAuthToken(String authToken) {
        this.authtoken = authToken;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return message;
    }

    public void setError(String error) {
        this.message = error;
    }
}
