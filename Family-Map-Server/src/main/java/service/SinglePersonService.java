package service;
import ReqRes.SinglePersonResult;
import dao.*;
import model.*;

import java.sql.Connection;

/**
 * finds a single person from the database given the personID
 */
public class SinglePersonService {
    /**
     * ID unique to person found
     */
    private String personID;
    /**
     * person object found
     */
    private Person person;

    /**
     * sets ID from path
     * @param ID Id from path
     */

    /**
     * find the person by the personID
     * @param ID
     */
    public SinglePersonResult findPersonByID(String ID, String authToken) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();

        AuthTokenDao aDao = new AuthTokenDao(conn);
        String currentUsername = aDao.getUserFromToken(authToken);

        UserDao uDao = new UserDao(conn);
        User user = uDao.getUserById(currentUsername);
        PersonDao pDao = new PersonDao(conn);
        Person person = pDao.findPerson(ID);
        db.closeConnection(true);
        if(user != null && person != null) {
            if (user.getUsername().equals(person.getAssociatedUsername())) {
                SinglePersonResult result = new SinglePersonResult();
                result.successBody(person);
                return result;
            }
            else{
                SinglePersonResult result = new SinglePersonResult();
                result.failBody("There was an error finding person in single person service");
                return result;
            }
        }
        else{
            SinglePersonResult result = new SinglePersonResult();
            result.failBody("There was an error finding person in single person service");
            return result;
        }
    }

    public Person getPerson(){
        return person;
    }

}
