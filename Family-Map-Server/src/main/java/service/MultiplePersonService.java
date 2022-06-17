package service;
import ReqRes.MultiplePersonResult;
import dao.*;
import model.*;

import java.sql.Connection;

/**
 * finds and returns all persons in a user's family tree
 */
public class MultiplePersonService {
    /**
     * finds all the people in a family tree given a username
     */
    public MultiplePersonResult returnFamilyTree(String authtoken) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();

        AuthTokenDao aDao = new AuthTokenDao(conn);
        String currentUsername = aDao.getUserFromToken(authtoken);

        UserDao uDao = new UserDao(conn);
        User user = uDao.getUserById(currentUsername);
        Person[] personArray = null;
        if(user != null) {
            PersonDao pDao = new PersonDao(conn);
            personArray = pDao.getAllPersonsForUser(user.getUsername());
        }
        db.closeConnection(true);

        if(personArray != null){
            MultiplePersonResult result = new MultiplePersonResult();
            result.successBody(personArray);
            return result;
        }
        else{
            MultiplePersonResult result = new MultiplePersonResult();
            result.failBody("Error: problem returning user's family tree");
            return result;
        }
    }

}
