package service;

import ReqRes.FillResult;
import dao.*;
import model.Person;
import model.User;
import passoffrequest.FillRequest;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fills the database with the family tree for a user
 */
public class FillService extends dataGenerator{
    public FillService() throws FileNotFoundException {
    }

    /**
     * Fills database
     */
    public FillResult fill(FillRequest fr) throws DataAccessException, FileNotFoundException, SQLException {
        try {
            Database db = new Database();
            Connection conn = db.getConnection();

            PersonDao pDao = new PersonDao(conn);
            pDao.deleteUsersPersons(fr.getUsername());

            EventDao eDao = new EventDao(conn);
            eDao.deleteUsersEvents(fr.getUsername());

            UserDao uDao = new UserDao(conn);
            User user = uDao.getUserById(fr.getUsername());
            Person person = new Person(fr.getUsername(), user.getFirstName(), user.getLastName(), user.getGender());
            int eventsBefore = eDao.getNumRows();
            int personsBefore = pDao.getNumRows();
            db.closeConnection(true);

            generatePersonCaller(user.getGender(), fr.getGenerations(), fr.getUsername(), person);

            conn = db.getConnection();
            eDao = new EventDao(conn);
            pDao = new PersonDao(conn);
            int newEvents = eDao.getNumRows() - eventsBefore;
            int newPersons = pDao.getNumRows() - personsBefore;

            db.closeConnection(true);

            FillResult result = new FillResult();
            result.successBody(newPersons, newEvents);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            FillResult result = new FillResult();
            result.failBody("dataaccess exception");
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            FillResult result = new FillResult();
            result.failBody("sql exception");
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            FillResult result = new FillResult();
            result.failBody("file not found exception");
            return result;
        }
    }

    /**
     * fills database with a deault of 4 gens if number of generations is not specified
     * @param username username to fill in family tree for
     */
    public void defaultFillDB(String username){
        int numGenerations = 4;
    }



}
