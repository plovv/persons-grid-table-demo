package com.plovv.demopersonstable.presenters;

import android.os.AsyncTask;

import com.plovv.demopersonstable.database.PersonDAO;
import com.plovv.demopersonstable.models.PersonModel;
import com.plovv.demopersonstable.views.IPersonDetailsView;


public class PersonDetailsPresenter {

    private IPersonDetailsView view;
    private final int PersonID;

    public PersonDetailsPresenter(IPersonDetailsView view) {
        this.view = view;
        this.PersonID = this.view.getPersonIdArg();

        this.view.setActionHandler(new IPersonDetailsView.IViewActionHandler() {
            @Override
            public void onViewCreated() {
                (new FetchPerson(PersonDetailsPresenter.this, PersonID)).execute();
            }

            @Override
            public void onViewDestroyed() {
                detachView();
            }
        });
    }

    private PersonModel getPersonFromDB(int PersonID) {
        PersonDAO personDAO = new PersonDAO();

        return personDAO.getPerson(PersonID);
    }

    private void loadPersonView(PersonModel person) {
        view.setFirstName(person.FirstName);
        view.setLastName(person.LastName);
        view.setAge(person.Age.toString());
        view.setEmail(person.Email);
        view.setAddress(person.Address);
        view.setPhoneNumber(person.Phone);
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

    // async call to fetch single person from db
    private class FetchPerson extends AsyncTask<Void, Void, PersonModel> {

        private PersonDetailsPresenter caller;
        private int PersonID;
        private boolean operationFailed = false;

        public FetchPerson(PersonDetailsPresenter caller, int personID) {
            this.caller = caller;
            this.PersonID = personID;
        }

        @Override
        protected void onPreExecute() {
            if (caller != null) {
                caller.showLoadProgress();
            }
        }

        @Override
        protected PersonModel doInBackground(Void... voids) {
            try {
                return caller.getPersonFromDB(PersonID);
            } catch (Exception e) {

                operationFailed = true;
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(PersonModel results) {
            if (caller != null) {
                caller.hideLoadProgress();

                if (!operationFailed && results != null) {
                    caller.loadPersonView(results);
                } else if (operationFailed) {
                    caller.showMessage("Error loading the person.");
                } else {
                    caller.showMessage("Person was not found.");
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

}
