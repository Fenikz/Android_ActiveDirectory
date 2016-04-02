package com.dsmakarov.androidactivedirectory;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dmitry on 02.04.2016.
 */
public class HostsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "HostsDb";
    private static final int DB_VERSION = 1;
    private static final String TAG = "DbHelper" ;

    public HostsDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static final String HOSTS_TABLE = "Hosts";

    public static final String KEY_ID = "_id";
    public static final String KEY_HOSTNAME = "hostname";
    public static final String KEY_IP = "ip";

    private static final String DATABASE_CREATE = "create table " + HOSTS_TABLE + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_HOSTNAME + " text, "
            + KEY_IP + " text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion +
                ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + HOSTS_TABLE);
        onCreate(db);
    }

    // TODO: 02.04.2016 Обработать вставку значений в ДБ 
    public static void insertHost(String hostname, String ip) {
        ContentValues hostValues = new ContentValues();
        hostValues.put(KEY_HOSTNAME, hostname);
        hostValues.put(KEY_IP, ip);
    }
}
