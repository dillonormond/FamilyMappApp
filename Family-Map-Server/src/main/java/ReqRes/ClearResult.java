package ReqRes;

/**
 * result of the clear service
 */
public class ClearResult {
    /**
     * message to be output in body
     */
    private String message;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;

    /**
     * body for successful clear
     */
    public void successBody(){
        success = true;
        message = "Clear Succeeded";
    }

    /**
     * body for failed claer
     */
    public void failBody(){
        success = false;
        this.message = "clear failed";
    }

    public boolean getSuccess(){
        return success;
    }


}
