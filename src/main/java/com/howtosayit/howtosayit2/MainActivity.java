package com.howtosayit.howtosayit2;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private String LOG_TAG = "myLog";
    private Lesson lesson;
    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;
    private Spinner lessons;
    private Button btnNext;
    private Button btnPrev;
    private Button btnSound;
    private Button btnStart;
    private TextView content;
    private TextView number;
    private MediaPlayer mp;

    private Runnable stopPlayerTask = new Runnable() {
        @Override
        public void run() {
            mp.pause();
            mp.release();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();
        lesson = new Lesson();

        lessons = (Spinner)findViewById(R.id.spinnerLessons);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnPrev = (Button)findViewById(R.id.btnPrevious);
        btnSound = (Button)findViewById(R.id.btnSound);
        content = (TextView)findViewById(R.id.tvContent);
        number = (TextView)findViewById(R.id.tvNumber);
        btnStart = (Button)findViewById(R.id.btnStart);

        setUpListLessons();

        addButtonListeners();

    }

    private void setUpListLessons() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lessons,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lessons.setAdapter(adapter);

        lessons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);

                lesson.setEntityList(getLessonFromDB("lesson = ?", item.toString()));

                if(!lesson.getEntityList().isEmpty()) {
                    Entity entity = lesson.getEntityList().get(0);

                    setTexView(content, entity.getRus());
                    setTexView(number, entity.getNumber() + "/" + lesson.getEntityList().size());

                    play(entity.getStart(), entity.getStop(), entity.getLesson());
                } else {
                    setTexView(content, "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private List<Entity> getLessonFromDB(String selection, String value) {
        String[] selectionArgs = new String[] { value};

        Cursor c = db.query("lessons", null, selection, selectionArgs, null, null, null);
        List<Entity> values = new ArrayList<>();

        if(c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int rusColIndex = c.getColumnIndex("rus");
            int engColIndex = c.getColumnIndex("eng");
            int numberColIndex = c.getColumnIndex("number");
            int lessonIndex = c.getColumnIndex("lesson");
            int startIndex = c.getColumnIndex("start");
            int stopIndex = c.getColumnIndex("stop");

            do {
                values.add(new Entity(c.getInt(idColIndex), c.getString(rusColIndex),
                        c.getString(engColIndex), c.getInt(numberColIndex),
                        c.getString(lessonIndex), c.getInt(startIndex), c.getInt(stopIndex)));
            } while(c.moveToNext());
        } else {
            Log.d(LOG_TAG, "0 rows");
        }
        c.close();

        return values;
    }

    private void addButtonListeners() {
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = content.getText();
                int index = getIndex(russianText.toString());

                if(index > 0) {
                    index--;

                    Entity entity = lesson.getEntityList().get(index);
                    setTexView(content, entity.getRus());
                    setTexView(number, entity.getNumber() + "/" + lesson.getEntityList().size());

                    play(entity.getStart(), entity.getStop(), entity.getLesson());
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = content.getText();
                int index = getIndex(russianText.toString());

                if(index != -1 && index < lesson.getEntityList().size() - 1) {
                    index++;

                    Entity entity = lesson.getEntityList().get(index);
                    setTexView(content, entity.getRus());
                    setTexView(number, entity.getNumber() + "/" + lesson.getEntityList().size());

                    play(entity.getStart(), entity.getStop(), entity.getLesson());
                }
            }
        });

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = content.getText();
                int index = getIndex(russianText.toString());
                play(lesson.getEntityList().get(index).getStart(),
                            lesson.getEntityList().get(index).getStop(),
                                    lesson.getEntityList().get(index).getLesson());
            }
        });
    }

    private int getIndex(String text) {
        int index = -1;
        for(int i = 0; i < lesson.getEntityList().size(); i++) {
            if(lesson.getEntityList().get(i).getRus().equals(text)) {
                index = lesson.getEntityList().get(i).getNumber() - 1;
                break;
            }
        }
        return index;
    }

    private void setTexView(TextView tv, String value) {
        tv.setText(value);
    }

    private void play(int start, int stop, String fileName) {
     /*   MediaPlayer mp = MediaPlayer.create(this, R.raw.lesson1);
        //Звук будет проигрываться только 1 раз:
        mp.setLooping(false);
        //Установка обработчика события на момент готовности проигрывателя:
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp)
            {
                //При готовности к проигрыванию запуск вывода звука:
                //mp.seekTo();
                mp.start();
                //mp.pause();
            }
        });
*/


        int id = getResources().getIdentifier(fileName, "raw", getPackageName());

        mp = MediaPlayer.create(this, id);
        mp.seekTo(start);
        mp.start();

        int delta = stop - start;
        Handler handler = new Handler();

        handler.postDelayed(stopPlayerTask, delta);

        /*mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(LOG_TAG, "release");
                mp.release();
            }
        });*/

    }

}