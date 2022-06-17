package dao;

import model.Event;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private Database db;
    private User userToInsert;
    private User userToInsert2;
    private UserDao uDao;
    private AuthTokenDao aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new Database();
        // and a new event with random data
        userToInsert = new User("dormond", "pw", "dillonormond@gmail.com",
                "dillon", "ormond", "m", "testID");
        userToInsert2 = new User("jack", "hello", "jack@gmail.com",
                "jack", "tilly", "m", "ssjdid");

        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Then we pass that connection to the EventDAO, so it can access the database.
        uDao = new UserDao(conn);
        aDao = new AuthTokenDao(conn);
        //Let's clear the database as well so any lingering data doesn't affect our tests
        uDao.clear();
    }
    @AfterEach
    public void tearDown() {
        // Here we close the connection to the database file, so it can be opened again later.
        // We will set commit to false because we do not want to save the changes to the database
        // between test cases.
        db.closeConnection(false);
    }
    @Test
    public void insertPass() throws DataAccessException {
        uDao.createUser(userToInsert);
        User compareTest = uDao.getUserById(userToInsert.getUsername());
        assertNotNull(compareTest);
        assertEquals(userToInsert, compareTest);
    }
    @Test
    public void findPass() throws DataAccessException {
        uDao.createUser(userToInsert);
        User compareTest = uDao.getUserById(userToInsert.getUsername());
        assertNotNull(compareTest);
        assertEquals(userToInsert, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException{
        uDao.createUser(userToInsert);
//        User compareTest = uDao.getUserById(userToInsert.getUsername());
//        assertNotNull(compareTest);
//        assertEquals(userToInsert, compareTest);
        assertThrows(DataAccessException.class, () -> uDao.createUser(userToInsert));
    }
    @Test
    public void findFail() throws DataAccessException{
        uDao.createUser(userToInsert);
        User compareTest = uDao.getUserById("notausername");
     //   assertNotNull(compareTest);
        assertNotEquals(userToInsert, compareTest);
    }

    @Test
    public void clearPass() throws DataAccessException{
        uDao.createUser(userToInsert);
        uDao.createUser(userToInsert2);
        User compareTest = uDao.getUserById("dormond");
        uDao.clear();
        compareTest = uDao.getUserById("dormond");
        assertNull(compareTest);
    }

    @Test
    public void loginPass() throws DataAccessException {
        uDao.createUser(userToInsert);
        User compareTest = uDao.login(userToInsert.getUsername(),userToInsert.getPassword());
        assertEquals(userToInsert, compareTest);
    }

    @Test
    public void loginFail() throws DataAccessException {
        uDao.createUser(userToInsert);
        User compareTest = uDao.login("wrong","wrong");
        assertNull(compareTest);
    }



}
