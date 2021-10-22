package com.plovv.demopersonstable.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.plovv.demopersonstable.R;
import com.plovv.demopersonstable.presenters.PersonDetailsPresenter;

public class PersonDetailsActivity extends AppCompatActivity implements IPersonDetailsView {

    private PersonDetailsPresenter presenter;
    private IViewActionHandler actionHandler;
    private Resources resources;

    private LinearLayout progressBarContainer;

    private TextView TxtFirstName,
                     TxtLastName,
                     TxtAge,
                     TxtEmail,
                     TxtAddress,
                     TxtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        resources = getResources();

        initTextViews();

        progressBarContainer = findViewById(R.id.loading_progress_bar);

        presenter = new PersonDetailsPresenter(this);
    }

    @Override
    protected void onResume() {
        if (actionHandler != null) {
            actionHandler.onViewCreated();
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (actionHandler != null) {
            actionHandler.onViewDestroyed();
        }

        super.onDestroy();
    }

    @Override
    public void setActionHandler(IPersonDetailsView.IViewActionHandler handler) {
        this.actionHandler = handler;
    }

    @Override
    public int getPersonIdArg() {
        return getIntent().getIntExtra("personID", 0);
    }

    @Override
    public void setFirstName(String firstName) {
        TxtFirstName.setText(resources.getString(R.string.person_first_name, firstName));
    }

    @Override
    public void setLastName(String lastName) {
        TxtLastName.setText(resources.getString(R.string.person_last_name, lastName));
    }

    @Override
    public void setAge(String age) {
        TxtAge.setText(resources.getString(R.string.person_age, age));
    }

    @Override
    public void setEmail(String email) {
        TxtEmail.setText(resources.getString(R.string.person_email, email));
    }

    @Override
    public void setAddress(String address) {
        TxtAddress.setText(resources.getString(R.string.person_address, address));
    }

    @Override
    public void setPhoneNumber(String phone) {
        TxtPhoneNumber.setText(resources.getString(R.string.person_phone_number, phone));
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showListProgress() {
        if(progressBarContainer != null){
            progressBarContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideListProgress() {
        if(progressBarContainer != null){
            progressBarContainer.setVisibility(View.GONE);
        }
    }

    private void initTextViews() {
        TxtFirstName = findViewById(R.id.txt_firstname);
        TxtLastName = findViewById(R.id.txt_lastname);
        TxtAge = findViewById(R.id.txt_age);
        TxtEmail = findViewById(R.id.txt_email);
        TxtAddress = findViewById(R.id.txt_address);
        TxtPhoneNumber = findViewById(R.id.txt_phone);
    }

}