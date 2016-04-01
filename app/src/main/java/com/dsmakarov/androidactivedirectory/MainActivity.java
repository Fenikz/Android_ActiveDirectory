package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    // TODO: 01.04.2016 Записать текущий ip в SharedPerferences

    String mCurrentIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Получаем текущий IP-адресс
        mCurrentIp = NetHelper.getIPAddress(true);

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
                //Log.d(TAG, "onCreate: pingTarget" + pingTarget);

                String resultString = NetHelper.ping(targetIpEditText.getText().toString());
                //String resultString = NetHelper.getHostName(targetIpEditText.getText().toString());
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

    public class ScanLocalIpsTask extends AsyncTask<String, Integer, String> {

        //Предполагаемая маска (255.255.255.0)

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> localIpsHashMap = new HashMap<>();

            Log.d(TAG, "doInBackground: Начало выполнения " + params[0]);
            
            //Позиция последней точки в IP-адресе
            int lastDot = params[0].lastIndexOf(".");
            String subnetIp = params[0].substring(0, lastDot + 1);

            for (int i = 0; i < 255; i++) {
                Log.d(TAG, "doInBackground: Цикл " + i);
                // TODO: 31.03.2016 Добавить ограничение на свой IP
                if (NetHelper.ping(subnetIp + i).contains("1 received")) {
                    localIpsHashMap.put(subnetIp + i, "Enabled");
                } else {
                    localIpsHashMap.put(subnetIp + i, "Disabled");
                }
            }

            for (HashMap.Entry<String, String> element : localIpsHashMap.entrySet()) {
                Log.d(TAG, "scanLoaclIps: IP " + element.getKey() + " is " + element.getValue() );
            }

            return "OK";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // TODO: 31.03.2016 Добавить в процентах (255 = 100%)
        }
    }
}
