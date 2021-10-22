package com.plovv.demopersonstable.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.plovv.demopersonstable.R;
import com.plovv.demopersonstable.presenters.PersonsTablePresenter;
import com.plovv.demopersonstable.utils.PersonsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IPersonsTableView {

    private IViewActionHandler actionHandler;

    private RecyclerView personsRecyclerView;
    private LinearLayout listHeader;
    private ProgressBar listProgress;

    private PersonsAdapter personsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

        listProgress = findViewById(R.id.loading_progress_bar);

        personsRecyclerView = findViewById(R.id.recycler_view_persons);
        personsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        listHeader = findViewById(R.id.header_persons);

        new PersonsTablePresenter(this);
    }

    private void setupActionBar() {
        Toolbar appToolBar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolBar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Persons");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search by name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                actionHandler.onSearchPerson(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    actionHandler.onSearchPerson("");
                }

                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
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

    //#region "view overrides"
    @Override
    public void setActionHandler(IViewActionHandler handler) {
        actionHandler = handler;
    }

    @Override
    public void setPersonsList(ArrayList<ArrayList<String>> dataRows, ArrayList<String> columns, HashMap<String, Integer> columnWidths, int rowHeight) {
        // create header
        TextView txtView;
        LayoutInflater inflater = LayoutInflater.from(this);

        // remove any existing children before adding new
        if (listHeader.getChildCount() > 0) {
            listHeader.removeAllViews();
        }

        for (int i = 0; i < columns.size(); i++) {
            String title = columns.get(i);

            txtView = (TextView) inflater.inflate(R.layout.list_header_column, listHeader, false);
            txtView.setWidth(columnWidths.get(title));
            txtView.setText(title);

            txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actionHandler != null) {
                        actionHandler.onSort(title);
                    }
                }
            });

            listHeader.addView(txtView);
        }

        // init recycler
        personsAdapter = new PersonsAdapter(dataRows, columns, columnWidths, rowHeight, new PersonsAdapter.IOnItemClickHandler() {

            @Override
            public void onItemClick(ArrayList<String> personRowData) {
                if (actionHandler != null) {
                    actionHandler.onPersonSelect(personRowData);
                }
            }

        });

        personsRecyclerView.setAdapter(personsAdapter);
    }

    @Override
    public void updatePersonList(ArrayList<ArrayList<String>> dataRows) {
        if (personsAdapter != null) {
            personsAdapter.updateDataRows(dataRows);
            personsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void navigateToPersonDetails(int personIdArg) {
        Intent intent = new Intent(this, PersonDetailsActivity.class);
        intent.putExtra("personID", personIdArg);
        startActivity(intent);
    }

    @Override
    public void updateSortColumnTitle(String column, ORDERING_TYPE ordering) {
        for (int i = 0; i < listHeader.getChildCount(); i++) {
            TextView txtView = (TextView) listHeader.getChildAt(i);
            String title = txtView.getText().toString().replace("▲", "").replace("▼", "").trim();

            if (title.equalsIgnoreCase(column)) {
                String indicatorChar = "▼";

                if (ordering == ORDERING_TYPE.DESC) {
                    indicatorChar = "▲";
                }

                txtView.setText(indicatorChar + " " + title);
            } else {
                txtView.setText(title);
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showListProgress() {
        if(listProgress != null){
            listProgress.setVisibility(View.VISIBLE);
        }

        if(personsRecyclerView != null){
            personsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideListProgress() {
        if(personsRecyclerView != null){
            personsRecyclerView.setVisibility(View.VISIBLE);
        }

        if(listProgress != null){
            listProgress.setVisibility(View.GONE);
        }
    }
    //#endregion

}