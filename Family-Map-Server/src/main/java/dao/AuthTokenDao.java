package dao;
import model.AuthToken;
import java.sql.*;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

/**
 * handles authToken table in DB
 */
public class AuthTokenDao {
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe
    private final Connection conn;

    public AuthTokenDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * checks if authToken is correct
     * @param token token to compare to the one in database
     * @return return true or false depending on success
     */
    public String getUserFromToken(String token) throws DataAccessException {
        ResultSet rs = null;
        String foundUser;
        String sql = "SELECT * FROM AuthTokens WHERE authtoken = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if(rs.next()){
                foundUser = rs.getString("username");
                return foundUser;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException();
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
    public String getTokenFromUser(String username) throws DataAccessException {
        ResultSet rs = null;
        String foundToken;
        String sql = "SELECT * FROM AuthTokens WHERE username = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if(rs.next()){
                foundToken = rs.getString("authtoken");
                return foundToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException();
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
     * generates authToken
     * @param username username to generate token for
     */


    public void generateAuthToken(String username) throws DataAccessException {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String sql = "INSERT INTO AuthTokens (username, authtoken) VALUES (?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2, base64Encoder.encodeToString(randomBytes));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("error generating authtoken");
        }
    }

    /**
     * clears all authTokens from database
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM AuthTokens";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error occurred clearing users");
        }
    }
}
