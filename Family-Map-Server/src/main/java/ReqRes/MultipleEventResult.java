package ReqRes;
import model.*;

/**
 * result of finding all the events in a user's family tree
 */
public class MultipleEventResult {
    /**
     * list of events found in family tree
     */
    private Event[] data;
    /**
     * message to be output in body
     */
    private String message;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;

    /**
     * body for successfully finding all events
     * @param events events found

     */
    public void successBody(Event[] events){
        this.data = events;
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
