package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.TextView;

public class HostDetailActivity extends Activity {

    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_deatail);

        SQLiteOpenHelper hostsDatabaseHelper = new HostsDatabaseHelper(this);
        SQLiteDatabase db = hostsDatabaseHelper.getReadableDatabase();

        int hostId = (Integer) getIntent().getExtras().get(HostsDatabaseHelper.KEY_ID);

        cursor = db.query(HostsDatabaseHelper.HOSTS_TABLE,
                new String[]{
                        HostsDatabaseHelper.KEY_ID,
                        HostsDatabaseHelper.KEY_IP,
                        HostsDatabaseHelper.KEY_MAC,
                        HostsDatabaseHelper.KEY_HOSTNAME
                },
                "_id = ?", new String[]{Integer.toString(hostId)}, null, null, null);

        if (cursor.moveToFirst()) {

            int ipColumnIndex = cursor.getColumnIndex(HostsDatabaseHelper.KEY_IP);
            int macColumnIndex = cursor.getColumnIndex(HostsDatabaseHelper.KEY_MAC);
            int hostnameColumnIndex = cursor.getColumnIndex(HostsDatabaseHelper.KEY_HOSTNAME);

            TextView ipTextView = (TextView) findViewById(R.id.ip_det_textview);
            ipTextView.setText("IP: " + cursor.getString(ipColumnIndex));

            TextView macTextView = (TextView) findViewById(R.id.mac_det_textview);
            macTextView.setText("MAC: " + cursor.getString(macColumnIndex));

            TextView hostnameTextView = (TextView) findViewById(R.id.hostname_det_textview);
            hostnameTextView.setText("Hostname " + cursor.getString(hostnameColumnIndex));
        }
    }

    @Override
    protected void onDestroy() {
        cursor.close();
        super.onDestroy();
    }
}
