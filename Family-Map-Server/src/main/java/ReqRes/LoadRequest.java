package ReqRes;
import model.*;

/**
 * request to Load the DB given arrays of users, persons, and events
 */
public class LoadRequest {
    /**
     * list of users to put into database
     */
    private User[] users;
    /**
     * list of persons to add to database
     */
    private Person[] persons;
    /**
     * list of events to add to database
     */
    private Event[] events;

    public LoadRequest(User[] users, Person[] persons, Event[] events){

    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}
