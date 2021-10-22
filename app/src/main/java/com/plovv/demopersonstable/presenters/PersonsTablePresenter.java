package com.plovv.demopersonstable.presenters;

import android.os.AsyncTask;

import com.plovv.demopersonstable.database.PersonDAO;
import com.plovv.demopersonstable.database.SQLiteDb;
import com.plovv.demopersonstable.models.PersonModel;
import com.plovv.demopersonstable.views.IPersonsTableView;

import java.util.ArrayList;
import java.util.HashMap;

public class PersonsTablePresenter {

    private IPersonsTableView view;

    private final ColumnOrdering columnOrdering;
    private String currentSearchTerm;

    public PersonsTablePresenter(IPersonsTableView view) {
        this.view = view;

        this.columnOrdering = new ColumnOrdering(SQLiteDb.COLUMN_PERSON_ID, SQLiteDb.ORDERING_TYPE.ASC);
        this.currentSearchTerm = null;

        view.setActionHandler(new IPersonsTableView.IViewActionHandler() {
            @Override
            public void onSearchPerson(String searchTerm) {
                if (searchTerm.trim().isEmpty()) {
                    currentSearchTerm = null;
                } else {
                    currentSearchTerm = searchTerm.trim();
                }

                updatePersonsGrid();
            }

            @Override
            public void onPersonSelect(ArrayList<String> personRowDat) {
                if (personRowDat != null) {
                    if (personRowDat.size() > 0) {
                        int personID = Integer.parseInt(personRowDat.get(0));

                        navigateToPersonDetails(personID);
                    }
                }
            }

            @Override
            public void onSort(String column) {
                if (columnOrdering.orderColumn.equalsIgnoreCase(column)) {
                    columnOrdering.orderingType = columnOrdering.orderingType == SQLiteDb.ORDERING_TYPE.ASC ? SQLiteDb.ORDERING_TYPE.DESC : SQLiteDb.ORDERING_TYPE.ASC;
                } else {
                    columnOrdering.orderColumn = column;
                    columnOrdering.orderingType = SQLiteDb.ORDERING_TYPE.ASC;
                }

                updatePersonsGrid();
            }

            @Override
            public void onViewCreated() {
                createPersonsGrid();
            }

            @Override
            public void onViewDestroyed() {
                detachView();
            }
        });
    }

    private void createPersonsGrid() {
        new FetchPersons(
                this,
                true,
                columnOrdering.orderColumn,
                columnOrdering.orderingType,
                null
        ).execute();
    }

    private void updatePersonsGrid() {
        new FetchPersons(
                this,
                false,
                columnOrdering.orderColumn,
                columnOrdering.orderingType,
                currentSearchTerm
        ).execute();
    }

    private ArrayList<PersonModel> getPersonsFromDB(String orderColumn, SQLiteDb.ORDERING_TYPE orderingType, String searchTerm) {
        PersonDAO personDAO = new PersonDAO();

        return personDAO.getAllPersons(orderColumn, orderingType, searchTerm);
    }

    private void initPersonsListView(ArrayList<PersonModel> persons) {
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

        ArrayList<String> columns = new ArrayList<String>(){{
            add(SQLiteDb.COLUMN_PERSON_ID);
            add(SQLiteDb.COLUMN_PERSON_FIRST_NAME);
            add(SQLiteDb.COLUMN_PERSON_LAST_NAME);
            add(SQLiteDb.COLUMN_PERSON_AGE);
            add(SQLiteDb.COLUMN_PERSON_EMAIL);
            add(SQLiteDb.COLUMN_PERSON_ADDRESS);
            add(SQLiteDb.COLUMN_PERSON_PHONE_NUMBER);
        }};

        HashMap<String, Integer> widths = new HashMap<String, Integer>() {{
           put(SQLiteDb.COLUMN_PERSON_ID, 140);
           put(SQLiteDb.COLUMN_PERSON_FIRST_NAME, 380);
           put(SQLiteDb.COLUMN_PERSON_LAST_NAME, 380);
           put(SQLiteDb.COLUMN_PERSON_AGE, 160);
           put(SQLiteDb.COLUMN_PERSON_EMAIL, 620);
           put(SQLiteDb.COLUMN_PERSON_ADDRESS, 620);
           put(SQLiteDb.COLUMN_PERSON_PHONE_NUMBER, 420);
        }};

        view.setPersonsList(data, columns, widths, 200);
    }

    private void updatePersonsListView(ArrayList<PersonModel> persons) {
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

        view.updateSortColumnTitle(columnOrdering.orderColumn, columnOrdering.orderingType == SQLiteDb.ORDERING_TYPE.ASC ? IPersonsTableView.ORDERING_TYPE.ASC : IPersonsTableView.ORDERING_TYPE.DESC);
        view.updatePersonList(data);
    }

    public void navigateToPersonDetails(int personID) {
        view.navigateToPersonDetails(personID);
    }

    public void detachView() {
        this.view = null;
    }

    public void showLoadProgress() {
        view.showListProgress();
    }

    public void hideLoadProgress() {
        view.hideListProgress();
    }

    public void showMessage(String message) {
        view.showMessage(message);
    }

    // async call to fetch persons from the db
    private class FetchPersons extends AsyncTask<Void, Void, ArrayList<PersonModel>> {

        private PersonsTablePresenter caller;
        private boolean operationFailed = false;

        private final String orderColumn;
        private final SQLiteDb.ORDERING_TYPE orderingType;
        private final String searchTerm;
        private final Boolean firstLoad;

        public FetchPersons(PersonsTablePresenter caller, Boolean firstLoad, String orderColumn, SQLiteDb.ORDERING_TYPE orderingType, String searchTerm) {
            this.caller = caller;

            this.orderColumn = orderColumn;
            this.orderingType = orderingType;
            this.searchTerm = searchTerm;
            this.firstLoad = firstLoad;
        }

        @Override
        protected void onPreExecute() {
            if (caller != null) {
                caller.showLoadProgress();
            }
        }

        @Override
        protected ArrayList<PersonModel> doInBackground(Void... voids) {
            try {
                return caller.getPersonsFromDB(orderColumn, orderingType, searchTerm);
            } catch (Exception e) {

                operationFailed = true;
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<PersonModel> results) {
            if (caller != null) {
                caller.hideLoadProgress();

                if (!operationFailed && results != null) {
                    if(firstLoad){
                        caller.initPersonsListView(results);
                    } else {
                        caller.updatePersonsListView(results);
                    }
                } else if (operationFailed) {
                    caller.showMessage("Error loading persons.");
                } else {
                    caller.showMessage("Persons not found.");
                }

                caller = null;
            }
        }

        @Override
        protected void onCancelled() {
            if (caller != null) {
                caller.hideLoadProgress();
                caller = null;
            }

            super.onCancelled();
        }
    }

    class ColumnOrdering {
        public String orderColumn;
        public SQLiteDb.ORDERING_TYPE orderingType;

        public ColumnOrdering(String orderColumn, SQLiteDb.ORDERING_TYPE orderingType) {
            this.orderColumn = orderColumn;
            this.orderingType = orderingType;
        }
    }

}
