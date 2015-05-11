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
import android.widget.Toast;

import com.howtosayit.howtosayit2.R;
import com.howtosayit.howtosayit2.controllers.MainController;
import com.howtosayit.howtosayit2.listeners.AudioPlayerListener;
import com.howtosayit.howtosayit2.models.Phrase;
import com.howtosayit.howtosayit2.utils.PlayAudio;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private String LOG_TAG = "myLog";
    private final int LESSONS_SIZE = 429;
    public static final String LESSON = "lesson";
    public static final String LESSON_RU = "Урок";
    private Spinner lessons;
    private Button btnNext;
    private Button btnPrev;
    private Button btnSound;
    private Button btnStart;
    private TextView russianContent;
    private TextView englishContent;
    private TextView number;
    private TextView lessonNumber;
    private PlayAudio audio;
    private MainController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audio = new PlayAudio();
        controller = MainController.getController(this);

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
        lessonNumber = (TextView)findViewById(R.id.tvLessonNumber);
    }

    private void setUpListLessons() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, fillLessonsNames());
        adapter.setDropDownViewResource(R.layout.spinner_layout);

        lessons.setAdapter(adapter);

        lessons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);

                controller.getLessonFromDB(
                        LESSON + " = ?", item.toString().replaceAll(LESSON_RU, LESSON));

                if(!controller.getLesson().getPhrases().isEmpty()) {
                    fillActivityContent(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private List<String> fillLessonsNames() {
        List<String> lessonsNames = new ArrayList<>();
        for(int i = 0; i < LESSONS_SIZE; i++) {
            lessonsNames.add(LESSON_RU + (i + 1));
        }
        return lessonsNames;
    }

    private void addButtonListeners() {
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = russianContent.getText();
                int index = getIndex(russianText.toString());

                if(index > 0) {
                    index--;
                    fillActivityContent(index);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = russianContent.getText();
                int index = getIndex(russianText.toString());

                if(index != -1 && index < controller.getLesson().getPhrases().size() - 1) {
                    index++;
                    fillActivityContent(index);
                }
            }
        });

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence russianText = russianContent.getText();
                int index = getIndex(russianText.toString());

                switchButtons(false);

                makeSound(controller.getLesson().getPhrases().get(index).getStart(),
                            controller.getLesson().getPhrases().get(index).getStop(),
                                controller.getLesson().getPhrases().get(index).getLesson());

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
        Intent intent = new Intent(this, UserActionActivity.class);
        startActivity(intent);
    }

    private int getIndex(String text) {
        int index = -1;

        for(int i = 0; i < controller.getLesson().getPhrases().size(); i++) {
            if(controller.getLesson().getPhrases().get(i).getRus().equals(text)) {
                index = controller.getLesson().getPhrases().get(i).getNumber() - 1;
                break;
            }
        }
        return index;
    }

    private void fillActivityContent(int index) {
        Phrase phrase = controller.getLesson().getPhrases().get(index);

        setTextView(russianContent, phrase.getRus());
        setTextView(englishContent, phrase.getEng());
        setTextView(lessonNumber, phrase.getLesson().replaceAll(LESSON, LESSON_RU + " "));
        setTextView(number, phrase.getNumber() + "/" + controller.getLesson().getPhrases().size());

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

    @Override
    protected void onRestart() {
        super.onRestart();
        controller.getLessonFromDB(LESSON + " = ?", getNextLessonName());
        int position = lessons.getSelectedItemPosition();
        if(position < LESSONS_SIZE - 1) {
            lessons.setSelection(++position);
            fillActivityContent(0);
        }
    }

    private String getNextLessonName() {
        int num = Integer.parseInt(controller.getLesson().getName().replaceAll("\\D", ""));

        if(num < LESSONS_SIZE) {
            num++;
        } else {
            Toast.makeText(this, "Поздравляю, вы прошли весь курс.", Toast.LENGTH_SHORT).show();
        }
        return LESSON + num;
    }
}