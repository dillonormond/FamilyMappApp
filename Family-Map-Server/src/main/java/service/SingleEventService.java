package service;

import ReqRes.SingleEventResult;
import ReqRes.SinglePersonResult;
import dao.*;
import model.Event;
import model.Person;
import model.User;

import java.sql.Connection;

/**
 * find a single event given the event ID from the path
 */
public class SingleEventService {
    /**
     * ID unique to event
     */
    private String eventID;
    /**
     * event object found
     */
    private Event event;

    /**
     * sets the ID
     * @param ID ID from the path
     */

    /**
     * finds event by eventID
     * @param ID eventID to find
     */
    public SingleEventResult findEventByID(String ID, String authToken) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();

        AuthTokenDao aDao = new AuthTokenDao(conn);
        String currentUsername = aDao.getUserFromToken(authToken);

        UserDao uDao = new UserDao(conn);
        User user = uDao.getUserById(currentUsername);
//        PersonDao pDao = new PersonDao(conn);
//        Person person = pDao.findPerson(ID);

        EventDao eDao = new EventDao(conn);
        Event event = eDao.find(ID);
        db.closeConnection(true);
        if(user != null && event != null) {
            if (user.getUsername().equals(event.getUsername())) {
                SingleEventResult result = new SingleEventResult();
                result.successBody(event);
                return result;
            }
            else{
                SingleEventResult result = new SingleEventResult();
                result.failBody("There was an error finding event in event person service");
                return result;
            }
        }
        else{
            SingleEventResult result = new SingleEventResult();
            result.failBody("There was an error finding event in event person service");
            return result;
        }
    }
    public Event getPerson(){
        return event;
    }
}
