package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.concurrent.ExecutionException;

public class EnvironmentLanActivity extends Activity {

    public static final String TAG = "EnvironmentLanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment_lan);

        SharedPreferences sharedPreferences = getSharedPreferences(Host.PREF_IP_ADDRESS, MODE_PRIVATE);
        final String currentIp = sharedPreferences.getString("currentIp", "Exception");

        Button startScanButton = (Button) findViewById(R.id.start_scan_button);

        final ListView hostsListAdapter = (ListView) findViewById(R.id.list_hosts);

        SQLiteOpenHelper sqLiteOpenHelper = new HostsDatabaseHelper(this);
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();

        Cursor cursor = db.query(HostsDatabaseHelper.HOSTS_TABLE,
                new String[]{ HostsDatabaseHelper.KEY_IP, HostsDatabaseHelper.KEY_MAC },
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            CursorAdapter listCursorAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    new String[] {HostsDatabaseHelper.KEY_IP, HostsDatabaseHelper.KEY_MAC},
                    new int[] {android.R.id.text1, android.R.id.text2},
                    0);
        }

        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                new IpScannerTask(getApplicationContext(), progressBar, hostsListAdapter).execute(currentIp);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
