package com.example.nevermore.webscraper;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Collections;

public class SavedMemberActivity extends AppCompatActivity {

    private static final String TAG = "SavedMemberActivity";
    private static final int ACTIVITY_NUM = 3;
    private Context mContext = SavedMemberActivity.this;
    
    private DatabaseHelper memberDb;

    private String clanName, dateCreated, tableName;
    private TextView tvClanName, tvDateCreated;
    private ImageButton backArrow;
    private ListView mMemberList;

    private ArrayList<Member> membersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_member);
        memberDb = new DatabaseHelper(this);

        tvClanName = findViewById(R.id.clcr_name);
        tvDateCreated = findViewById(R.id.date_created);
        backArrow = findViewById(R.id.back_button);
        mMemberList = findViewById(R.id.memberList);

        getIncomingIntent();
        tvClanName.setText(clanName);
        tvDateCreated.setText(dateCreated);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setupBottomNavigationView();
        Log.d(TAG, "onCreate: table name: " + tableName);

        membersArrayList = new ArrayList<>();

        Cursor result = memberDb.getAllData(tableName);
        if (result.getCount() == 0) {
            //show message
            membersArrayList.add(new Member("No ","member", 0));
            Log.d(TAG, "onCreate: No members found");
            return;
        } else {
            while (result.moveToNext()) {
                membersArrayList.add(new Member(result.getString(0),result.getString(1),result.getInt(2)));
            }
        }
        Collections.sort(membersArrayList);
        MemberListAdapter adapter = new MemberListAdapter(mContext, R.layout.member_list_item, membersArrayList);
        mMemberList.setAdapter(adapter);
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incomming intent");
        if (getIntent().hasExtra("db_table_name") && getIntent().hasExtra("clan_name") && getIntent().hasExtra("date_created")){
            Log.d(TAG, "getIncomingIntent: There are extras");
            tableName = getIntent().getStringExtra("db_table_name");
            clanName = getIntent().getStringExtra("clan_name");
            dateCreated = getIntent().getStringExtra("date_created");
        }
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(SavedMemberActivity.this,this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
