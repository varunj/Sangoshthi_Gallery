package io.gihub.varunj.sangoshthi_gallery.utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.IOException;

/**
 * This is a Singleton Pattern and this will only be initialized once
 * use @method getInstance() to get the instance for this class
 * This is thread safe Lazy initialization singleton class
 * Created by Varun on 16-03-2017.
 */

public class CommonUtils {

    // this will hold the instance of CommonUtil which can only be created once
    private static CommonUtils instance;

    /**
     * This will disallow any outside class to create instance of this class
     */
    private CommonUtils() {}

    /**
     * This class will the only access point to this class
     *
     * @return CommonUtils
     *                  one and only one instance of CommonUtils class will be returned
     */
    public static synchronized CommonUtils getInstance() {
        if (instance == null) {
            instance = new CommonUtils();
        }
        return instance;
    }

    /* --- Common Util member functions and member variables --- */

    // TAG for logging
    private static final String TAG = CommonUtils.class.getSimpleName();

    /* This will hold the global ffmpeg variable */
    public FFmpeg ffmpeg;

    /* This will hold all mediaRecorder functionality*/
    private MediaRecorder mediaRecorder;

    /* This will hold mediaPlayer for playing the recorded media */
    private MediaPlayer mediaPlayer;

    /**
     * This function will load ffmpeg for use
     */
    public void loadFFMPEGBinary(final Context context) {

        if(ffmpeg == null) {
            ffmpeg = FFmpeg.getInstance(context);
        }
        try {
            ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.v(TAG,"ffmpeg not supported");
                    Toast.makeText(context, "Ffmpeg not supported", Toast.LENGTH_SHORT).show();
                    ((Activity)context).finish();
                }

                @Override
                public void onSuccess() {
                    Log.v(TAG,"ffmpeg supported");
                }

                @Override
                public void onStart() {
                    Log.v(TAG,"ffmpeg started");
                }

                @Override
                public void onFinish() {
                    Log.v(TAG,"ffmpeg loading finished");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /* --- Media Related Functionalities --- */

    /**
     * This method will start the voice recording
     * @param voiceStoragePath
     *                      String to the path where the file will be stored
     */
    public void startAudioRecording(String voiceStoragePath) {
        if(mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(voiceStoragePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "Not able to prepare mediaRecorder: " + e);
            mediaRecorder.stop();
            mediaRecorder.release();
        }

    }

    /**
     * This function will stop voice recording and release the resources
     */
    public void stopAudioRecording() {
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Log.d(TAG, "mediaRecorder: " + mediaRecorder);
        } else {
            Log.d(TAG, "mediaRecorder already stopped");
        }
    }

    /**
     * This function plays the media passed to it
     *
     * @param voiceStoragePath
     *                      String for the path where audio is to be played
     */
    public void startAudioPlaying(String voiceStoragePath) {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            Log.d(TAG, "media player stopped: " + mediaPlayer);
        }

        try {
            mediaPlayer.setDataSource(voiceStoragePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopAudioPlaying() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(TAG, "media player stopped: " + mediaPlayer);
        }
    }

}
