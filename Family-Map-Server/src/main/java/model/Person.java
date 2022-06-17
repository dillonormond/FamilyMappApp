package model;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * model for Person object
 */
public class Person {
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public Person(String personID, String associatedUsername, String firstName, String lastName, String gender,
                  String fatherID, String motherID, String spouseID){
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }
    public Person(String associatedUsername, String firstName, String lastName, String gender){
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        this.personID =  base64Encoder.encodeToString(randomBytes);
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }
    public Person(String associatedUsername, String firstName, String lastName, String gender, String personID){
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Person) {
            Person pUser = (Person) o;
            return pUser.getPersonID().equals((getPersonID())) &&
                    pUser.getGender().equals(getGender()) &&
                    pUser.getFirstName().equals(getFirstName()) &&
                    pUser.getLastName().equals(getLastName()) &&
                    pUser.getAssociatedUsername().equals(getAssociatedUsername()) &&
                    pUser.getFatherID().equals(getFatherID()) &&
                    pUser.getMotherID().equals(getMotherID()) &&
                    pUser.getSpouseID().equals(getSpouseID());
        } else {
            return false;
        }
    }

    /**
     * ID unique to each person
     */
    private String personID;
    /**
     * user this person is relative of
     */
    private String associatedUsername;
    /**
     * person's first name
     */
    private String firstName;
    /**
     * person's last name
     */
    private String lastName;
    /**
     * person's gender
     */
    private String gender;
    /**
     * ID of person's father. Can be null
     */
    private String fatherID;
    /**
     * ID of person's mother. Can be null
     */
    private String motherID;
    /**
     * ID of person's spouse. Can be null
     */
    private String spouseID;

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }
}
