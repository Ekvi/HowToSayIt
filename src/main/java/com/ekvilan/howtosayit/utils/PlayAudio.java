package com.ekvilan.howtosayit.utils;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.ekvilan.howtosayit.listeners.AudioPlayerListener;
import com.google.android.vending.expansion.downloader.Constants;

import java.io.IOException;


public class PlayAudio {
    public static PlayAudio instance;
    private MediaPlayer mp;
    private AudioPlayerListener listener;

    private PlayAudio(){}

    public static PlayAudio getInstance() {
        if(instance == null) {
            instance = new PlayAudio();
        }
        return instance;
    }

    private Runnable stopPlayerTask = new Runnable() {
        @Override
        public void run() {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;

                if(listener != null) {
                    listener.fireStopAudio();
                }
            }
        }
    };

    public void play(Context context, final int start, int stop, String fileName) {
        Handler handler = new Handler();

        if(mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }

        try {
            ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context, 2, 0);
            AssetFileDescriptor afd = expansionFile.getAssetFileDescriptor(fileName + ".mp3");

            mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
        } catch (IOException e) {
            Log.w(Constants.TAG, "Failed to find expansion file", e);
        }

        mp.prepareAsync();
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.seekTo(start);
                mp.start();
            }
        });

        int delta = stop - start;
        handler.postDelayed(stopPlayerTask, delta);
    }

    public void setOnEventListener(AudioPlayerListener listener) {
        this.listener = listener;
    }
}


