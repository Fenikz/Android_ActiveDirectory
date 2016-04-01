package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.os.Bundle;

public class EnvironmentLanActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment_lan);

        // TODO: 01.04.2016 Вынести ScanLoaclIpsTask в отдельный класс
       // new MainActivity.ScanLocalIpsTask.execute();

    }
}
