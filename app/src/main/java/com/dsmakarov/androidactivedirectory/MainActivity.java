package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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
                //Log.d(TAG, "onCreate: pingTarget" + pingTarget);

                String resultString = NetHelper.ping(targetIpEditText.getText().toString());

                //String resultString = NetHelper.multiPing(new String[]{"ya.ru", mCurrentIp});

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
}
