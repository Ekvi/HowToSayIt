package com.howtosayit.howtosayit2.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.howtosayit.howtosayit2.R;
import com.howtosayit.howtosayit2.controllers.MainController;
import com.howtosayit.howtosayit2.listeners.AudioPlayerListener;
import com.howtosayit.howtosayit2.models.Lesson;
import com.howtosayit.howtosayit2.models.Phrase;
import com.howtosayit.howtosayit2.utils.PlayAudio;


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
    private PlayAudio audio;
    private MainController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audio = new PlayAudio();
        controller = new MainController(this);

        initViews();
        setUpListLessons();
        addButtonListeners();
        checkAudioListener();

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

                switchButtons(false);

                makeSound(controller.getLessonPhrases().get(index).getStart(),
                            controller.getLessonPhrases().get(index).getStop(),
                                controller.getLessonPhrases().get(index).getLesson());

                checkAudioListener();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUserActionActivity();
            }
        });
    }

    private void checkAudioListener() {
        audio.setOnEventListener(new AudioPlayerListener() {
            @Override
            public void fireStopAudio() {
                switchButtons(true);
            }
        });
    }

    private void callUserActionActivity() {
        Lesson lesson = new Lesson();
        lesson.setPhrases(controller.getLessonPhrases());

        Intent intent = new Intent(this, UserActionActivity.class);
        intent.putExtra("lesson", lesson);
        startActivity(intent);
    }

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

        switchButtons(false);
        makeSound(phrase.getStart(), phrase.getStop(), phrase.getLesson());
        checkAudioListener();
    }

    private void setTextView(TextView tv, String value) {
        tv.setText(value);
    }

    private void makeSound(int start, int stop, String lesson) {
        audio.play(this, start, stop, lesson);
    }

    private void switchButtons(boolean flag) {
        btnNext.setEnabled(flag);
        btnPrev.setEnabled(flag);
        btnSound.setEnabled(flag);
    }
}