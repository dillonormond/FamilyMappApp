package service;

import dao.DataAccessException;
import model.Person;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class dataGeneratorTest {

    @Test
    public void testGenerate() throws FileNotFoundException, DataAccessException {
        dataGenerator test = new dataGenerator();
        Person userPerson = new Person("dormond", "dillon", "ormond", "m");

        test.generatePersonCaller("f", 2, "dormond", userPerson);
    }
}
