package com.howtosayit.howtosayit2.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.howtosayit.howtosayit2.R;
import com.howtosayit.howtosayit2.controllers.MainController;
import com.howtosayit.howtosayit2.models.Lesson;
import com.howtosayit.howtosayit2.models.Phrase;
import com.howtosayit.howtosayit2.utils.CheckAnswer;
import com.howtosayit.howtosayit2.utils.PlayAudio;

import java.util.List;


public class UserActionActivity extends Activity {
    private String LOG_TAG = "myLog";
    private final String COLOR_BLACK = "#000000";
    private final String COLOR_RED = "#FF0000";
    private final String COLOR_BLUE = "#0000FF";
    private TextView tvLesson;
    private Button btnNext;
    private Button btnHelp;
    private TextView russianContent;
    private EditText answer;
    private TextView number;
    private PlayAudio audio;
    private Phrase phrase;
    private AnswerTextWatcher watcher;
    private CheckAnswer checkAnswer;
    private MainController controller;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_action);

        audio = new PlayAudio();
        watcher = new AnswerTextWatcher();
        checkAnswer = new CheckAnswer();
        controller = MainController.getController(this);

        initViews();

        addButtonListeners();
        answer.addTextChangedListener(watcher);

        setUpActivity();
    }

    private void initViews() {
        tvLesson = (TextView)findViewById(R.id.tvLesson);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnHelp = (Button)findViewById(R.id.btnHelp);
        russianContent = (TextView)findViewById(R.id.tvRussianContent);
        answer = (EditText)findViewById(R.id.etEnglish);
        number = (TextView)findViewById(R.id.tvNumber);
    }

    private void setUpActivity() {
        phrase = controller.getLesson().getPhrases().get(index);
        checkAnswer.setCorrect(phrase.getEng());

        setTextView(tvLesson, MainActivity.LESSON_RU + " " + phrase.getLesson().replaceAll("\\D", ""));
        setTextView(russianContent, phrase.getRus());
        setTextView(number, "осталось "
                + (controller.getLesson().getPhrases().size() - index));

        answer.setText("");
        btnNext.setEnabled(false);
    }

    private void setTextView(TextView tv, String value) {
        tv.setText(value);
    }

    private class AnswerTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!answer.getText().toString().isEmpty()) {
                setTextView(russianContent, phrase.getRus());
            }

            String color = checkAnswer.isCorrect(answer.getText().toString()) ? COLOR_BLACK : COLOR_RED;
            setColor(answer, color);

            if(checkAnswer.isCorrectAnswer(answer.getText().toString())) {
                setColor(answer, COLOR_BLUE);
                makeSound(phrase.getStart(), phrase.getStop(), phrase.getLesson());
                btnNext.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void makeSound(int start, int stop, String lesson) {
        audio.play(this, start, stop, lesson);
    }

    private void addButtonListeners() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < controller.getLesson().getPhrases().size() - 1) {
                    index++;
                    setUpActivity();
                } else if(index == controller.getLesson().getPhrases().size() - 1){
                    returnToMainActivity();
                }
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextView(russianContent, phrase.getEng());
                addValueForRepeat();
            }
        });
    }

    private void setColor(EditText et, String color) {
        et.setTextColor(Color.parseColor(color));
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("index", index);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void addValueForRepeat() {
        Lesson lesson = controller.getLesson();
        List<Phrase> phrases = lesson.getPhrases();

        phrases.add(phrase);
        lesson.setPhrases(phrases);
        controller.setLesson(lesson);
    }
}
