package com.example.nevermore.webscraper;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Collections;

public class SavedListActivity extends AppCompatActivity {
    private static final String TAG = "SavedListActivity";
    private static final int ACTIVITY_NUM = 3;
    private Context mContext;
    private DatabaseHelper tableDb;

    private RecyclerView allTableList;
    private DatabaseTableAdapter tableAdapter;
    private TextView errorText;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    ArrayList<DataTable> allTables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_list);
        mContext = SavedListActivity.this;
        tableDb = new DatabaseHelper(this);

        allTableList = (RecyclerView) findViewById(R.id.allTableList);
        errorText = (TextView) findViewById(R.id.errorText);
        toolbar = findViewById(R.id.savedListToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        allTables = new ArrayList<>();

        allTableList.setHasFixedSize(true);
        allTableList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false));

        showAllTables();

        tableAdapter = new DatabaseTableAdapter(this,allTables,tableDb,coordinatorLayout);
        allTableList.setAdapter(tableAdapter);

        setupBottomNavigationView();

    }

    private void showAllTables(){


        Cursor res = tableDb.getAllLogData();
        if (res.getCount() == 0){
            //show message
            errorText.setVisibility(View.VISIBLE);
            allTableList.setVisibility(View.GONE);
            return;
        }else{
            errorText.setVisibility(View.GONE);
            allTableList.setVisibility(View.VISIBLE);

            while (res.moveToNext()){
                allTables.add(new DataTable(res.getString(0),res.getString(1),res.getInt(2),res.getString(3),res.getString(4)));
                Log.d(TAG, "showAllTables: all table: " + allTables.toString());
                Collections.reverse(allTables);
                Log.d(TAG, "showAllTables: table after reverse: " + allTables.toString());
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: options menu created");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem  = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tableAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    /**
     *
     * * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(SavedListActivity.this,SavedListActivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
