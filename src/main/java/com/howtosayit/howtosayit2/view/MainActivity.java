package com.howtosayit.howtosayit2.view;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.howtosayit.howtosayit2.R;
import com.howtosayit.howtosayit2.controllers.MainController;
import com.howtosayit.howtosayit2.models.Phrase;


public class MainActivity extends Activity {
    private String LOG_TAG = "myLog";
    private Spinner lessons;
    private Button btnNext;
    private Button btnPrev;
    private Button btnSound;
    private Button btnStart;
    private TextView russianContent;
    private TextView englishContent;
    private TextView number;
    private MediaPlayer mp;
    private MainController controller;

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

        controller = new MainController(this);

        initViews();
        setUpListLessons();
        addButtonListeners();

    }

    private void initViews() {
        lessons = (Spinner)findViewById(R.id.spinnerLessons);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnPrev = (Button)findViewById(R.id.btnPrevious);
        btnSound = (Button)findViewById(R.id.btnSound);
        btnStart = (Button)findViewById(R.id.btnStart);
        russianContent = (TextView)findViewById(R.id.tvRussianContent);
        englishContent = (TextView)findViewById(R.id.tvEnglishContent);
        number = (TextView)findViewById(R.id.tvNumber);
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
                controller.setLessonPhrases(controller.getLessonFromDB("lesson = ?", item.toString()));

                if(!controller.getLessonPhrases().isEmpty()) {
                    nextPrevClickReaction(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void addButtonListeners() {
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = russianContent.getText();
                int index = getIndex(russianText.toString());

                if(index > 0) {
                    index--;
                    nextPrevClickReaction(index);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = russianContent.getText();
                int index = getIndex(russianText.toString());

                if(index != -1 && index < controller.getLessonPhrases().size() - 1) {
                    index++;
                    nextPrevClickReaction(index);
                }
            }
        });

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = russianContent.getText();
                int index = getIndex(russianText.toString());
                play(controller.getLessonPhrases().get(index).getStart(),
                            controller.getLessonPhrases().get(index).getStop(),
                                controller.getLessonPhrases().get(index).getLesson());
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUserActionActivity();
            }
        });
    }

    private void callUserActionActivity() {
        Intent intent = new Intent(this, UserActionActivity.class);
        startActivity(intent);
    }

    /*private void compareEditedText(final int index) {
        final Phrase phrase = lesson.getPhrases().get(index);

        setTexView(russianContent, phrase.getRus());
        setTexView(number, phrase.getNumber() + "/" + lesson.getPhrases().size());

        etEnglishContent.setEnabled(true);
        etEnglishContent.setText("");
        //btnNext.setEnabled(false);

        etEnglishContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(LOG_TAG, "edit " + etEnglishContent.getText().toString());
                String typed = etEnglishContent.getText().toString();
                String english = phrase.getEng();

                int length = typed.length();
                if(!typed.equalsIgnoreCase(english.substring(0, length))) {
                    etEnglishContent.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    etEnglishContent.setTextColor(Color.parseColor("#000000"));
                }
                *//*Log.d(LOG_TAG, "substring " + english.substring(0, length));
                Log.d(LOG_TAG, "typed length " + typed.length());
                Log.d(LOG_TAG, "is equals " + typed.equalsIgnoreCase(english.substring(0, length)));*//*

                if(english.equalsIgnoreCase(typed)) {
                    //Log.d(LOG_TAG, "typed = " + typed + "  english = " + english);
                    etEnglishContent.setTextColor(Color.parseColor("#0000FF"));
                    play(phrase.getStart(), phrase.getStop(), phrase.getLesson());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }*/


    private int getIndex(String text) {
        int index = -1;
        for(int i = 0; i < controller.getLessonPhrases().size(); i++) {
            if(controller.getLessonPhrases().get(i).getRus().equals(text)) {
                index = controller.getLessonPhrases().get(i).getNumber() - 1;
                break;
            }
        }
        return index;
    }

    private void nextPrevClickReaction(int index) {
        Phrase phrase = controller.getLessonPhrases().get(index);

        setTextView(russianContent, phrase.getRus());
        setTextView(englishContent, phrase.getEng());
        setTextView(number, phrase.getNumber() + "/" + controller.getLessonPhrases().size());

        play(phrase.getStart(), phrase.getStop(), phrase.getLesson());
    }

    private void setTextView(TextView tv, String value) {
        tv.setText(value);
    }

    private void play(int start, int stop, String fileName) {
        int id = getResources().getIdentifier(fileName, "raw", getPackageName());

        mp = MediaPlayer.create(this, id);
        mp.seekTo(start);
        mp.start();

        int delta = stop - start;
        Handler handler = new Handler();

        handler.postDelayed(stopPlayerTask, delta);
    }
}