package com.ekvilan.howtosayit.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ekvilan.howtosayit.R;
import com.ekvilan.howtosayit.controllers.MainController;
import com.ekvilan.howtosayit.listeners.AudioPlayerListener;
import com.ekvilan.howtosayit.models.Phrase;
import com.ekvilan.howtosayit.utils.PlayAudio;
import com.ekvilan.howtosayit.utils.expansion.AudioDownloaderService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements IDownloaderClient {
    private String LOG_TAG = "myLog";
    private final int LESSONS_SIZE = 429;
    private final String POSITION = "position";
    private final String INDEX = "index";
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
    private SharedPreferences preferences;

    private int index = 0;
    private int globalPosition;

    private IDownloaderService remoteService;
    private IStub downloaderClientStub;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!expansionFilesDelivered()) {
            try {
                Intent launchIntent = this.getIntent();

                Intent notifierIntent = new Intent(this, MainActivity.class);
                notifierIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                notifierIntent.setAction(launchIntent.getAction());

                if (launchIntent.getCategories() != null) {
                    for (String category : launchIntent.getCategories()) {
                        notifierIntent.addCategory(category);
                    }
                }

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this, 0, notifierIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                int startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(
                        this, pendingIntent, AudioDownloaderService.class);

                if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
                    downloaderClientStub = DownloaderClientMarshaller.CreateStub(
                            this, AudioDownloaderService.class);

                    setUpDownloadUI();
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(LOG_TAG, "Can't find own package!");
                e.printStackTrace();
            }
        } else {
            startApp();
        }
    }

    private boolean expansionFilesDelivered() {
        for (XAPKFile xf : xAPKS) {
            String fileName = Helpers.getExpansionAPKFileName(this, xf.mIsMain, xf.mFileVersion);
            if (!Helpers.doesFileExist(this, fileName, xf.mFileSize, false)) {
                return false;
            }
        }
        return true;
    }

    private static final XAPKFile[] xAPKS = {new XAPKFile(true, 2, 194814255L)};

    private static class XAPKFile {
        public final boolean mIsMain;
        public final int mFileVersion;
        public final long mFileSize;

        XAPKFile(boolean isMain, int fileVersion, long fileSize) {
            mIsMain = isMain;
            mFileVersion = fileVersion;
            mFileSize = fileSize;
        }
    }

    private void setUpDownloadUI() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(getResources().getString(R.string.downloadMessage));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void startApp() {
        audio = PlayAudio.getInstance();
        controller = MainController.getController(this);

        initViews();

        setUpListLessons(loadSpinnerPosition());
        addButtonListeners();
        checkAudioListener();

        showBanner();
    }

    @Override
    protected void onResume() {
        if (null != downloaderClientStub) {
            downloaderClientStub.connect(this);
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (null != downloaderClientStub) {
            downloaderClientStub.disconnect(this);
        }
        super.onStop();
    }

    @Override
    public void onServiceConnected(Messenger m) {
        remoteService = DownloaderServiceMarshaller.CreateProxy(m);
        remoteService.onClientUpdated(downloaderClientStub.getMessenger());
    }

    @Override
    public void onDownloadProgress(DownloadProgressInfo progress) {
        long percents = progress.mOverallProgress * 100 / progress.mOverallTotal;
        progressDialog.setProgress((int) percents);
    }

    @Override
    public void onDownloadStateChanged(int newState) {
        switch (newState) {
            case STATE_DOWNLOADING:
                Log.v(LOG_TAG, "Downloading...");
                break;
            case STATE_COMPLETED:
                progressDialog.setMessage(getResources().getString(R.string.downloadFinished));
                progressDialog.dismiss();
                break;
            case STATE_FAILED_UNLICENSED:
            case STATE_FAILED_FETCHING_URL:
            case STATE_FAILED_SDCARD_FULL:
            case STATE_FAILED_CANCELED:
            case STATE_FAILED:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(getResources().getString(R.string.downloadErrorTitle));
                alert.setMessage(getResources().getString(R.string.downloadFailed));
                alert.setNeutralButton(getResources().getString(R.string.downloadClose), null);
                alert.show();
                break;
        }
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

    private void setUpListLessons(int position) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, createLessonsNames());
        adapter.setDropDownViewResource(R.layout.spinner_layout);

        lessons.setAdapter(adapter);
        lessons.setSelection(position);

        lessons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);

                controller.getLessonFromDB(
                        LESSON + " = ?", item.toString().replaceAll(LESSON_RU, LESSON));

                if (!controller.getLesson().getPhrases().isEmpty()) {
                    if(position != globalPosition) {
                        globalPosition = position;
                        index = 0;
                        fillActivityContent(index);
                    } else {
                        fillActivityContent(index);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private List<String> createLessonsNames() {
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
                if(index > 0) {
                    index--;
                    fillActivityContent(index);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index != -1 && index < controller.getLesson().getPhrases().size() - 1) {
                    index++;
                    fillActivityContent(index);
                }
            }
        });

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        startActivityForResult(intent, 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        int ind = data.getIntExtra(INDEX, 0);

        if(ind == controller.getLesson().getPhrases().size() - 1) {
            controller.getLessonFromDB(LESSON + " = ?", getNextLessonName());
            int position = lessons.getSelectedItemPosition();
            if(position < LESSONS_SIZE - 1) {
                lessons.setSelection(++position);
                globalPosition = position;
                index = 0;
                fillActivityContent(index);
            }
        }
    }

    private String getNextLessonName() {
        int num = Integer.parseInt(controller.getLesson().getName().replaceAll("\\D", ""));

        if(num < LESSONS_SIZE) {
            num++;
        } else {
            Toast.makeText(this, getResources().getString(R.string.congratulation),
                    Toast.LENGTH_SHORT).show();
        }
        return LESSON + num;
    }

    @Override
    protected void onDestroy() {
        saveAppState();
        super.onDestroy();
    }

    private void saveAppState() {
        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(POSITION, lessons.getSelectedItemPosition());
        editor.commit();
    }

    private int loadSpinnerPosition() {
        preferences = getPreferences(MODE_PRIVATE);
        return preferences.getInt(POSITION, 0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(lessons != null) {
            outState.putInt(INDEX, index);
            outState.putInt(POSITION, lessons.getSelectedItemPosition());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        index = savedInstanceState.getInt(INDEX);
        globalPosition = savedInstanceState.getInt(POSITION);
    }

    private void showBanner() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
