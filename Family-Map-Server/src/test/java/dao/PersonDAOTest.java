package dao;

import model.Event;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDAOTest {
    private Database db;
    private Person personToInsert;
    private Person personToInsert2;
    private PersonDao pDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new Database();
        // and a new event with random data
        personToInsert = new Person("thisisanID", "dormond", "dillon",
                "ormond", "m", "dillondad", "dillonmom", "dillonspouse");
        personToInsert2 = new Person("jackID", "dormond", "jack",
                "smith", "m", "jackdad", "jackmom", "jackspouse");

        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Then we pass that connection to the EventDAO, so it can access the database.
        pDao = new PersonDao(conn);
        //Let's clear the database as well so any lingering data doesn't affect our tests
        pDao.clear();
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
        pDao.createPerson(personToInsert);
        Person compareTest = pDao.findPerson(personToInsert.getPersonID());
        assertNotNull(compareTest);
        assertEquals(personToInsert, compareTest);
    }
    @Test
    public void findPass() throws DataAccessException {
        pDao.createPerson(personToInsert);
        Person compareTest = pDao.findPerson(personToInsert.getPersonID());
        assertNotNull(compareTest);
        assertEquals(personToInsert, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException{
        pDao.createPerson(personToInsert);
//        User compareTest = uDao.getUserById(userToInsert.getUsername());
//        assertNotNull(compareTest);
//        assertEquals(userToInsert, compareTest);
        assertThrows(DataAccessException.class, () -> pDao.createPerson(personToInsert));
    }
    @Test
    public void findFail() throws DataAccessException{
        Person compareTest = pDao.findPerson("notausername");
        //   assertNotNull(compareTest);
        assertNull(compareTest);
    }

    @Test
    public void clearPass() throws DataAccessException{
        pDao.createPerson(personToInsert);
        pDao.createPerson(personToInsert2);
        Person compareTest = pDao.findPerson("jackID");
        pDao.clear();
        compareTest = pDao.findPerson("jackID");
        assertNull(compareTest);
    }

    @Test
    public void deleteUsersPersonsPass() throws DataAccessException {
        pDao.createPerson(personToInsert);
        pDao.deleteUsersPersons("dormond");

        Person compareTest = pDao.findPerson("thisisanID");
        assertNull(compareTest);
    }

    @Test
    public void deleteUsersPersonFail() throws DataAccessException {
        pDao.createPerson(personToInsert);
        pDao.deleteUsersPersons("incorrectID");

        Person compareTest = pDao.findPerson("thisisanID");
        assertNotNull(compareTest);
    }

    @Test
    public void testFindRows() throws DataAccessException, SQLException {
        pDao.createPerson(personToInsert);
        pDao.createPerson(personToInsert2);
        assertEquals(2, pDao.getNumRows());
    }

    @Test
    public void getAllPersonsForUserPass() throws DataAccessException {
        pDao.createPerson(personToInsert2);
        pDao.createPerson(personToInsert);
        Set<Person> personSet = new HashSet<>();
        personSet.add(personToInsert);
        personSet.add(personToInsert2);

        Person[] resultArray = pDao.getAllPersonsForUser("dormond");
        assertEquals(personSet.size(), resultArray.length);
    }

    @Test
    public void getAllPersonsForUserFail() throws DataAccessException {
        pDao.createPerson(personToInsert2);
        pDao.createPerson(personToInsert);
        Set<Person> personSet = new HashSet<>();
        personSet.add(personToInsert);
        personSet.add(personToInsert2);

        Person[] resultArray = pDao.getAllPersonsForUser("wrong");
        assertEquals(0, resultArray.length);
    }


}
