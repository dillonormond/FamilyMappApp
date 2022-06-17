package service;
import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.*;
import ReqRes.*;

import java.sql.Connection;

/**
 * service to login the user
 */
public class LoginService {
    /**
     * holds the input request
     */
    public LoginService(){

    }

    /**
     * logs in the user
     * @return authToken string
     */
    public LoginResult loginUser(LoginRequest request) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        UserDao loginUser = new UserDao(conn);
        User tempUser = loginUser.login(request.getUsername(), request.getPassword());
        if(tempUser != null){
            String username = tempUser.getUsername();
            String personID = tempUser.getPersonID();
            db.closeConnection(true);
            conn = db.getConnection();
            AuthTokenDao aDao = new AuthTokenDao(conn);
            String authToken = aDao.getTokenFromUser(username);
            if(authToken == null){
                aDao.generateAuthToken(username);
                authToken = aDao.getTokenFromUser(username);
            }
            db.closeConnection(true);
            LoginResult result = new LoginResult();
            result.successBody(username, personID, authToken);
            return result;
        }
        else{
            db.closeConnection(true);
            LoginResult result = new LoginResult();
            result.failBody("Login error occurred");
            return result;
        }
    }

}
