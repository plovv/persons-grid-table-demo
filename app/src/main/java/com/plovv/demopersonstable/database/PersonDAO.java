package com.plovv.demopersonstable.database;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.plovv.demopersonstable.models.PersonModel;

import java.util.ArrayList;

public class PersonDAO {

    private SQLiteDb database;

    public PersonDAO() {
        database = SQLiteDb.getInstance();
    }

    public ArrayList<PersonModel> getAllPersons(String orderColumn, SQLiteDb.ORDERING_TYPE orderingType, @Nullable String searchTerm) {
        ArrayList<String> columns = new ArrayList<String>(){{
            add(SQLiteDb.COLUMN_PERSON_ID);
            add(SQLiteDb.COLUMN_PERSON_FIRST_NAME);
            add(SQLiteDb.COLUMN_PERSON_LAST_NAME);
            add(SQLiteDb.COLUMN_PERSON_AGE);
            add(SQLiteDb.COLUMN_PERSON_EMAIL);
            add(SQLiteDb.COLUMN_PERSON_ADDRESS);
            add(SQLiteDb.COLUMN_PERSON_PHONE_NUMBER);
        }};

        Cursor dbResults = null;
        ArrayList<PersonModel> results = new ArrayList<>();

        try{
            String whereCondition = null;

            if (searchTerm != null) {
                if(!searchTerm.trim().isEmpty()) {
                    whereCondition = SQLiteDb.COLUMN_PERSON_FIRST_NAME + " like '%" + searchTerm.trim().replace("'", "''") + "%'" +
                            " or " + SQLiteDb.COLUMN_PERSON_LAST_NAME + " like '%" + searchTerm.trim().replace("'", "''") + "%'";
                }
            }

            dbResults = database.getData(SQLiteDb.TABLE_PERSON, columns, whereCondition, orderColumn, orderingType);

            if (dbResults != null) {
                if(dbResults.moveToFirst()){
                    do{
                        PersonModel person = new PersonModel();

                        person.ID = dbResults.getInt(0);
                        person.FirstName = dbResults.getString(1);
                        person.LastName = dbResults.getString(2);
                        person.Age = dbResults.getInt(3);
                        person.Email = dbResults.getString(4);
                        person.Address = dbResults.getString(5);
                        person.Phone = dbResults.getString(6);

                        results.add(person);
                    }while (dbResults.moveToNext());
                }
            }

            return results;
        }catch (Exception ex){
            ex.printStackTrace();

            return null;
        }finally {
            if(dbResults != null){
                dbResults.close();
            }
        }
    }

    public PersonModel getPerson(int personID) {
        String query = "select "
                            + SQLiteDb.COLUMN_PERSON_ID + ","
                            + SQLiteDb.COLUMN_PERSON_FIRST_NAME + ","
                            + SQLiteDb.COLUMN_PERSON_LAST_NAME + ","
                            + SQLiteDb.COLUMN_PERSON_AGE + ","
                            + SQLiteDb.COLUMN_PERSON_EMAIL + ","
                            + SQLiteDb.COLUMN_PERSON_ADDRESS + ","
                            + SQLiteDb.COLUMN_PERSON_PHONE_NUMBER + " " +
                        "from "
                            + SQLiteDb.TABLE_PERSON + " " +
                        "where "
                            + SQLiteDb.COLUMN_PERSON_ID + " = ?";

        Cursor dbResults = null;
        PersonModel person = null;

        try{
            dbResults = database.querySelect(query, new String[]{String.valueOf(personID)});

            if (dbResults != null) {
                if(dbResults.moveToFirst()){
                    person = new PersonModel();

                    person.ID = dbResults.getInt(0);
                    person.FirstName = dbResults.getString(1);
                    person.LastName = dbResults.getString(2);
                    person.Age = dbResults.getInt(3);
                    person.Email = dbResults.getString(4);
                    person.Address = dbResults.getString(5);
                    person.Phone = dbResults.getString(6);
                }
            }

            return person;
        }catch (Exception ex){
            ex.printStackTrace();

            return null;
        }finally {
            if(dbResults != null){
                dbResults.close();
            }
        }
    }

    public long insertPerson(PersonModel person) {
        ArrayList<String> columns = new ArrayList<String>(){{
            add(SQLiteDb.COLUMN_PERSON_ID);
            add(SQLiteDb.COLUMN_PERSON_FIRST_NAME);
            add(SQLiteDb.COLUMN_PERSON_LAST_NAME);
            add(SQLiteDb.COLUMN_PERSON_AGE);
            add(SQLiteDb.COLUMN_PERSON_EMAIL);
            add(SQLiteDb.COLUMN_PERSON_ADDRESS);
            add(SQLiteDb.COLUMN_PERSON_PHONE_NUMBER);
        }};

        ArrayList<ArrayList<String>> data = new ArrayList<>();
        data.add(new ArrayList<String>(){{
            add(person.ID.toString());
            add(person.FirstName);
            add(person.LastName);
            add(person.Age.toString());
            add(person.Email);
            add(person.Address);
            add(person.Phone);
        }});

        return database.insert(SQLiteDb.TABLE_PERSON, columns, data);
    }

    public long insertPersons(ArrayList<PersonModel> persons) {
        ArrayList<String> columns = new ArrayList<String>(){{
            add(SQLiteDb.COLUMN_PERSON_ID);
            add(SQLiteDb.COLUMN_PERSON_FIRST_NAME);
            add(SQLiteDb.COLUMN_PERSON_LAST_NAME);
            add(SQLiteDb.COLUMN_PERSON_AGE);
            add(SQLiteDb.COLUMN_PERSON_EMAIL);
            add(SQLiteDb.COLUMN_PERSON_ADDRESS);
            add(SQLiteDb.COLUMN_PERSON_PHONE_NUMBER);
        }};

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        for (PersonModel person : persons) {
            data.add(new ArrayList<String>(){{
                add(person.ID.toString());
                add(person.FirstName);
                add(person.LastName);
                add(person.Age.toString());
                add(person.Email);
                add(person.Address);
                add(person.Phone);
            }});
        }

        return database.insert(SQLiteDb.TABLE_PERSON, columns, data);
    }

}
