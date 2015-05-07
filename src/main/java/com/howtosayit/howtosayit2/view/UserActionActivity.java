package com.howtosayit.howtosayit2.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.howtosayit.howtosayit2.R;
import com.howtosayit.howtosayit2.controllers.MainController;
import com.howtosayit.howtosayit2.models.Lesson;
import com.howtosayit.howtosayit2.models.Phrase;
import com.howtosayit.howtosayit2.utils.CheckAnswer;
import com.howtosayit.howtosayit2.utils.PlayAudio;

import java.util.Date;


public class UserActionActivity extends Activity {
    private String LOG_TAG = "myLog";
    private Spinner lessons;
    private Button btnNext;
    private Button btnHelp;
    private TextView russianContent;
    private EditText answer;
    private TextView number;
    private Lesson lesson;
    private PlayAudio audio;
    private Phrase phrase;
    private AnswerTextWatcher watcher;
    private CheckAnswer checkAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_action);

        audio = new PlayAudio();
        watcher = new AnswerTextWatcher();
        checkAnswer = new CheckAnswer();
        initViews();

        Intent intent = getIntent();
        lesson = intent.getParcelableExtra("lesson");

        addButtonListeners();
        answer.addTextChangedListener(watcher);

        setUpActivity(0);
    }

    private void initViews() {
        lessons = (Spinner)findViewById(R.id.spinnerLessons);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnHelp = (Button)findViewById(R.id.btnHelp);
        russianContent = (TextView)findViewById(R.id.tvRussianContent);
        answer = (EditText)findViewById(R.id.etEnglish);
        number = (TextView)findViewById(R.id.tvNumber);
    }

    private void setUpActivity(int index) {
        phrase = lesson.getPhrases().get(index);
        checkAnswer.setCorrect(phrase.getEng());

        setTextView(russianContent, phrase.getRus());
        setTextView(number, "left " + (lesson.getPhrases().size() - (phrase.getNumber() - 1)));

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
            String color = checkAnswer.isCorrect(answer.getText().toString()) ? "#000000" : "#FF0000";
            setColor(answer, color);

            if(checkAnswer.isCorrectAnswer(answer.getText().toString())) {
                setColor(answer, "#0000FF");
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
                CharSequence russianText = russianContent.getText();
                int index = getIndex(russianText.toString());

                if(index != -1 && index < lesson.getPhrases().size() - 1) {
                    index++;
                    setUpActivity(index);
                }
            }
        });
    }

    private int getIndex(String text) {
        int index = -1;
        for(int i = 0; i < lesson.getPhrases().size(); i++) {
            if(lesson.getPhrases().get(i).getRus().equals(text)) {
                index = lesson.getPhrases().get(i).getNumber() - 1;
                break;
            }
        }
        return index;
    }

    private void setColor(EditText et, String color) {
        et.setTextColor(Color.parseColor(color));
    }
}
