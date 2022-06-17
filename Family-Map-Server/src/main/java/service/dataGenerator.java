package service;

import com.google.gson.Gson;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import dao.PersonDao;
import model.*;
import model.json.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.Random;

public class dataGenerator {
    private LocationData locData;
    private maleNamesData maleNames;
    private femaleNamesData femaleNames;
    private lastNamesData lastNames;
    public dataGenerator() throws FileNotFoundException {
        Gson gson = new Gson();
        Reader locReader = new FileReader("json/locations.json");
        locData = gson.fromJson(locReader, LocationData.class);
        Reader reader = new FileReader("json/mnames.json");
        maleNames = gson.fromJson(reader, maleNamesData.class);
        reader = new FileReader("json/fnames.json");
        femaleNames = gson.fromJson(reader, femaleNamesData.class);
        reader = new FileReader("json/snames.json");
        lastNames = gson.fromJson(reader, lastNamesData.class);

    }

    public void generatePersonCaller(String gender, int generations, String username, Person userPerson) throws FileNotFoundException, DataAccessException {
        Person person = generatePerson(gender, generations, username);
        Database db = new Database();
        Connection conn = db.getConnection();
        EventDao eDao = new EventDao(conn);
        eDao.deletePersonsEvents(person.getPersonID());

        userPerson.setFatherID(person.getFatherID());
        userPerson.setMotherID(person.getMotherID());
        PersonDao pDao = new PersonDao(conn);
        pDao.createPerson(userPerson);

        int rndLoc = new Random().nextInt(locData.getData().length);
        Location rndLocationBirth = locData.getData()[rndLoc];


        Event birth = new Event(username, userPerson.getPersonID(), rndLocationBirth.getLatitude(), rndLocationBirth.getLongitude(),
                rndLocationBirth.getCountry(), rndLocationBirth.getCity(), "Birth", 1800 + (30 * generations));

        eDao.insert(birth);

        db.closeConnection(true);
    }
    Person generatePerson(String gender, int generations, String username) throws FileNotFoundException, DataAccessException {
        Person mother = null;
        Person father = null;
        if(generations >= 1){
            mother = generatePerson("f", generations - 1, username);
            father = generatePerson("m", generations - 1, username);
            father.setSpouseID(mother.getPersonID());
            mother.setSpouseID(father.getPersonID());
            Database db = new Database();
            Connection conn = db.getConnection();
            PersonDao motherDao = new PersonDao(conn);
            motherDao.createPerson(mother);
            PersonDao fatherDao = new PersonDao(conn);
            fatherDao.createPerson(father);
            EventDao marriageDao = new EventDao(conn);
            int rand = new Random().nextInt(locData.getData().length);
            Location marriageLoc = locData.getData()[rand];
            Event motherMarriage = new Event(username, mother.getPersonID(), marriageLoc.getLatitude(), marriageLoc.getLongitude(),
                    marriageLoc.getCountry(), marriageLoc.getCity(), "Marriage", 1800 + ((30 * generations)));
            Event fatherMarriage = new Event(username, father.getPersonID(), marriageLoc.getLatitude(), marriageLoc.getLongitude(),
                    marriageLoc.getCountry(), marriageLoc.getCity(), "Marriage", 1800 + ((30 * generations)));
            marriageDao.insert(motherMarriage);
            marriageDao.insert(fatherMarriage);
            db.closeConnection(true);
        }
        int rndLoc = new Random().nextInt(locData.getData().length);
        int rndLoc2 = new Random().nextInt(locData.getData().length);


        int rndLast = new Random().nextInt(lastNames.getData().length);
        Location rndLocationBirth = locData.getData()[rndLoc];
        Location rndLocationDeath = locData.getData()[rndLoc2];
        String lastName = lastNames.getData()[rndLast];


        int rndFirst;
        String firstName = null;
        if(gender.equals("f")){
            rndFirst = new Random().nextInt(femaleNames.getData().length);
            firstName = femaleNames.getData()[rndFirst];
        }
        if(gender.equals("m")){
            rndFirst = new Random().nextInt(maleNames.getData().length);
            firstName = maleNames.getData()[rndFirst];
        }
        Person person = new Person(username, firstName, lastName, gender);
        if(mother != null){
            person.setMotherID(mother.getPersonID());
        }
        if(father != null){
            person.setFatherID(father.getPersonID());
        }
        Event birth = new Event(username, person.getPersonID(), rndLocationBirth.getLatitude(), rndLocationBirth.getLongitude(),
                rndLocationBirth.getCountry(), rndLocationBirth.getCity(), "Birth", 1800 + (30 * generations));
        Event death = new Event(username, person.getPersonID(), rndLocationDeath.getLatitude(), rndLocationDeath.getLongitude(),
                rndLocationDeath.getCountry(), rndLocationDeath.getCity(), "Death", 1880 + (30 * generations));

        Database db = new Database();
        Connection conn = db.getConnection();
        EventDao eDao = new EventDao(conn);
        eDao.insert(birth);
        eDao.insert(death);
        db.closeConnection(true);

        return person;
    }
}
