package com.ekvilan.howtosayit.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ekvilan.howtosayit.R;
import com.ekvilan.howtosayit.controllers.MainController;
import com.ekvilan.howtosayit.listeners.AudioPlayerListener;
import com.ekvilan.howtosayit.models.Lesson;
import com.ekvilan.howtosayit.models.Phrase;
import com.ekvilan.howtosayit.utils.CheckAnswer;
import com.ekvilan.howtosayit.utils.PlayAudio;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;


public class UserActionActivity extends Activity {
    private String LOG_TAG = "myLog";
    private final String COLOR_BLACK = "#000000";
    private final String COLOR_RED = "#FF0000";
    private final String COLOR_BLUE = "#0000FF";
    private final String INDEX = "index";

    private TextView tvLesson;
    private Button btnAccepted;
    private Button btnHelp;
    private TextView russianContent;
    private EditText answer;
    private TextView number;

    private PlayAudio audio;
    private Phrase phrase;
    private CheckAnswer checkAnswer;
    private MainController controller;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_action);

        audio = PlayAudio.getInstance();
        checkAnswer = new CheckAnswer();
        controller = MainController.getController(this);

        initViews();

        addButtonListeners();

        answer.addTextChangedListener(new AnswerTextWatcher());

        setUpActivity();

        showBanner();
    }

    private void initViews() {
        tvLesson = (TextView)findViewById(R.id.tvLesson);
        btnAccepted = (Button)findViewById(R.id.btnAccepted);
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
        setTextView(number, getResources().getString(R.string.left_phrases_message) + " "
                + (controller.getLesson().getPhrases().size() - index));

        answer.setEnabled(true);
        answer.setText("");
        answer.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(answer, InputMethodManager.SHOW_IMPLICIT);

        btnAccepted.setEnabled(false);
        btnAccepted.setBackgroundResource(R.drawable.btn_accepted_off);
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
                answer.setEnabled(false);
                makeSound(phrase.getStart(), phrase.getStop(), phrase.getLesson());
                audio.setOnEventListener(new AudioPlayerListener() {
                    @Override
                    public void fireStopAudio() {
                        btnAccepted.setEnabled(true);
                        btnAccepted.setBackgroundResource(R.drawable.btn_accepted_on);
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void makeSound(int start, int stop, String lesson) {
        audio.play(this, start, stop, lesson);
    }

    private void addButtonListeners() {
        btnAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < controller.getLesson().getPhrases().size() - 1) {
                    index++;
                    setUpActivity();
                } else if (index == controller.getLesson().getPhrases().size() - 1) {
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
        intent.putExtra(INDEX, index);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INDEX, index);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        index = savedInstanceState.getInt(INDEX);
        setUpActivity();
    }

    private void showBanner() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
