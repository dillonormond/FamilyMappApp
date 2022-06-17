package service;
import dao.*;
import model.*;
import ReqRes.*;

import java.sql.Connection;

/**
 * fills database with more than one user worth of data
 */
public class LoadService extends ClearService{


    /**
     * fills database with multiple users, persons, and events
     */
    public LoadResult load(LoadRequest request){
        ClearResult cr = clear();
        if(cr.getSuccess()){
            try{
                loadUsers(request.getUsers());
                loadPersons(request.getPersons());
                loadEvents(request.getEvents());

                int i = request.getEvents().length;
                LoadResult lr = new LoadResult();
                String message = String.format("Successfully added %d users, %d persons, and %d events to the database.", request.getUsers().length, request.getPersons().length, request.getEvents().length);
                lr.successBody(message);
                return lr;
            } catch (DataAccessException e) {
                e.printStackTrace();
                LoadResult lr = new LoadResult();
                lr.failBody("There was an error while loading the data into the database");
                return lr;
            }
        }
        else{
            LoadResult lr = new LoadResult();
            lr.failBody("There was an error clearing the database during load");
            return lr;
        }
    }


    public void loadUsers(User[] users) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        UserDao uDao = new UserDao(conn);
        for(int i = 0; i < users.length; i++){
            User tempUser = users[i];
            uDao.createUser(tempUser);
        }
        db.closeConnection(true);
    }
    public void loadPersons(Person[] persons) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        PersonDao pDao = new PersonDao(conn);
        for(int i = 0; i < persons.length; i++){
            Person tempPerson = persons[i];
            pDao.createPerson(tempPerson);
        }
        db.closeConnection(true);
    }
    public void loadEvents(Event[] events) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        EventDao eDao = new EventDao(conn);
        for(int i = 0; i < events.length; i++){
            Event tempEvent = events[i];
            eDao.insert(tempEvent);
        }
        db.closeConnection(true);
    }
}
