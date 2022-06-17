package service;
import ReqRes.MultipleEventResult;
import ReqRes.MultiplePersonResult;
import dao.*;
import model.*;

import java.sql.Connection;

/**
 * service to find all the events in a user's family tree
 */
public class MultipleEventService {
    /**
     * finds and returns all the events in the tree
     * @return array of events
     *
     */
    public MultipleEventResult returnAllEvents(String authtoken) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();

        AuthTokenDao aDao = new AuthTokenDao(conn);
        String currentUsername = aDao.getUserFromToken(authtoken);

        UserDao uDao = new UserDao(conn);
        User user = uDao.getUserById(currentUsername);
        Event[] eventArray = null;
        if(user != null){
            EventDao eDao = new EventDao(conn);
            eventArray = eDao.findUserEvents(user.getUsername());
        }
        db.closeConnection(true);

        if(eventArray != null){
            MultipleEventResult result = new MultipleEventResult();
            result.successBody(eventArray);
            return result;
        }
        else{
            MultipleEventResult result = new MultipleEventResult();
            result.failBody("Error: problem returning user's events");
            return result;
        }
    }
}
