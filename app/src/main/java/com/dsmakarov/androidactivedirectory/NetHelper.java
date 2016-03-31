package com.dsmakarov.androidactivedirectory;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Class contains methods to work with network
 *
 */
public class NetHelper {

    /**
     * Method ping address
     * @param address ip address or host
     * @return ping result
     */
    public static String ping(String address) {

        Process process;
        final String TAG = "NetHelper";

        try {
            process = Runtime.getRuntime().exec("ping -c 1 -w 1 " + address);

            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            BufferedReader stderr = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );

            String s = "";
            StringBuilder stringBuilder = new StringBuilder();


            while ((s = stdout.readLine()) != null || ((s = stderr.readLine()) != null)) {
                stringBuilder.append(s).append("\n");
            }

            process.destroy();

            // TODO: 31.03.2016 доабавить разбор строки и вывод краткого результата
            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Exception";
        }
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4 true = return ipv4, false = return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
