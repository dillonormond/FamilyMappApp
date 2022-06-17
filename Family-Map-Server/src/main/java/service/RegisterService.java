package service;
import model.*;
import ReqRes.*;
import dao.*;

import java.io.FileNotFoundException;
import java.sql.Connection;

/**
 * registers user for site and adds them into database
 */
public class RegisterService extends dataGenerator{
    /**
     * holds the input request
     */
    private RegisterRequest rr;
    public RegisterService() throws FileNotFoundException {
        super();
    }

    /**
     * registers the user
     * @return the authToken of user
     */
    public RegisterResult register(RegisterRequest rr) throws DataAccessException {
        Database db = new Database();
        try{
            User tempUser = createUser(rr, db);
            if(tempUser != null) {
                String username = tempUser.getUsername();
                String personID = tempUser.getPersonID();
                Connection conn = db.getConnection();
                AuthTokenDao aDao = new AuthTokenDao(conn);
                aDao.generateAuthToken(username);
                String authToken = aDao.getTokenFromUser(username);

                Person person = new Person(username, rr.getFirstName(), rr.getLastName(), rr.getGender(), personID);

                PersonDao pDao = new PersonDao(conn);
//                pDao.createPerson(person);
                db.closeConnection(true);
                RegisterResult result = new RegisterResult();
                result.successBody(authToken, username, personID);
                generatePersonCaller(rr.getGender(), 4, rr.getUsername(), person);
                return result;
            }
            else{
                RegisterResult result = new RegisterResult();
                result.failBody("Error registering new user");
                return result;
            }
        } catch (DataAccessException e) {
            db.closeConnection(true);
            e.printStackTrace();
            RegisterResult result = new RegisterResult();
            result.failBody("Dataaccess error thrown");
            return result;
        } catch (FileNotFoundException e) {
            db.closeConnection(true);
            e.printStackTrace();
            RegisterResult result = new RegisterResult();
            result.failBody("file not found error");
            return result;
        }

    }

    /**
     * creates the user
     */
    public User createUser(RegisterRequest rr, Database db) throws DataAccessException {
        Connection conn = db.getConnection();
        User newUser = new User(rr.getUsername(), rr.getPassword(), rr.getEmail(), rr.getFirstName(),
                                rr.getLastName(), rr.getGender(), rr.getPersonID());
        UserDao newUserDao = new UserDao(conn);
        newUserDao.createUser(newUser);
        db.closeConnection(true);
        return newUser;
    }


    /**
     * generates the user's authToken
     * @return the authToken
     */
    public String generateAuthToken(){
        return null;
    }
}
