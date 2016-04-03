package com.dsmakarov.androidactivedirectory;

import java.io.BufferedReader;
import java.io.FileReader;
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

    private static final String TAG = "NetHelper";

    /**
     * Method ping address
     * @param address ip address or host
     * @return ping result
     */
    public static String ping(String address) {
// TODO: 03.04.2016 Изменить String на InetAddress
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

    // TODO: 03.04.2016 Переписать Ping с помощью process builder'а и вывести Err-поток в основной
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

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }
}
