package com.howtosayit.howtosayit2.controllers;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.howtosayit.howtosayit2.models.Lesson;
import com.howtosayit.howtosayit2.utils.DataBaseHelper;
import com.howtosayit.howtosayit2.models.Phrase;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    private String LOG_TAG = "myLog";
    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;
    private Lesson lesson;

    public MainController(Context activity) {
        dbHelper = new DataBaseHelper(activity);
        db = dbHelper.getWritableDatabase();
        lesson = new Lesson();
    }

    public List<Phrase> getLessonFromDB(String selection, String value) {
        String[] selectionArgs = new String[] {value};

        Cursor c = db.query("lessons", null, selection, selectionArgs, null, null, null);
        List<Phrase> phrases = new ArrayList<>();

        if(c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int rusColIndex = c.getColumnIndex("rus");
            int engColIndex = c.getColumnIndex("eng");
            int numberColIndex = c.getColumnIndex("number");
            int lessonIndex = c.getColumnIndex("lesson");
            int startIndex = c.getColumnIndex("start");
            int stopIndex = c.getColumnIndex("stop");

            do {
                phrases.add(new Phrase(c.getInt(idColIndex), c.getString(rusColIndex),
                        c.getString(engColIndex), c.getInt(numberColIndex),
                        c.getString(lessonIndex), c.getInt(startIndex), c.getInt(stopIndex)));
            } while(c.moveToNext());
        } else {
            Log.d(LOG_TAG, "0 rows");
        }
        c.close();

        return phrases;
    }

    public void setLessonPhrases(List<Phrase> phrases) {
        lesson.setPhrases(phrases);
    }

    public List<Phrase> getLessonPhrases() {
        return lesson.getPhrases();
    }
}
