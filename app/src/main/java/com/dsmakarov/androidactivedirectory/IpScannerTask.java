package com.dsmakarov.androidactivedirectory;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

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
    private int mSubnetCount = 255;

    public IpScannerTask(Context context, ProgressBar progressBar, ListView listView) {
        mProgressBar = progressBar;
        mListView = listView;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

        ArrayList<HashMap<String, String>> resultArrayList = new ArrayList<>();
        HashMap<String, String> localIpsHashMap;

        Log.d(TAG, "doInBackground: Начало выполнения " + params[0]);

        //Позиция последней точки в IP-адресе
        int lastDot = params[0].lastIndexOf(".");
        String subnetIp = params[0].substring(0, lastDot + 1);
        //new String[256]
        //String[] ipArrayList = new String[mSubnetCount];

        //i < 255
        for (int i = 0; i < mSubnetCount; i++) {

            Log.d(TAG, "doInBackground: Цикл " + i);
            // TODO: 31.03.2016 Добавить ограничение на свой IP

            localIpsHashMap = new HashMap<>();

            try {
                if (InetAddress.getByName(subnetIp + i).isReachable(1000)) {
                    localIpsHashMap.put("ip", subnetIp + i);
                    localIpsHashMap.put("status", "Enabled");
                    resultArrayList.add(localIpsHashMap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            publishProgress(i);

            //ipArrayList[i] = subnetIp + i;
        }

        return resultArrayList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        float subnetCount = mSubnetCount;
        float progressStatus = (values[0]/subnetCount) * 100;
        Log.d(TAG, "onProgressUpdate: progress " + progressStatus);
        mProgressBar.setProgress((int) progressStatus);
        // TODO: 31.03.2016 Добавить в процентах (255 = 100%)
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        SimpleAdapter adapter = new SimpleAdapter(mContext,
                hashMaps,
                android.R.layout.simple_list_item_2,
                new String[] {"ip", "status"},
                new int[] {android.R.id.text1, android.R.id.text2});
        mListView.setAdapter(adapter);
    }
}
