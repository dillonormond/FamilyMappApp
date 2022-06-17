package dao;

import model.Event;
import model.Person;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * handles events table in the DB
 */
public class EventDao {
    private final Connection conn;

    public EventDao(Connection conn)
    {
        this.conn = conn;
    }

    /**
     * adds event to database
     * @param event event object to be added
     * @throws DataAccessException if there is a SQL error
     */
    public void insert(Event event) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * finds an event by eventID
     * @param eventID eventID to look for
     * @return the event that was found. Returns null if not found
     * @throws DataAccessException if there is an SQL error
     */
    public Event find(String eventID) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("AssociatedUsername"),
                        rs.getString("personID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"), rs.getString("eventID"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * find events connected to a user
     * @param username username to find events for
     * @return list of events found
     */
    public Event[] findUserEvents(String username) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE associatedUsername = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            Set<Event> eventSet = new HashSet<>();
            while(rs.next()){
                event = new Event(rs.getString("associatedUsername"), rs.getString("personID"), rs.getFloat("latitude"),
                        rs.getFloat("longitude"), rs.getString("country"), rs.getString("city"),
                        rs.getString("eventType"), rs.getInt("year"), rs.getString("eventID"));
                eventSet.add(event);
            }
            Event [] eventArr = new Event[eventSet.size()];
            eventArr = eventSet.toArray(eventArr);
            return eventArr;
        }
        catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error encountered finding event");
        }
    }


    public void deletePersonsEvents(String personID) throws DataAccessException {
        String sql = "DELETE FROM Events WHERE personID = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error deleting events from specific person");
        }
    }

    public void deleteUsersEvents(String username) throws DataAccessException {
        String sql = "DELETE FROM Events WHERE associatedUsername = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error deleting events from specific person");
        }
    }

    /**
     * clears all events from database
     */
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Events";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error occurred clearing Persons");
        }
    }
    public int getNumRows() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Events";
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

}
