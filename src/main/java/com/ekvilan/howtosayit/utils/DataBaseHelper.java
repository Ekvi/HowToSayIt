package com.ekvilan.howtosayit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DataBaseHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = "myLog";
    private final String KEY_DB_VER = "db_ver";
    private static final String DB_NAME = "how_to_say_it.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private SQLiteDatabase db;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;

        initDb();
    }

    private void initDb() {
        if (dbExist()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int dbVersion = prefs.getInt(KEY_DB_VER, 1);
            if(dbVersion != DATABASE_VERSION) {
                File dbFile = context.getDatabasePath(DB_NAME);
                if (!dbFile.delete()) {
                    Log.w(LOG_TAG, "Unable to update database");
                }
                createDatabase();
            } else {
                openDatabase();
            }
        } else {
            createDatabase();
        }
    }

    private boolean dbExist() {
        return context.getDatabasePath(DB_NAME).exists();
    }

    private void openDatabase() throws android.database.SQLException {
        String path =  context.getDatabasePath(DB_NAME).toString();
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private void createDatabase() {
        db = this.getReadableDatabase();
        copyDatabase();
        saveDbVersion();
    }

    private void copyDatabase() {
        Log.i("Database", "New DB is copying to device!");

        String path = context.getDatabasePath(DB_NAME).getPath();
        byte[] buffer = new byte[1024];
        OutputStream output;
        InputStream input;
        int length;

        try {
            input = context.getAssets().open(DB_NAME);
            output = new FileOutputStream(path);

            while((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            input.close();

            Log.i("Database", "New DB was copied to device");

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        if(db != null) {
            db.close();
        }
        super.close();
    }

    private void saveDbVersion() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_DB_VER, DATABASE_VERSION);
        editor.commit();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }
}
