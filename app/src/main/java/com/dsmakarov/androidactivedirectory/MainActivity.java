package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    String mCurrentIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Получаем текущий IP-адресс
        mCurrentIp = NetHelper.getIPAddress(true);
        // TODO: 31.03.2016 Запускать в отдельном потоке
        scanLoaclIps(mCurrentIp);

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
                resultTextView.setText(resultString);

                // Прячем клавиатуру
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pingButton.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    private HashMap<String, String> scanLoaclIps(String currentIp) {

        HashMap<String, String> localIpsHashMap = new HashMap<>();

        //Позиция последней точки в IP-адресе
        int lastDot = currentIp.lastIndexOf(".");

        //Предполагаемая маска (255.255.255.0)

        //Предполагаемое начало диапазона
        String subnetIp = currentIp.substring(0, lastDot + 1);

        for (int i = 0; i < 255; i++) {
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

        //Log.d(TAG, "scanLoaclIps: assume mask " + subnetIp);

            //С помщью маски подсети получаем кол-во адрессов для сканирования сети

        return localIpsHashMap;
    }

}
