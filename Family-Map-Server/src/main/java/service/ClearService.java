package service;

import ReqRes.ClearResult;
import dao.*;
import dao.DataAccessException;
import dao.Database;

import java.sql.Connection;

/**
 * clears the entire database
 */
public class ClearService {
    /**
     * clears the whole database
     */
    public ClearResult clear(){
        Database db = new Database();
        try{
            Connection conn = db.getConnection();
            AuthTokenDao aDao = new AuthTokenDao(conn);
            aDao.clear();
            UserDao uDao = new UserDao(conn);
            uDao.clear();
            PersonDao pDao = new PersonDao(conn);
            pDao.clear();
            EventDao eDao = new EventDao(conn);
            eDao.clear();
            db.closeConnection(true);
            ClearResult result = new ClearResult();
            result.successBody();
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            ClearResult result = new ClearResult();
            result.failBody();
            return result;
        }
    }

}
