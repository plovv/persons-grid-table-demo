package com.plovv.demopersonstable.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.plovv.demopersonstable.application.DemoPersonsTableApp;

import java.util.ArrayList;

public class SQLiteDb extends SQLiteOpenHelper {

    public enum ORDERING_TYPE {
        ASC, DESC
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Persons.db";

    // tables
    public static final String TABLE_PERSON = "Person";

    // columns
    public static final String COLUMN_PERSON_ID = "Id";
    public static final String COLUMN_PERSON_FIRST_NAME = "FirstName";
    public static final String COLUMN_PERSON_LAST_NAME = "LastName";
    public static final String COLUMN_PERSON_AGE = "Age";
    public static final String COLUMN_PERSON_EMAIL = "Email";
    public static final String COLUMN_PERSON_ADDRESS = "Address";
    public static final String COLUMN_PERSON_PHONE_NUMBER = "PhoneNumber";

    // table creation scripts
    private static final String[] CREATE_QUERIES = {
        "CREATE TABLE IF NOT EXISTS ["+TABLE_PERSON+"](["+COLUMN_PERSON_ID+"] INTEGER PRIMARY KEY, ["+COLUMN_PERSON_FIRST_NAME+"] TEXT COLLATE NOCASE, ["+COLUMN_PERSON_LAST_NAME+"] TEXT COLLATE NOCASE, ["+COLUMN_PERSON_AGE+"] INTEGER, ["+COLUMN_PERSON_EMAIL+"] TEXT COLLATE NOCASE, ["+COLUMN_PERSON_ADDRESS+"] TEXT COLLATE NOCASE, ["+COLUMN_PERSON_PHONE_NUMBER+"] TEXT COLLATE NOCASE);"
    };

    private static SQLiteDb instance;
    private SQLiteDatabase db;

    public static synchronized SQLiteDb getInstance() {
        if (instance == null) {
            instance = new SQLiteDb(DemoPersonsTableApp.getInstance().getApplicationContext());
        }

        return instance;
    }

    public static synchronized SQLiteDb getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteDb(context);
        }

        return instance;
    }

    private SQLiteDatabase getCurrentDB() {
        if (this.db == null) {
            this.db = getWritableDatabase();
        }

        return this.db;
    }

    private SQLiteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if (this.db == null) {
            db = getWritableDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String query : CREATE_QUERIES) {
            try {
                db.execSQL(query);
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLiteException("Failed to run initial create queries.\nException: " + e.getMessage());
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: implement upgrade logic on version change
    }

    public void closeDatabase() {
        if(db != null) {
            db.close();
        }

        this.close();
    }

    public Cursor querySelect(String query, @Nullable String[] args) {
        try {
            SQLiteDatabase db = getCurrentDB();

            return db.rawQuery(query, args);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public Cursor getData(String table, ArrayList<String> selectColumns, @Nullable String where, @Nullable String orderColumn, @Nullable ORDERING_TYPE orderingType) {
        try {
            SQLiteDatabase db = getCurrentDB();

            String[] columns;
            String order = null;

            if (selectColumns != null) {
                columns = new String[selectColumns.size()];
                columns = selectColumns.toArray(columns);
            } else {
                throw new NullPointerException();
            }

            if (orderColumn != null ) {
                order = orderColumn;

                if (orderingType == null) {
                    order += " asc";
                } else if (orderingType == ORDERING_TYPE.ASC) {
                    order += " asc";
                } else if (orderingType == ORDERING_TYPE.DESC){
                    order += " desc";
                }
            }

            return db.query(table, columns, where, null, null, null, order, null);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /***
     * Insert a single entry or multiple entries into a db table.
     * @param table
     * @param insertColumns
     * @param data
     * @return The new entry's row id, or the the last row id in case of multiple entries.
     */
    public long insert(String table, ArrayList<String> insertColumns, ArrayList<ArrayList<String>> data) {
        SQLiteDatabase db = getCurrentDB();

        int cols, rows;
        long rowID = -1;

        db.beginTransaction();

        try {
            ContentValues values;
            ArrayList<String> singleRow;

            cols = insertColumns.size();
            rows = data.size();

            for (int i = 0; i < rows; i++) {
                values = new ContentValues();
                singleRow = data.get(i);

                for (int j = 0; j < cols; j++) {
                    String rowVal = singleRow.get(j);

                    if (rowVal == null) {
                        values.putNull( insertColumns.get(j));
                    } else {
                        values.put(insertColumns.get(j), rowVal);
                    }
                }

                rowID = db.insert(table, null, values);

                if (rowID == -1) {
                    throw new Exception("Insert failed.");
                }
            }

            db.setTransactionSuccessful();

            return rowID;
        } catch (Exception e) {
            e.printStackTrace();

            return -1;
        } finally {
            db.endTransaction();
        }
    }

}
