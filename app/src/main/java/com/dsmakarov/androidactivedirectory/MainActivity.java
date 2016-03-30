package com.dsmakarov.androidactivedirectory;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    public static final String TAG = "PING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: " + ping());

    }

    private String ping() {

        Process process;

        try {
            process = Runtime.getRuntime().exec("ping -c 1 -w 1 google.com");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String s = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s).append("\n");
            }

            process.destroy();
            Toast.makeText(getApplicationContext(), "Reachable", Toast.LENGTH_SHORT).show();
            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Not worked";
        }
    }

    /*
    executeCmd("ping -c 1 -w 1 google.com", false);

    public static String executeCmd(String cmd, boolean sudo){
        try {

            Process p;
            if(!sudo)
                p= Runtime.getRuntime().exec(cmd);
            else{
                p= Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            }
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            String res = "";
            while ((s = stdInput.readLine()) != null) {
                res += s + "\n";
            }
            p.destroy();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
    */
}
