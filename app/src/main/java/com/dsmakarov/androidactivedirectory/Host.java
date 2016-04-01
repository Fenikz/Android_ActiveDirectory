package com.dsmakarov.androidactivedirectory;

/**
 * Класс представляет собой реализацую абстрактной сущности хост
 */
public class Host {

    //Константа для хранения текущего IP адреса в SharedPreferences
    public static final String PREF_IP_ADDRESS = "PREF_IP_ADDRESS";

    String mIp;
    String mHostName;
    Boolean mIsOnline;

    public Host(String ip, String name, Boolean isOnline) {
        mIp = ip;
        mHostName = name;
        mIsOnline = isOnline;
    }

    public String getIp() {
        return mIp;
    }

    public String getName() {
        return mHostName;
    }

    public Boolean getStatus() {
        return mIsOnline;
    }
}
