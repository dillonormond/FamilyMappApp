package ReqRes;

import model.Person;

/**
 * result of request to find a single person in the database
 */
public class SinglePersonResult {

    /**
     * personID of found person
     */
    private String personID;
    /**
     * username associated with found person
     */
    private String associatedUsername;
    /**
     * first name of found person
     */
    private String firstName;
    /**
     * last name of found person
     */
    private String lastName;
    /**
     * gender of found person
     */
    private String gender;
    /**
     * fatherID of found person. Can be null
     */
    private String fatherID;
    /**
     * motherID of found person. Can be null
     *
     */
    private String motherID;
    /**
     * spouseID of found person. Can be null
     */
    private String spouseID;
    /**
     * message to be output in body
     */
    private String message;
    /**
     * boolean to show whether request has succeeded
     */
    private boolean success;

    /**
     * result body of a successful request
     */
    public void successBody(Person person){
        this.associatedUsername = person.getAssociatedUsername();
        this.fatherID = person.getFatherID();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.personID = person.getPersonID();
        this.gender = person.getGender();
        this.motherID = person.getMotherID();
        this.spouseID = person.getSpouseID();
        this.success = true;
    }

    /**
     * result body of failed request
     * @param message message to print
     */
    public void failBody(String message){
        this.message = message;
        this.success = false;
    }

    public boolean isSuccess(){
        return success;
    }

}
