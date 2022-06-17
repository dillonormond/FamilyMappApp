package ReqRes;

/**
 * Result of Fill service
 */
public class FillResult {
    /**
     * message to be output in body
     */
    private String message;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;

    /**
     * body for successfull fill
     */
    public void successBody(int persons, int events){
        success = true;
        message = String.format("Successfully added %d persons and %d events to the database.", persons, events);
    }

    /**
     * body for failed fill
     * @param message message to be printed
     */
    public void failBody(String message){
        success = false;
        this.message = message;
    }
}
