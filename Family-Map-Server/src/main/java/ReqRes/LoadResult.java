package ReqRes;

/**
 * result of the Load service
 */
public class LoadResult {
    /**
     * message to be output in body
     */
    private String message;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;

    /**
     * Body of successful Load
     * @param message message to be printed
     */
    public void successBody(String message){
        this.message = message;
        success = true;
    }

    /**
     * body for failed load
     * @param message message to be printed
     */
    public void failBody(String message){
        this.message = message;
        success = false;
    }
}
