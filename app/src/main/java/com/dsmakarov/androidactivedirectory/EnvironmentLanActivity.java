package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EnvironmentLanActivity extends Activity {

    public static final String TAG = "EnvironmentLanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment_lan);

        SharedPreferences sharedPreferences = getSharedPreferences(Host.PREF_IP_ADDRESS, MODE_PRIVATE);
        final String currentIp = sharedPreferences.getString("currentIp", "Exception");

        //TODO: 01.04.2016 Вынести ScanLoaclIpsTask в отдельный класс
        //new MainActivity.ScanLocalIpsTask.execute();

        final ArrayList<HashMap<String, String>> myArrList = new ArrayList<>();

        Button startScanButton = (Button) findViewById(R.id.start_scan_button);

        final ListView hostsListAdapter = (ListView) findViewById(R.id.list_hosts);

        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                    myArrList.addAll(new IpScannerTask(progressBar).execute(currentIp).get());

                    final SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
                            myArrList,
                            android.R.layout.simple_list_item_2,
                            new String[] {"ip", "status"},
                            new int[] {android.R.id.text1, android.R.id.text2});

                    hostsListAdapter.setAdapter(adapter);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
