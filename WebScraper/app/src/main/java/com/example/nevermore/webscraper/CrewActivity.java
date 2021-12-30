package com.example.nevermore.webscraper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
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

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CrewActivity extends Activity{

    SwipeRefreshLayout swipeRefreshLayout;
    private Switch autoRefresh;

    private static final String TAG = "CrewActivity";
    private static final int ACTIVITY_NUM = 1;
    private InterstitialAd mInterstitialAd;

    final Handler handler = new Handler();
    final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            new doit(getApplicationContext()).execute();
            handler.postDelayed(this,  2000);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crew);

        //ad
        MobileAds.initialize(this, getString(R.string.interstitial_ad_app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("C8365180A72F56D0C52685BFF4B65A66").build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView time = (TextView) findViewById(R.id.time);
        autoRefresh = (Switch) findViewById(R.id.autoRefresh);
        final TextView timeFd = (TextView) findViewById(R.id.timeFd);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        setupBottomNavigationView();
        new doit(getApplicationContext()).execute();

        final Handler timeHandler = new Handler();
        Runnable timeRefresh = new Runnable() {
            @Override
            public void run() {
                time.setText(getTime());
                timeHandler.postDelayed(this,  1000);
            }
        };

        Thread timeThread = new Thread(timeRefresh);
        timeThread.start();

        getFdCountdown(timeFd);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        if (isOnline()){
            boolean firstStart = prefs.getBoolean("firstStart", true);
            if (firstStart){
                firstTimeDialog();
            }
            autoRefresh.setChecked(false);
            if (!autoRefresh.isChecked()){
                Log.d(TAG, "onCheckedChanged: running swip to refresh");

                swipeRefreshLayout.setColorSchemeColors(Color.MAGENTA, Color.LTGRAY);

                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        new doit(getApplicationContext()).execute();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, 3000);
                    }
                });
            }

            autoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    showAd();
                    Log.d(TAG, "onCheckedChanged: switch changed to: " + isChecked);
                    if (isChecked == true) {
                        // The toggle is enabled
                        Log.d(TAG, "onCheckedChanged: running auto refresh");
                        swipeRefreshLayout.setEnabled(false);
                        refresh.run();
                        Toast.makeText(CrewActivity.this, "Auto Refresh ENABLED", Toast.LENGTH_SHORT).show();
                    } else if (isChecked == false){
                        // The toggle is disabled
                        Log.d(TAG, "onCheckedChanged: running swip to refresh");
                        handler.removeCallbacks(refresh);
                        swipeRefreshLayout.setEnabled(true);
                        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
                        swipeRefreshLayout.setColorSchemeColors(Color.MAGENTA, Color.LTGRAY);

                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {

                                new doit(getApplicationContext()).execute();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }, 3000);
                            }
                        });
                    }
                }
            });
        }else {
            noInternetDialog(CrewActivity.this).show();
        }

    }



    private class doit extends AsyncTask<Void,Void,Void> {

        String url = "https://www.ninjasaga.com/game-info/all_crew.php";
        ListView mListView = (ListView) findViewById(R.id.listView);
        int i;
        int j = 2;
        String date;
        String[] listName = new String[25];
        String[] listCm = new String[25];
        String[] listNo = new String[25];
        String[] listDamage = new String[25];

        Document doc;

        private WeakReference<Context> contextRef;

        public doit(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                doc = Jsoup.connect(url).get();

                Elements times = doc.select("table tr table td table td.e_font-text01");
                for (Element time: times){
                    date = time.select("tr:nth-child(28) td:nth-child(1)").text();
                }

                Elements ranks = doc.select("table tr table td table td.e_font-text01");
                for (Element rank: ranks){
                    for(i=0; i<25; i++){
                        do {
                            j++;
                            listName[i] = rank.select("tr:nth-child(" + j + ") td:nth-child(2) a").text();
                            listCm[i] = rank.select("tr:nth-child(" + j + ") td:nth-child(3)").text();
                            listNo[i] = rank.select("tr:nth-child(" + j + ") td:nth-child(4)").text();
                            listDamage[i] = rank.select("tr:nth-child(" + j + ") td:nth-child(5)").text();
                        }while (listName[i]== null && listCm[i]== null && listNo[i]== null && listDamage[i] == null);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
                Context context = contextRef.get();

                Crew crews[] = new Crew[25];

                int gap[] = new int[25];
                String listGap[] = new String[25];

                for (i=0;i<24;i++){
                    if (listDamage[i] != null && listDamage[i+1] != null){
                        gap[i] = Integer.parseInt(listDamage[i]) - Integer.parseInt(listDamage[i+1]);
                        listGap[i] = Integer.toString(gap[i]);
                    }
                }

                Crew info = new Crew("Name /", "Crew Master" , "Member No" , "Damage /", "Gap");
                Crew clan1 = new Crew(listName[0], listCm[0], listNo[0], listDamage[0], "Champ");
                for (int i = 1; i<25;i++){
                    crews[i] = new Crew(listName[i], listCm[i], listNo[i], listDamage[i], listGap[i-1]);
                }
                final ArrayList<Crew> crewList = new ArrayList<>();

//                Iterator itr = clanList.iterator();
//                while(itr.hasNext()){
//                    Crew crew = (Crew)itr.next();
//                    Log.d(TAG, "onPostExecute: Name: " + crew.getName() + "\n Reps: " +crew.getReps());
//                }
                crewList.add(info);
                crewList.add(clan1);
                for (i=1; i<25; i++){
                    crewList.add(crews[i]);
                }

                final CrewListAdapter adapter = new CrewListAdapter(context, R.layout.adapter_view_layout, crewList);

                if (mListView.getAdapter() == null){
                    Log.d(TAG, "onPostExecute: setting the adapter in crewActivity");

                    mListView.setAdapter(adapter);

                }else {
                    Log.d(TAG, "onPostExecute: there is adapter in crewActivity");
                    ((CrewListAdapter)mListView.getAdapter()).refill(crewList);

                }


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(refresh);
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoRefresh.setChecked(false);
        handler.removeCallbacks(refresh);
    }

    private String getTime(){

        String timeNow;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.US);

        sdfDate.setTimeZone(TimeZone.getTimeZone("America/Managua")); //google 'android list of timezones'
        sdfTime.setTimeZone(TimeZone.getTimeZone("America/Managua"));

        Date today = c.getTime();
        String date = sdfDate.format(today);
        String time = sdfTime.format(today);

        timeNow = date + " " + time;

        return timeNow;
    }

    private void getFdCountdown(final TextView timeFd){

        //get FD date from firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fdDateString = dataSnapshot.child("crew_date").getValue().toString();
                Log.d(TAG, "onDataChange: date: " + fdDateString);

                timeFd.setText(fdDateString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void firstTimeDialog(){
        CustomDialog customDialog = new CustomDialog(CrewActivity.this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setCancelable(true);
        customDialog.show();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public AlertDialog.Builder noInternetDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You are not connected to the internet");
        builder.setCancelable(false);

        builder.setPositiveButton("Reload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recreate();
            }
        });

        builder.setNegativeButton("View saved", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(CrewActivity.this, SavedListActivity.class);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                startActivity(intent);
            }
        });
        return builder;
        }

    @Override
    public void recreate() {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            super.recreate();
        } else {
            startActivity(getIntent());
            finish();
        }
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(CrewActivity.this,this, bottomNavigationView);
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
