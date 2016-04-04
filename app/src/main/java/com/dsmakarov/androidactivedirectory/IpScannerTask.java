package com.dsmakarov.androidactivedirectory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс для сканирования сети
 */
public class IpScannerTask extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {
    //Предполагаемая маска (255.255.255.0)

    private static final String TAG = "IpScannerTask";
    private ProgressBar mProgressBar;
    private ListView mListView;
    private Context mContext;
    private int mSubnetCount = 256;

    private SQLiteOpenHelper hostsDatabaseHelper;
    private SQLiteDatabase db;

    public IpScannerTask(Context context, ProgressBar progressBar, ListView listView) {
        mProgressBar = progressBar;
        mListView = listView;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        hostsDatabaseHelper = new HostsDatabaseHelper(mContext);
        db = hostsDatabaseHelper.getWritableDatabase();
    }


    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

        // TODO: 03.04.2016 Изменить возвращаемый тип
        ArrayList<HashMap<String, String>> resultArrayList = new ArrayList<>();

        Log.d(TAG, "doInBackground: Начало выполнения " + params[0]);

        //Позиция последней точки в IP-адресе
        int lastDot = params[0].lastIndexOf(".");
        String subnetIp = params[0].substring(0, lastDot + 1);

        ContentValues hostCv = new ContentValues();
        db.delete(HostsDatabaseHelper.HOSTS_TABLE, null, null);

        //i < 255
        for (int i = 0; i < mSubnetCount; i++) {

            Log.d(TAG, "doInBackground: Цикл " + i);
            // TODO: 31.03.2016 Добавить ограничение на свой IP

            try {
                if (InetAddress.getByName(subnetIp + i).isReachable(1000)) {
                    Log.d(TAG, "doInBackground: online " + subnetIp + i);
                    hostCv.put(HostsDatabaseHelper.KEY_IP, subnetIp + i);
                    hostCv.put(HostsDatabaseHelper.KEY_MAC, NetHelper.getMacFromArpCache(subnetIp + i));
                    // TODO: 03.04.2016 Проверить правильность заполнения hostname
                    hostCv.put(HostsDatabaseHelper.KEY_HOSTNAME, InetAddress.getByName(subnetIp + i).getCanonicalHostName());
                    db.insert(HostsDatabaseHelper.HOSTS_TABLE, null, hostCv);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            publishProgress(i);
        }

        if (hostCv.size() > 0) {
            Log.d(TAG, "doInBackground: hostCv.size() " + hostCv.size());
        }


        return resultArrayList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        float subnetCount = mSubnetCount;
        float progressStatus = (values[0]/subnetCount) * 100;
        mProgressBar.setProgress((int) progressStatus);
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        Cursor cursor = db.query(HostsDatabaseHelper.HOSTS_TABLE,
                new String[]{
                        HostsDatabaseHelper.KEY_ID,
                        HostsDatabaseHelper.KEY_HOSTNAME,
                        HostsDatabaseHelper.KEY_MAC
                },
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            SimpleCursorAdapter listCursorAdapter = new SimpleCursorAdapter(mContext,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    new String[]{HostsDatabaseHelper.KEY_HOSTNAME, HostsDatabaseHelper.KEY_MAC},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0);

            mListView.setAdapter(listCursorAdapter);

            Toast.makeText(mContext,
                    "Найдено " + cursor.getCount() + " хостов",
                    Toast.LENGTH_SHORT).show();
        }
        //TODO: 03.04.2016 Закрыть курсор
        db.close();
    }
}
