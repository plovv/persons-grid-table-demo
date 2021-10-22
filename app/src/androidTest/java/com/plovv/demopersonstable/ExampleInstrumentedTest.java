package com.plovv.demopersonstable;

import android.content.Context;
import android.os.Debug;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.plovv.demopersonstable.database.PersonDAO;
import com.plovv.demopersonstable.database.SQLiteDb;
import com.plovv.demopersonstable.models.PersonModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private final static String[] FirstNames = {"Phanuhel", "Lavone", "Tony", "Martie", "Heli", "Martha", "Nick", "John", "Helen", "George", "Jim"};
    private final static String[] LastNames = {"Haywood", "Boatwright", "Arrington", "Kay", "Fairclough", "Smith", "Kingston", "Breckenridge", "Lyon", "Clifford", "Parish"};
    private final static String[] Addresses = {"Rhodes", "Paradise city", "Downtown", "Street", "Hollywood", "Athens"};

    private SQLiteDb database;
    private PersonDAO personDAO;

    @Before
    public void initDatabase() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        database = SQLiteDb.getInstance(appContext);
        personDAO = new PersonDAO();
    }

    @After
    public void closeDb() throws IOException {
        if (database != null) {
            database.closeDatabase();
        }
    }

    @Test
    public void insertTestData() throws Exception {
        Random random = new Random();
        ArrayList<PersonModel> persons = new ArrayList<>();

        for(int i = 0; i < 20; i++) {
            PersonModel person = new PersonModel();
            person.ID = i+1;
            person.FirstName = FirstNames[random.nextInt(FirstNames.length - 1)];
            person.LastName = LastNames[random.nextInt(LastNames.length - 1)];
            person.Age = random.nextInt(82) + 18;
            person.Email = person.FirstName.toLowerCase() + "." + person.LastName.toLowerCase() + "@email.com";
            person.Address = Addresses[random.nextInt(Addresses.length - 1)] + " " + (random.nextInt(99) + 1);
            person.Phone = "+" + (random.nextInt(50) + 10) + " " + "5555" + (random.nextInt(999999) + 100000);

            persons.add(person);
        }

        if (personDAO.insertPersons(persons) == -1) {
            Log.e("TEST", "Failed to insert persons into the database.");
        }
    }
}