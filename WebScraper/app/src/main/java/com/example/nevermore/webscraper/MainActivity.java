package com.example.nevermore.webscraper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nevermore.webscraper.BottomNavigationViewHelper;
import com.example.nevermore.webscraper.Clan;
import com.example.nevermore.webscraper.ClanListAdapter;
import com.example.nevermore.webscraper.CustomDialog;
import com.example.nevermore.webscraper.R;
import com.example.nevermore.webscraper.UpdateHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements UpdateHelper.OnUpdateCheckListener{

    @Override
    public void onUpdateCheckListener(final String urlApp) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("please update to new version to continue use")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent updateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlApp));
                        startActivity(updateIntent);
                    }
                }).setCancelable(false).create();

        alertDialog.show();

    }

    SwipeRefreshLayout swipeRefreshLayout;
    private Switch autoRefresh;


    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 0;
    private InterstitialAd mInterstitialAd;

    final Handler handler = new Handler();
    final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            new doit(getApplicationContext()).execute();
            handler.postDelayed(this,2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();

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



    }


    private class doit extends AsyncTask<Void,Void,Void> {

        String url = "https://www.ninjasaga.com/game-info/all_clan.php";
        ListView mListView = (ListView) findViewById(R.id.listView);
        int i;
        int j = 2;
        String[] listName = new String[25];
        String[] listCm = new String[25];
        String[] listNo = new String[25];
        String[] listReps = new String[25];

        Document doc;

        private WeakReference<Context> contextRef;

        public doit(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                doc = Jsoup.connect(url).get();

                Elements ranks = doc.select("table tr table td table td.e_font-text01");
                for (Element rank: ranks){
                    for(i=0; i<25; i++){
                        do {
                            j++;
                            listName[i] = rank.select("tr:nth-child(" + j + ") td:nth-child(2) a").text();
                            listCm[i] = rank.select("tr:nth-child(" + j + ") td:nth-child(3)").text();
                            listNo[i] = rank.select("tr:nth-child(" + j + ") td:nth-child(4)").text();
                            listReps[i] = rank.select("tr:nth-child(" + j + ") td:nth-child(5)").text();
                        }while (listName[i]== null && listCm[i]== null && listNo[i]== null && listReps[i] == null);

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

                Clan clans[] = new Clan[25];

                int gap[] = new int[25];
                String listGap[] = new String[25];

                for (i=0;i<24;i++){
                    if (listReps[i] != null && listReps[i+1] != null){
                        gap[i] = Integer.parseInt(listReps[i]) - Integer.parseInt(listReps[i+1]);
                        listGap[i] = Integer.toString(gap[i]);
                    }
                }

                Clan info = new Clan("Name /", "Clan Master" , "Member No" , "Reputation /", "Gap");
                Clan clan1 = new Clan(listName[0], listCm[0], listNo[0], listReps[0], "Champ");
                for (int i = 1; i<25;i++){
                    clans[i] = new Clan(listName[i], listCm[i], listNo[i], listReps[i], listGap[i-1]);
                }
                final ArrayList<Clan> clanList = new ArrayList<>();
                clanList.add(info);
                clanList.add(clan1);
                for (i=1; i<25; i++){
                    clanList.add(clans[i]);
                }

                final ClanListAdapter adapter = new ClanListAdapter(context, R.layout.adapter_view_layout, clanList);

                if (mListView.getAdapter() == null){
                    Log.d(TAG, "onPostExecute: setting the adapter");

                    mListView.setAdapter(adapter);

                }else {
                    Log.d(TAG, "onPostExecute: there is adapter");
                    ((ClanListAdapter)mListView.getAdapter()).refill(clanList);
                }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        if (isOnline()){
            boolean firstStart = prefs.getBoolean("firstStart", true);
            if (firstStart){
                firstTimeDialog();
            }
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
            }else{
                Log.d(TAG, "onCheckedChanged: running auto refresh");
                swipeRefreshLayout.setEnabled(false);
                final Handler handler = new Handler();
                Runnable refresh = new Runnable() {
                    @Override
                    public void run() {
                        new doit(getApplicationContext()).execute();
                        handler.postDelayed(this,  2000);
                    }

                };
                Thread mythread = new Thread(refresh);
                mythread.start();
                Toast.makeText(MainActivity.this, "Auto Refresh ENABLED", Toast.LENGTH_SHORT).show();
//                autoRefresh.setVisibility(View.GONE);
            }

            autoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, "onCheckedChanged: switch changed to: " + isChecked);
                    showAd();
                    if (isChecked == true) {
                        // The toggle is enabled
                        Log.d(TAG, "onCheckedChanged: running auto refresh");
                        swipeRefreshLayout.setEnabled(false);
                        refresh.run();

                        Toast.makeText(MainActivity.this, "Auto Refresh ENABLED", Toast.LENGTH_SHORT).show();
                    } else if (isChecked == false){
                        // The toggle is disabled
                        Log.d(TAG, "onCheckedChanged: running swipe to refresh");
                        swipeRefreshLayout.setEnabled(true);
                        handler.removeCallbacks(refresh);
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
            noInternetDialog(MainActivity.this).show();
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
                String fdDateString = dataSnapshot.child("date").getValue().toString();
                Log.d(TAG, "onDataChange: date: " + fdDateString);

                timeFd.setText(fdDateString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void firstTimeDialog(){
        CustomDialog customDialog = new CustomDialog(MainActivity.this);
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
                Intent intent = new Intent(MainActivity.this, SavedListActivity.class);
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

    /**
     *
     * * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(MainActivity.this,this, bottomNavigationView);
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
