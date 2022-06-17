package ReqRes;

import model.*;

/**
 * Result of finding and returning all persons in a user's family tree
 */
public class MultiplePersonResult {
    /**
     * list of persons found in family tree
     */
    private Person[] data;
    /**
     * message to be output in body
     */
    private String message;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;

    /**
     * body for successful attempt
     * @param persons list of persons in family tree
     */
    public void successBody(Person[] persons){
        this.data = persons;
        this.success = true;
    }

    /**
     * body for failed attempt
     * @param message message to be printed
     */
    public void failBody(String message){
        this.message = message;
        this.success = false;
    }

    public boolean isSuccess(){
        return success;
    }
}
