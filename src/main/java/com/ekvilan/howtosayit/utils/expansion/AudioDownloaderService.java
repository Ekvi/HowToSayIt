package com.ekvilan.howtosayit.utils.expansion;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;


public class AudioDownloaderService  extends DownloaderService {
    public static final String BASE64_PUBLIC_KEY = "MyKey";
    public static final byte[] SALT = new byte[] { 1, 43, -11, -2, 55, 99,
            -101, -13, 42, 3, -9, -5, 10, 5, -106, -107, -33, 45, -1, 84
    };

    @Override
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT() {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName() {
        return DownloaderServiceBroadcastReceiver.class.getName();
    }
}
