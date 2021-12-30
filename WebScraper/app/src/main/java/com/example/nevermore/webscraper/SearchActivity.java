package com.example.nevermore.webscraper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 2;
    private InterstitialAd mInterstitialAd;

    //sqlite database
    DatabaseHelper clanDb;

    private Spinner selector;
    private EditText inputId;
    private Button goBtn;
    private ImageButton insertBtn;
    private String preUrl;
    private TextView clcrname, progressText;
    private ListView mListView;
    private ProgressBar progressBar;
    private ArrayList<Suggestion> suggestionArray;

    private RelativeLayout nameLayout;
    private CoordinatorLayout coordinatorLayout;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private String[] name = new String[1];
    private String[] level = new String[1];
    private String[] points =  new String[1];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MobileAds.initialize(this, getString(R.string.interstitial_ad_app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        Log.d(TAG, "onCreate: ad unit: " + getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("C8365180A72F56D0C52685BFF4B65A66").build());

        clanDb = new DatabaseHelper(this);

        inputId = (EditText) findViewById(R.id.input_clan_id);
        goBtn = (Button) findViewById(R.id.btnSearch);
        selector = (Spinner) findViewById(R.id.selector);
        clcrname = (TextView) findViewById(R.id.name);
        mListView = (ListView) findViewById(R.id.memberList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.progressText);
        insertBtn = (ImageButton) findViewById(R.id.insertBtn);
        nameLayout = (RelativeLayout) findViewById(R.id.relLayout3);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        suggestionArray = new ArrayList<>();
        setupBottomNavigationView();

        //setting up clan/crew chooser
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.platform, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selector.setAdapter(adapter);
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                inputId.setVisibility(View.VISIBLE);
                if ((adapterView.getItemAtPosition(i).toString()).equals("Clan")){
                    preUrl = "https://www.ninjasaga.com/game-info/clan.php?page=0&clan_id=";
                    final DatabaseReference databaseReference = firebaseDatabase.getReference("Clan");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                suggestionArray.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Suggestion suggestion = new Suggestion();
                                    suggestion.setName(ds.getValue(Suggestion.class).getName());
                                    Log.d(TAG, "onDataChange: name" + suggestion.getName());
                                    suggestion.setId(ds.getValue(Suggestion.class).getId());
                                    suggestionArray.add(suggestion);
                                }
                                SuggestionAdapter suggestionAdapter = new SuggestionAdapter(SearchActivity.this, R.layout.layout_suggestion, suggestionArray);
                                mListView.setAdapter(suggestionAdapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Suggestion obj = (Suggestion) adapterView.getAdapter().getItem(i);
                            String id = obj.getId();
                            inputId.setText(id);
                        }
                    });

                }else if ((adapterView.getItemAtPosition(i).toString()).equals("Crew")){
                    preUrl = "https://www.ninjasaga.com/game-info/crew.php?page=0&crew_id=";
                    DatabaseReference databaseReference = firebaseDatabase.getReference("Crew");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                suggestionArray.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Suggestion suggestion = new Suggestion();
                                    suggestion.setName(ds.getValue(Suggestion.class).getName());
                                    Log.d(TAG, "onDataChange: name" + suggestion.getName());
                                    suggestion.setId(ds.getValue(Suggestion.class).getId());
                                    suggestionArray.add(suggestion);
                                }
                                SuggestionAdapter suggestionAdapter = new SuggestionAdapter(SearchActivity.this, R.layout.layout_suggestion, suggestionArray);
                                mListView.setAdapter(suggestionAdapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: URL: " + preUrl);
                if (!inputId.getText().toString().equals("")){
                    hideKeyboard();
                    nameLayout.setVisibility(View.GONE);
                    insertBtn.setVisibility(View.GONE);
                    new searchMembers(getApplicationContext()).execute(preUrl);
                    goBtn.setEnabled(false);
                    showAd();

                }else{
                    nameLayout.setVisibility(View.VISIBLE);
                    nameLayout.setBackgroundResource(R.color.grey);
                    clcrname.setText("ID field shouldn't be empty");
                    insertBtn.setVisibility(View.GONE);
                }

            }
        });

    }

    private class searchMembers extends AsyncTask<String,Integer,Void>{
        private String id;

        String url = "", lastPageNo, aChar = "", clanName, preUrl;

        int pageNoLast, totalMembers = 0;
        int[] numberPoints = new int[1];
        int string = 0, lastTr = 0;
        int membersTr, membersTrDeduct = 0;

        Document doc;

        private WeakReference<Context> contextRef;

        public searchMembers(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            nameLayout.setBackgroundResource(R.color.colorPrimary);
            progressBar.setVisibility(View.VISIBLE);
            progressText.setText("");
            progressText.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: URL in asynctask bg: " + strings[0]);
            name[0] = "";
            level[0] = "";
            points[0] = "";

            try {
                id = inputId.getText().toString();
                preUrl = strings[0];
                url = preUrl + id;
                doc = Jsoup.connect(url).get();
                //get last page no
                Elements links = doc.select("table tr table td table td.e_font-text01 table tr:nth-child(1)");
                for (Element link : links) {
                    String pageNo;
                    pageNo = link.select("td:nth-child(2) div#page_no_1 a").last().attr("href");
                    Log.d(TAG, "doInBackground: " + pageNo);
                    lastPageNo = pageNo;
                    aChar = Character.toString(lastPageNo.charAt(14));
                    clanName = link.select("td:nth-child(1)").text();
                }

                if (aChar.equals("-")){
                    Log.d(TAG, "doInBackground: " + id +" doesn't exist" );
                    clanName = "";
                    aChar = "";
                    name = new String[1];
                    name[0] = "";
                    level = new String[1];
                    level[0] = "";
                    points = new String[1];
                    points[0] = "";
                    numberPoints = new int[1];
                    numberPoints[0] = 0;
                }else {
                    pageNoLast = Integer.parseInt(aChar);
                    Log.d(TAG, "doInBackground: last page: " + aChar);
                    //get total members
                    if (preUrl.equals("https://www.ninjasaga.com/game-info/clan.php?page=0&clan_id=")){
                        url = "https://www.ninjasaga.com/game-info/clan.php?page=" + pageNoLast + "&clan_id=" + id;
                    }else if (preUrl.equals("https://www.ninjasaga.com/game-info/crew.php?page=0&crew_id=")){
                        url = "https://www.ninjasaga.com/game-info/crew.php?page=" + pageNoLast + "&crew_id=" + id;
                    }

                    doc = Jsoup.connect(url).get();
                    Log.d(TAG, "doInBackground: we are in last page");
                    Elements allElements = doc.select("table tr table td table td.e_font-text01");
                    for (Element table : allElements) {
                        Elements trs = table.getElementsByTag("tr");
                        lastTr = trs.size();
                    }
                    membersTr = lastTr - 3;
                    totalMembers = (pageNoLast * 25) + membersTr;
                    Log.d(TAG, "doInBackground: total members: " + totalMembers);
                    name = new String[totalMembers];
                    level = new String[totalMembers];
                    points = new String[totalMembers];
                    numberPoints = new int[totalMembers];

                    //getting members from a single page
                    for (int i = 0; i <= pageNoLast; i++) {
                        if (i == pageNoLast) {
                            membersTrDeduct = 25 - membersTr;
                        }
                        //getting value from single page
                        for (int j = 0; j < 25 - membersTrDeduct; j++) {
                            if (preUrl.equals("https://www.ninjasaga.com/game-info/clan.php?page=0&clan_id=")){
                                url = "https://www.ninjasaga.com/game-info/clan.php?page=" + i + "&clan_id=" + id;
                            }else if (preUrl.equals("https://www.ninjasaga.com/game-info/crew.php?page=0&crew_id=")){
                                url = "https://www.ninjasaga.com/game-info/crew.php?page=" + i + "&crew_id=" + id;
                            }

                            Document doc1 = Jsoup.connect(url).get();
                            Elements members = doc1.select("table tr table td table td.e_font-text01");
                            for (Element member : members) {
                                name[string] = member.select("table tr:nth-child(" + (j + 3) + ") td:nth-child(1)").text();
                                level[string] = member.select("table tr:nth-child(" + (j + 3) + ") td:nth-child(2)").text();
                                points[string] = member.select("table tr:nth-child(" + (j + 3) + ") td:nth-child(3)").text();
                            }
                            numberPoints[string] = Integer.parseInt(points[string]);
                            string++;
                            publishProgress(string);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "onProgressUpdate: progress: " + values[0] + "/" + totalMembers);

            progressText.setText(values[0] + "/" + totalMembers + " Done");
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Log.d(TAG, "onPostExecute: we are in on post execute");
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            nameLayout.setVisibility(View.VISIBLE);
            Context context = contextRef.get();
            if (name[0].equals("") && aChar.equals("") && points[0].equals("") && level[0].equals("")){
                clcrname.setText("Not Found");
                insertBtn.setVisibility(View.GONE);
            }else{
                final ArrayList<Member> members = new ArrayList<Member>();
                clcrname.setText(clanName);
                for (int i=0;i<totalMembers;i++){
                    members.add(new Member(name[i], level[i], numberPoints[i]));
                }
                Collections.sort(members);
                MemberListAdapter adapter = new MemberListAdapter(context, R.layout.member_list_item, members);
                mListView.setAdapter(adapter);
                insertBtn.setVisibility(View.VISIBLE);
                insertBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    boolean isInserted;
                    StringBuilder tableName = new StringBuilder();
                    String tableClcr;
                    if (preUrl.equals("https://www.ninjasaga.com/game-info/clan.php?page=0&clan_id=")){
                        tableClcr = "Clan";
                        tableName.append("clan_data_");
                    }else {
                        tableClcr = "Crew";
                        tableName.append("crew_data_");
                    }

                    tableName.append(timeStamp());
                    clanDb.createTable(tableName.toString());
                    Log.d(TAG, "onClick: table name: " + tableName);
                    for (int i=0;i<totalMembers;i++){
                         isInserted = clanDb.insertData(name[i],level[i],numberPoints[i],tableName.toString());
                        if (isInserted && (i+1)==totalMembers){
                            showSnackbar();
                            Log.d(TAG, "onClick: data inserted");
                        }else if (!isInserted){
                            Log.d(TAG, "onClick: error inserting data");
                        }
                    }
                    clanDb.insertInLog(tableName.toString(),currentTime(),totalMembers,tableClcr,clanName);

                    }
                });

            }
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            goBtn.setEnabled(true);

        }

    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Data inserted", Snackbar.LENGTH_INDEFINITE)
                .setAction("View", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SearchActivity.this,SavedListActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
        snackbar.show();
    }

    private static String currentTime() {
        String currentTime = new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date());
        Log.d(TAG, "getCurrentTimeUsingDate: current time: " + currentTime);
        return currentTime;
    }

    private static String timeStamp() {
        String timeStamp = new SimpleDateFormat("yy_MM_dd_HH_mm_ss").format(new Date());
        Log.d(TAG, "getCurrentTimeUsingDate: time stamp: " + timeStamp);
        return timeStamp;
    }

    public void showMessage(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void hideKeyboard() {

        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    /**
     *
     * * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(SearchActivity.this,this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void showAd(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }
}
