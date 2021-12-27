package com.loganchang.stressmeter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

public class SoundAlert {
    public static final int VOLUME_LEVEL = 60;

    //instance widgets
    public static Vibrator mVibrator;
    public static MediaPlayer mMediaPlayer;


    public static void startAlarm(Context context) {
        //set up media player's sound
        mMediaPlayer = MediaPlayer.create(context, R.raw.gentle_sms);
        mMediaPlayer.setVolume(VOLUME_LEVEL, VOLUME_LEVEL);
        mMediaPlayer.setLooping(true);

        //give media player a vibration
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] echoPattern = {0, 100, 1000};
        mVibrator.vibrate(echoPattern, 0);

        mMediaPlayer.start();
    }

    public static void endAlarm() {

        //kill the vibration
        if (mVibrator != null && mVibrator.hasVibrator()) {
            mVibrator.cancel();
        }

        //kill the sound
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }
}
