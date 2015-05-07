package com.howtosayit.howtosayit2.utils;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;


public class PlayAudio {
    private MediaPlayer mp;

    private Runnable stopPlayerTask = new Runnable() {
        @Override
        public void run() {
            mp.stop();
            mp.release();
        }
    };

    public void play(Context context, final int start, int stop, String fileName) {
        int id = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());

        mp = MediaPlayer.create(context, id);
        mp.seekTo(start);
        mp.start();

        int delta = stop - start;
        Handler handler = new Handler();
        handler.postDelayed(stopPlayerTask, delta);
    }
}
