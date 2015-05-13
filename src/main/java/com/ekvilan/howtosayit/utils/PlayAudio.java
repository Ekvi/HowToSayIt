package com.ekvilan.howtosayit.utils;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import com.ekvilan.howtosayit.listeners.AudioPlayerListener;


public class PlayAudio {
    private MediaPlayer mp;
    private AudioPlayerListener listener;

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

        int id = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
        int delta = stop - start;

        if(mp != null && mp.isPlaying()) {
            mp.stop();
            mp.reset();
            mp.release();
        }

        mp = MediaPlayer.create(context, id);
        mp.seekTo(start);
        mp.start();

        handler.postDelayed(stopPlayerTask, delta);
    }

    public void setOnEventListener(AudioPlayerListener listener) {
        this.listener = listener;
    }
}

