package dao;
import model.User;

import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * handles the user table in the DB
 */
public class UserDao {
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe
    private final Connection conn;
    public UserDao(Connection conn) {
        this.conn = conn;
    }
    /**
     *Creates a user and adds to the database
     * @param userObj object with the info to add to database for that user
     */
    public void createUser(User userObj) throws DataAccessException {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        userObj.setPersonID(base64Encoder.encodeToString(randomBytes));
        String sql = "INSERT INTO Users (username, password, email, firstname, lastname, gender, personID) " +
                "VALUES(?,?,?,?,?,?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, userObj.getUsername());
            stmt.setString(2, userObj.getPassword());
            stmt.setString(3, userObj.getEmail());
            stmt.setString(4, userObj.getFirstName());
            stmt.setString(5, userObj.getLastName());
            stmt.setString(6, userObj.getGender());
            stmt.setString(7, userObj.getPersonID());

            stmt.executeUpdate();

        }
        catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error encountered inserting new user");
        }
    }
    /**
     * Logs user into their account
     * @param username chosen username
     * @param password chosen password
     * @return retuns an authToken
     */
    public User login(String username, String password){
        User user;
        ResultSet rs = null;
        String sql = "Select * FROM Users Where username = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if(rs.next()){
                String tempPW = rs.getString("password");
                if (tempPW.equals(password)){
                    user = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"),
                            rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                            rs.getString("personID"));
                    return user;
                }
                else{
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds a user from the database
     * @param username ID of user that is being looked for
     * @return user object that is found. Returns null if not found
     */
    public User getUserById(String username) throws DataAccessException {
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE username = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if(rs.next()){
                user = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("personID"));
                return user;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error encountered finding user");
        }
        finally {
            if(rs != null){
                try{
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * clears all data from the database
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Users";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error occurred clearing users");
        }
    }

}
