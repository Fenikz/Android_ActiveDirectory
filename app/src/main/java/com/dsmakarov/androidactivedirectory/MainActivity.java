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

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    String mCurrentIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentIp = NetHelper.getIPAddress(true);

        TextView ipTextView = (TextView) findViewById(R.id.ip_textview);
        ipTextView.setText(mCurrentIp);

        final EditText targetIpEditText = (EditText) findViewById(R.id.ping_edittext);

        final TextView resultTextView = (TextView) findViewById(R.id.result_textview);

        final Button pingButton = (Button) findViewById(R.id.start_ping_button);
        pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onCreate: pingTarget" + pingTarget);
                resultTextView.setText(NetHelper.ping(targetIpEditText.getText().toString()));

                // прячем клавиатуру. butCalculate - это кнопка
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pingButton.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        //Log.d(TAG, "onCreate: ping " + pingResult);
        Log.d(TAG, "onCreate: getIpAddress " + mCurrentIp);


    }


}
