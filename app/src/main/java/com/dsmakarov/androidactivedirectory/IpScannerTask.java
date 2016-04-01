package com.dsmakarov.androidactivedirectory;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс для сканирования сети
 */
public class IpScannerTask extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {
    //Предполагаемая маска (255.255.255.0)

    private static final String TAG = "IpScannerTask";
    private ProgressBar mProgressBar;

    public IpScannerTask(ProgressBar progressBar) {
        mProgressBar = progressBar;
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
        String[] ipArrayList = new String[25];

        //i < 255
        for (int i = 0; i < 25; i++) {

            Log.d(TAG, "doInBackground: Цикл " + i);
            // TODO: 31.03.2016 Добавить ограничение на свой IP

            localIpsHashMap = new HashMap<>();
            localIpsHashMap.put("ip", subnetIp + i);


            //if (NetHelper.ping(subnetIp + i).contains("1 received")) {

            if (hostIsReachable(subnetIp + i)) {
                localIpsHashMap.put("status", "Enabled");
            } else {
                localIpsHashMap.put("status", "Disabled");
            }

            resultArrayList.add(localIpsHashMap);
            publishProgress(i);

            ipArrayList[i] = subnetIp + i;
        }

        //mPing(ipArrayList);

        return resultArrayList;
    }

    public boolean hostIsReachable(String value) {

        InetAddress address = null;
        try {
            address = InetAddress.getByName(value);
            boolean reachable = address.isReachable(1000);
            Log.d(TAG, "mPing: " + value + " " + reachable);

            if (reachable) {
                return true;
            } else {
                return false;
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean executeCommand(String host){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue " + mExitValue);

            Log.d(TAG, "executeCommand: " + " mExitValue " + mExitValue);

            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:" + ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mProgressBar.setProgress(values[0]);
        // TODO: 31.03.2016 Добавить в процентах (255 = 100%)
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }
}
