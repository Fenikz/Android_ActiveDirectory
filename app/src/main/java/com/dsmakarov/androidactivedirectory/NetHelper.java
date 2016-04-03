package com.dsmakarov.androidactivedirectory;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Class contains methods to work with network
 *
 */
public class NetHelper {

    private static final String TAG = "NetHelper";

    /**
     * Method ping address
     * @param address ip address or host
     * @return ping result
     */
    public static String ping(String address) {

        Process process;

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

            //process.destroy();

            // TODO: 31.03.2016 доабавить разбор строки и вывод краткого результата
            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Exception";
        }
    }

    public static String testPing(String ip) {

        Process process;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            process = new ProcessBuilder()
                    .command("/system/bin/ping", ip)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String s = "";
            while ((s = stdout.readLine()) != null) {
                stringBuilder.append(s).append("\n");
            }

            process.destroy();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static String multiPing(String[] addressArray) {

        Process process = null;
        Runtime runtime = Runtime.getRuntime();
        String s = "";
        StringBuilder resultStringBuilder = new StringBuilder();

        try {
            // TODO: 01.04.2016 Переделать на множественный вызов в рамках одного процесса

            for (int i = 0; i < addressArray.length; i++) {
                Log.d(TAG, "multiPing: address " + addressArray[i]);
                process = runtime.exec("ping -c 1 -w 1 " + addressArray[i]);

                BufferedReader stdout = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );

                BufferedReader stderr = new BufferedReader(
                        new InputStreamReader(process.getErrorStream())
                );

                StringBuilder tempStringBuilder = new StringBuilder();
                //Читаем построчно результат PING
                while ((s = stdout.readLine()) != null || ((s = stderr.readLine()) != null)) {
                    tempStringBuilder.append(s).append("\n");
                }

                resultStringBuilder.append(addressArray[i]);

                if (tempStringBuilder.toString().contains("1 received")) {
                    resultStringBuilder.append(" Enabled");
                } else {
                    resultStringBuilder.append(" Disabled");
                }
                resultStringBuilder.append("\n");

                stdout.close();
                stderr.close();
                process.destroy();
            }

            // TODO: 31.03.2016 доабавить разбор строки и вывод краткого результата
            Log.d(TAG, "multiPing: " + resultStringBuilder.toString());
            return resultStringBuilder.toString();

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

    /**
     * Try to extract a hardware MAC address from a given IP address using the
     * ARP cache (/proc/net/arp).<br>
     * <br>
     * We assume that the file has this structure:<br>
     * <br>
     * IP address       HW type     Flags       HW address            Mask     Device
     * 192.168.18.11    0x1         0x2         00:04:20:06:55:1a     *        eth0
     * 192.168.18.36    0x1         0x2         00:22:43:ab:2a:5b     *        eth0
     *
     * @param ip
     * @return the MAC from the ARP cache
     */
    public static String getMacFromArpCache(String ip) {
        if (ip == null || ip.equals("")) {
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0])) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        return mac;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // TODO: 01.04.2016 Дописать процедуру 
    public static String getHostName(String address) {

        return "hostname";
    }

    public static boolean shutdownPc(String host) {

        /*
        set computertoshutdown=COMPUTERNAME
        set timetoshutdown =TIMEtoSHUTDOWN
        set message=MESSAGE

        shutdown -s -m \\%computertoshutdown% -t %timetoshutdown% -c "%message%"
         */

        return true;
    }

    /*
    String	getHostAddress()
    Returns the numeric representation of this IP address (such as "127.0.0.1").

    String	getHostName()
    Returns the host name corresponding to this IP address.
     */
}
