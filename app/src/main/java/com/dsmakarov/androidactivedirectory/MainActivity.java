package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    private String mCurrentIp;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Получаем текущий IP-адресс
        mCurrentIp = NetHelper.getIPAddress(true);

        mSharedPreferences = getSharedPreferences(Host.PREF_IP_ADDRESS, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("currentIp", mCurrentIp);
        editor.apply();

        //mSharedPreferences.getString("currentIp", "Exception");

        // TODO: 31.03.2016 Запускать в отдельном потоке
        //new ScanLocalIpsTask().execute(mCurrentIp);

        TextView ipTextView = (TextView) findViewById(R.id.ip_textview);
        ipTextView.setText(mCurrentIp);

        final EditText targetIpEditText = (EditText) findViewById(R.id.ping_edittext);
        final TextView resultTextView = (TextView) findViewById(R.id.result_textview);
        final Button pingButton = (Button) findViewById(R.id.start_ping_button);

        pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resultString = NetHelper.ping(targetIpEditText.getText().toString());
                resultTextView.setText(resultString);

                // Прячем клавиатуру
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pingButton.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_scan_network:
                Intent intent = new Intent(this, EnvironmentLanActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:
                return true;

            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }
}
