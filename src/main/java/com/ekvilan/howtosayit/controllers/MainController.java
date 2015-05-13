package com.ekvilan.howtosayit.controllers;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ekvilan.howtosayit.models.Lesson;
import com.ekvilan.howtosayit.models.Phrase;
import com.ekvilan.howtosayit.utils.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    private String LOG_TAG = "myLog";
    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;
    private Lesson lesson;

    public static MainController controller;

    private MainController(){}

    private MainController(Context activity) {
        dbHelper = new DataBaseHelper(activity);
        db = dbHelper.getWritableDatabase();
        lesson = new Lesson();
    }

    public static MainController getController(Context context) {
        if(controller == null) {
            controller = new MainController(context);
        }
        return controller;
    }

    public Lesson getLessonFromDB(String selection, String value) {
        String[] selectionArgs = new String[] {value};

        Cursor c = db.query("lessons", null, selection, selectionArgs, null, null, null);
        List<Phrase> phrases = new ArrayList<>();
        String name = "";

        if(c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int rusColIndex = c.getColumnIndex("rus");
            int engColIndex = c.getColumnIndex("eng");
            int numberColIndex = c.getColumnIndex("number");
            int lessonIndex = c.getColumnIndex("lesson");
            int startIndex = c.getColumnIndex("start");
            int stopIndex = c.getColumnIndex("stop");

            name = c.getString(lessonIndex);

            do {
                phrases.add(new Phrase(c.getInt(idColIndex), c.getString(rusColIndex),
                        c.getString(engColIndex), c.getInt(numberColIndex),
                        c.getString(lessonIndex), c.getInt(startIndex), c.getInt(stopIndex)));
            } while(c.moveToNext());
        } else {
            Log.d(LOG_TAG, "0 rows");
        }
        c.close();

        Lesson les = new Lesson(name, phrases);
        setLesson(les);
        
        return les;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public int getIndex(String text) {
        int index = -1;

        for(int i = 0; i < lesson.getPhrases().size(); i++) {
            if(lesson.getPhrases().get(i).getRus().equals(text)) {
                index = lesson.getPhrases().get(i).getNumber() - 1;
                break;
            }
        }
        return index;
    }
}
