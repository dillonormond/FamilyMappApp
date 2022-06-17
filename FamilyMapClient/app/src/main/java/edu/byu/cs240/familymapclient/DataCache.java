package edu.byu.cs240.familymapclient;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ReqRes.SinglePersonResult;
import model.Event;
import model.Person;

public class DataCache {
    private static DataCache instance;

    private Map<String, Person> personsMap = new HashMap<>();
    private Map<String, Event> eventsMap = new HashMap<>();

    private Map<String, Event> filteredEvents = new HashMap<>();

    private Map<String, List<Event>> personEvents = new HashMap<>();
    private List<String> markerEvents = new ArrayList<>();

    private String currUsername;
    private String userPersonID;
    private String authToken;

    List<Polyline> lines = new ArrayList<>();

    public List<Polyline> getLines() {
        return lines;
    }

    public void setLines(List<Polyline> lines) {
        this.lines = lines;
    }

    private int generations = 0;

    private boolean[] lineFilters = {false, false, false};
    private boolean[] eventFilters = {true, true, true, true};


    private Set<String> paternalAncestors = new HashSet<>();
    private Set<String> maternalAncestors = new HashSet<>();
    private Set<String> maleAncestors = new HashSet<>();
    private Set<String> femaleAncestors = new HashSet<>();

    private Set<String> workingPersonList = new HashSet<>();


    GoogleMap storedMap;


    public static DataCache getInstance(){
        if(instance == null){
            instance = new DataCache();
        }
        return instance;
    }

    private DataCache(){

    }
    public void destructor(){
        personsMap = new HashMap<>();
        eventsMap = new HashMap<>();

        filteredEvents = new HashMap<>();

        personEvents = new HashMap<>();
        markerEvents = new ArrayList<>();
        lineFilters[0] = false;
        lineFilters[1] = false;
        lineFilters[2] = false;
        eventFilters[0] = true;
        eventFilters[1] = true;
        eventFilters[2] = true;
        eventFilters[3] = true;


        paternalAncestors = new HashSet<>();
        maternalAncestors = new HashSet<>();
        maleAncestors = new HashSet<>();
        femaleAncestors = new HashSet<>();
        workingPersonList = new HashSet<>();
        String currUsername = null;
        String userPersonID = null;
        String authToken = null;
    }

    public void setEventsByPerson(){
            for(Map.Entry<String, Person> entry : personsMap.entrySet()){
                List<Event> tempList = new ArrayList<>();
                String id = entry.getValue().getPersonID();
                personEvents.put(id, tempList);
                for(Map.Entry<String, Event> eventEntry : eventsMap.entrySet()){
                    if(eventEntry.getValue().getPersonID().equals(id)){
                        personEvents.get(id).add(eventEntry.getValue());
                    }
                }
            }

    }

    public void setFilters(String userID, String fatherID, String motherID, String host, String port){
        workingPersonList = new HashSet<>();
        Person userPerson = personsMap.get(userID);
        if(userPerson.getSpouseID() != null){
            workingPersonList.add(userPerson.getSpouseID());
            paternalAncestors.add(userPerson.getSpouseID());
            maternalAncestors.add(userPerson.getSpouseID());
        }
        workingPersonList.add(userID);
        paternalAncestors.add(userID);
        maternalAncestors.add(userID);
        setPaternalAncestors(fatherID, host, port);
        setMaternalAncestors(motherID, host, port);
        setPersonGenders();
        if(eventFilters[0]){
            for(String s : paternalAncestors){
                workingPersonList.add(s);
            }
        }
        if(eventFilters[1]){
            for(String s : maternalAncestors){
                workingPersonList.add(s);
            }
        }
    }
    private void setPaternalAncestors(String id, String host, String port){
        paternalAncestors.add(id);

        ServerProxy server = new ServerProxy(host, port);
        SinglePersonResult result = server.getPerson(id);
        String fatherID = result.getFatherID();
        String motherID = result.getMotherID();
        if(fatherID != null){
            setPaternalAncestors(fatherID, host, port);
        }
        if(motherID != null) {
            setPaternalAncestors(motherID, host, port);
        }
        generations++;
    }

    private void setPersonGenders(){
        for(Map.Entry<String, Person> entry : personsMap.entrySet()){
            if(entry.getValue().getGender().equals("m")){
                maleAncestors.add(entry.getValue().getPersonID());
            }
            if(entry.getValue().getGender().equals("f")){
                femaleAncestors.add(entry.getValue().getPersonID());
            }
        }

    }

    private void setMaternalAncestors(String id, String host, String port){
        maternalAncestors.add(id);
        ServerProxy server = new ServerProxy(host, port);
        SinglePersonResult result = server.getPerson(id);
        String fatherID = result.getFatherID();
        String motherID = result.getMotherID();
        if(fatherID != null){
            setMaternalAncestors(fatherID, host, port);
        }
        if(motherID != null) {
            setMaternalAncestors(motherID, host, port);
        }

    }

