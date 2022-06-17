package dao;

import model.AuthToken;
import model.Event;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import javax.xml.crypto.Data;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDAOTest {
    private Database db;
    private User userToInsert;
    private User userToInsert2;
    private AuthTokenDao aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new Database();
        // and a new event with random data

        ClearService service = new ClearService();
        service.clear();
        userToInsert = new User("dormonddd", "pw", "dillonormond@gmail.com",
                "dillon", "ormond", "m", "testID");
        userToInsert2 = new User("jack", "hello", "jack@gmail.com",
                "jack", "tilly", "m", "ssjdid");

        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        UserDao uDao = new UserDao(conn);
        uDao.createUser(userToInsert);
        uDao.createUser(userToInsert2);
        //Then we pass that connection to the EventDAO, so it can access the database.
        aDao = new AuthTokenDao(conn);
        //Let's clear the database as well so any lingering data doesn't affect our tests

    }
    @AfterEach
    public void tearDown() {
        // Here we close the connection to the database file, so it can be opened again later.
        // We will set commit to false because we do not want to save the changes to the database
        // between test cases.
        db.closeConnection(false);
    }
    @Test
    public void generateTokenTest() throws DataAccessException{
        aDao.generateAuthToken("dormonddd");
       // assertNotNull();
    }

    @Test
    public void checkToken() throws DataAccessException{
        String usernameFound = aDao.getUserFromToken("y5YJjFaW-GI9zF3YDK1A5iKTJB2hbRVg");
        System.out.println(usernameFound);
        assertEquals("dormond1", usernameFound);

    }
    @Test
    public void checkUser() throws DataAccessException {
        String token = aDao.getTokenFromUser("dormond1");
        System.out.println(token);
        assertEquals("y5YJjFaW-GI9zF3YDK1A5iKTJB2hbRVg", token);
    }
}
