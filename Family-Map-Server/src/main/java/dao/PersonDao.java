package dao;
import model.*;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * handles the person table in the DB
 */
public class PersonDao {

    private final Connection conn;
    public PersonDao(Connection conn){this.conn = conn;}
    /**
     * Takes a person Obj and adds it to the database
     * @param personObj person to be added
     */
    public void createPerson(Person personObj) throws DataAccessException {
        String sql = "INSERT INTO Persons (personID, associatedUsername, firstName, lastName, gender, fatherID, motherID, spouseID) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, personObj.getPersonID());
            stmt.setString(2, personObj.getAssociatedUsername());
            stmt.setString(3, personObj.getFirstName());
            stmt.setString(4, personObj.getLastName());
            stmt.setString(5, personObj.getGender());
            stmt.setString(6, personObj.getFatherID());
            stmt.setString(7, personObj.getMotherID());
            stmt.setString(8, personObj.getSpouseID());


            stmt.executeUpdate();

        }
        catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error encountered inserting new person");
        }
    }
    public Person findPerson(String personID) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE personID = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if(rs.next()){
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"), rs.getString("firstName"),
                        rs.getString("lastName"), rs.getString("gender"), rs.getString("fatherID"), rs.getString("motherID"),
                        rs.getString("spouseID"));
                return person;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error encountered finding person");
        }
        return null;
    }

    public void deleteUsersPersons(String username) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE associatedUsername = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error deleting events from specific person");
        }
    }

    /**
     * clears all data from the database
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Persons";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error occurred clearing Persons");
        }
    }

    public int getNumRows() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Persons";
        ResultSet rs;
        int rows = -1;
        try(Statement stmt = conn.createStatement()){
            rs = stmt.executeQuery(sql);
            if(rs.next()){
                rows = rs.getInt("count(*)");
            }
        }
        return rows;
    }

    public Person[] getAllPersonsForUser(String username) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE associatedUsername = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            Set<Person> personSet = new HashSet<>();
            while(rs.next()){
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"), rs.getString("firstName"),
                        rs.getString("lastName"), rs.getString("gender"), rs.getString("fatherID"), rs.getString("motherID"),
                        rs.getString("spouseID"));
                personSet.add(person);
            }
            Person [] personArr = new Person[personSet.size()];
            personArr = personSet.toArray(personArr);
            return personArr;
        }
        catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error encountered finding person");
        }
    }

}