    public void filterEvents(){
        filteredEvents = new HashMap<>();
        Set<String> usedEvents = new HashSet<>();
        if(eventFilters[0]){
            for(Map.Entry<String, Event> entry : eventsMap.entrySet()){
                String test = entry.getValue().getPersonID();
                if(paternalAncestors.contains(entry.getValue().getPersonID())){
                    filteredEvents.put(entry.getKey(), entry.getValue());
                    usedEvents.add(entry.getKey());
                }
            }
        }
        if(eventFilters[1]){
            for(Map.Entry<String, Event> entry : eventsMap.entrySet()){
                if(maternalAncestors.contains(entry.getValue().getPersonID())){
                    filteredEvents.put(entry.getKey(), entry.getValue());
                    usedEvents.add(entry.getKey());
                }
            }
        }
        Map<String, Event> copyEvents = new HashMap<>();
        for(Map.Entry<String, Event> entry : filteredEvents.entrySet()){
            copyEvents.put(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<String, Event> entry : copyEvents.entrySet()){
            Person person = personsMap.get(entry.getValue().getPersonID());
            if(eventFilters[2] && person.getGender().equals("m") && !usedEvents.contains(entry.getKey())){
                filteredEvents.put(entry.getKey(), entry.getValue());
                usedEvents.add(entry.getKey());
            }
            else if(!eventFilters[2] && person.getGender().equals("m") && usedEvents.contains(entry.getKey())){
                filteredEvents.remove(entry.getKey());
                usedEvents.remove(entry.getKey());
            }
            if(eventFilters[3] && person.getGender().equals("f") && !usedEvents.contains(entry.getKey())){
                filteredEvents.put(entry.getKey(), entry.getValue());
                usedEvents.add(entry.getKey());
            }
            else if(!eventFilters[3] && person.getGender().equals("f") && usedEvents.contains(entry.getKey())){
                filteredEvents.remove(entry.getKey());
                usedEvents.remove(entry.getKey());
            }
        }

    }


    public static void setInstance(DataCache instance) {
        DataCache.instance = instance;
    }

    public Map<String, Person> getPersonsMap() {
        return personsMap;
    }

    public void addToPersonsMap(String ID, Person person){
        personsMap.put(ID, person);
    }

    public void addEventToMap(String eventID, Event event){
        eventsMap.put(eventID, event);
    }

    public void setPersonsMap(Map<String, Person> personsMap) {
        this.personsMap = personsMap;
    }

    public Map<String, Event> getEventsMap() {
        return eventsMap;
    }

    public void setEventsMap(Map<String, Event> eventsMap) {
        this.eventsMap = eventsMap;
    }

    public Map<String, List<Event>> getPersonEvents() {
        return personEvents;
    }

    public void setPersonEvents(Map<String, List<Event>> personEvents) {
        this.personEvents = personEvents;
    }

    public String getCurrUsername() {
        return currUsername;
    }

    public void setCurrUsername(String currUsername) {
        this.currUsername = currUsername;
    }

    public String getUserPersonID() {
        return userPersonID;
    }

    public void setUserPersonID(String userPersonID) {
        this.userPersonID = userPersonID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Set<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public void setPaternalAncestors(Set<String> paternalAncestors) {
        this.paternalAncestors = paternalAncestors;
    }

    public Set<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public void setMaternalAncestors(Set<String> maternalAncestors) {
        this.maternalAncestors = maternalAncestors;
    }


    public void setSpouseFilter(boolean decision){
        lineFilters[1] = decision;
    }
    public void setLifeStoryFilter(boolean decision){
        lineFilters[0] = decision;
    }
    public void setFamilyTreeFilter(boolean decision){
        lineFilters[2] = decision;
    }


    public boolean getSpouseFilter(){
        return lineFilters[1];
    }
    public boolean getLifeStoryFilter(){
        return lineFilters[0];
    }
    public boolean getFamilyTreeFilter(){
        return lineFilters[2];
    }


    public void setFatherFilter(boolean decision){eventFilters[0] = decision;}
    public void setMotherFilter(boolean decision){eventFilters[1] = decision;}
    public boolean getFatherFilter(){return eventFilters[0];}
    public boolean getMotherFilter(){return eventFilters[1];}

    public boolean getMaleFilter(){return eventFilters[2];}
    public boolean getFemaleFilter(){return eventFilters[3];}
    public void setMaleFilter(boolean decision){eventFilters[2] = decision;}
    public void setFemaleFilter(boolean decision){eventFilters[3] = decision;}

    public Map<String, Event> getFilteredEvents(){return filteredEvents;}



    public GoogleMap getStoredMap() {
        return storedMap;
    }

    public void setStoredMap(GoogleMap storedMap) {
        this.storedMap = storedMap;
    }

    public List<String> getCurrentMarkers() {
        return markerEvents;
    }

    public void setCurrentMarkers(List<String> events) {
        this.markerEvents = events;
    }
    public void addToMarkerEvents(String eventID){
        markerEvents.add(eventID);
    }
}
