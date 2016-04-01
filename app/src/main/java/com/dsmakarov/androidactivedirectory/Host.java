package com.dsmakarov.androidactivedirectory;

/**
 * Класс представляет собой реализацую абстрактной сущности хост
 */
public class Host {

    String mIp;
    String mName;
    Boolean mIsOnline;

    public Host(String ip, String name, Boolean isOnline) {
        mIp = ip;
        mName = name;
        mIsOnline = isOnline;
    }

    public String getIp() {
        return mIp;
    }

    public String getName() {
        return mName;
    }

    public Boolean getStatus() {
        return mIsOnline;
    }
}
