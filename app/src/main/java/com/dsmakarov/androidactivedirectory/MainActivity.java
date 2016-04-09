package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    private String mCurrentIp;
    private String mCurrentSubnetMask;
    private SharedPreferences mSharedPreferences;

    public String s_dns1 ;
    public String s_dns2;
    public String s_gateway;
    public String s_ipAddress;
    public String s_leaseDuration;
    public String s_netmask;
    public String s_serverAddress;

    DhcpInfo dhcpInfo;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        dhcpInfo = wifiManager.getDhcpInfo();

        s_dns1 = "DNS 1: "+String.valueOf(dhcpInfo.dns1);
        s_dns2 = "DNS 2: "+String.valueOf(dhcpInfo.dns2);
        s_gateway = "Default Gateway: "+String.valueOf(dhcpInfo.gateway);
        s_ipAddress = "IP Address: "+String.valueOf(dhcpInfo.ipAddress);
        s_leaseDuration = "Lease Time: "+ String.valueOf(dhcpInfo.leaseDuration);
        s_netmask = "Subnet Mask: " + String.valueOf(dhcpInfo.netmask);
        s_serverAddress="Server IP: "+String.valueOf(dhcpInfo.serverAddress);

        Log.d(TAG, "Network Info\n"+s_dns1+"\n"+s_dns2+"\n"+s_gateway+"\n"+s_ipAddress+"\n"+s_leaseDuration+"\n"+s_netmask+"\n"+s_serverAddress );


        //Добавляем IP в SharedPreferences
        mSharedPreferences = getSharedPreferences(Host.PREF_IP_ADDRESS, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        //Получаем текущий IP-адресс
        mCurrentIp = NetHelper.getIPAddress(true);
        editor.putString("currentIp", mCurrentIp);

        if (wifiManager.isWifiEnabled()) {
            //Получаем текущую маску подсети
            mCurrentSubnetMask = NetHelper.intToIp(dhcpInfo.netmask);
        }

        editor.putString("currentSubnetMask", mCurrentSubnetMask);
        editor.apply();

        TextView macTextView = (TextView) findViewById(R.id.mac_textview);

        String mac = "MAC-address";
        try {
            InetAddress address = InetAddress.getByName(mCurrentIp);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
            mac = networkInterface.getDisplayName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            mac = "UnknownHostException";
        } catch (SocketException e) {
            e.printStackTrace();
            mac = "SocketException";
        } finally {
            macTextView.setText(NetHelper.getMACAddress(mac));
        }

        TextView ipTextView = (TextView) findViewById(R.id.ip_textview);
        ipTextView.setText(mCurrentIp);

        final EditText targetIpEditText = (EditText) findViewById(R.id.ping_edittext);
        final TextView resultTextView = (TextView) findViewById(R.id.result_textview);
        final Button pingButton = (Button) findViewById(R.id.start_ping_button);

        //resultTextView.setText(NetHelper.intToIp(dhcpInfo.netmask));

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
