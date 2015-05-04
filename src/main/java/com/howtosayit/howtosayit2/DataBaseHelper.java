package com.howtosayit.howtosayit2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = "myLog";
    private final String DB_PATH = "/data/data/com.howtosayit.howtosayit2/databases/";
    private static final String DB_NAME = "how_to_say_it.db";
    private Context context;
    private SQLiteDatabase db;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;

        if (checkDatabase()) {
            openDatabase();
        } else {
            createDatabase();
        }
    }

    private boolean checkDatabase() {
        boolean exist = false;
        try {
            File dbFile = new File(DB_PATH + DB_NAME);
            exist = dbFile.exists();
        } catch(SQLiteException e) {
            Log.d(LOG_TAG, "Database doesn't exist!");
        }
        return exist;
    }

    private void openDatabase() throws android.database.SQLException {
        String path = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private void createDatabase() {
        db = this.getReadableDatabase();
        copyDatabase();
    }

    private void copyDatabase() {
        Log.i("Database", "New DB is copying to device!");

        byte[] buffer = new byte[1024];
        OutputStream output;
        InputStream input;
        int length;

        try {
            input = context.getAssets().open(DB_NAME);
            output = new FileOutputStream(DB_PATH + DB_NAME);

            while((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.close();
            output.flush();
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

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }
}
