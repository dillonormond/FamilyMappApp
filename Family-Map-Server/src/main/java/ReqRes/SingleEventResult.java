package ReqRes;

import model.Event;

/**
 * result of request to find a specific event
 */
public class SingleEventResult {
    /**
     * ID of found event
     */
    private String eventID;
    /**
     * username associated with found event
     */
    private String associatedUsername;
    /**
     * person found event is associated with
     */
    private String personID;
    /**
     * latitude of found event
     */
    private float latitude;
    /**
     * longitude of found event
     */
    private float longitude;
    /**
     * country where found event happened
     */
    private String country;
    /**
     * city where found event happened
     */
    private String city;
    /**
     * what the found event is
     */
    private String eventType;
    /**
     * year the found event happened
     */
    private int year;
    /**
     * message to be output in body
     */
    private String message;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;

    /**
     * body for successful request

     */
    public void successBody(Event event){
        this.associatedUsername = event.getUsername();
        this.eventID = event.getEventID();
        this.success = true;
        this.city = event.getCity();
        this.country = event.getCountry();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        this.year = event.getYear();
        this.eventType = event.getEventType();
        this.personID = event.getPersonID();
    }

    /**
     * body for failed request
     * @param message message to print
     */
    public void failBody(String message){
        this.success = false;
        this.message = message;
    }

    public boolean isSuccess(){
        return success;
    }


}
