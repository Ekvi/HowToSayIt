package com.howtosayit.howtosayit2.view;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.howtosayit.howtosayit2.R;


public class UserActionActivity extends Activity {
    private Spinner lessons;
    private Button btnNext;
    private Button btnHelp;
    private TextView russianContent;
    private EditText etEnglishContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_action);

        initViews();
    }

    private void initViews() {
        lessons = (Spinner)findViewById(R.id.spinnerLessons);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnHelp = (Button)findViewById(R.id.btnHelp);
        russianContent = (TextView)findViewById(R.id.tvRussianContent);
        etEnglishContent = (EditText)findViewById(R.id.etEnglish);
    }


}
