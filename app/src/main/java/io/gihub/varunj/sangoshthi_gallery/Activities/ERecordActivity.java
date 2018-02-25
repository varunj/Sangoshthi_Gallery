package io.gihub.varunj.sangoshthi_gallery.Activities;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.io.IOException;

import io.gihub.varunj.sangoshthi_gallery.R;
import io.gihub.varunj.sangoshthi_gallery.utils.CommonUtils;

public class ERecordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ERecordActivity.class.getSimpleName();
    private Button btn_record_record, btn_record_play, btn_record_submit, btn_record_delete;
    private TextView text_timer;
    private static String userRecordingPath, userRecordingPathTemp, userRecordingPathFinal1, userRecordingPathFinal2, userRecordingPathFinal3, lenRecording = "0";
    private long mStartTime;
    private MediaRecorder mediaRecorder;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        userRecordingPath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + CMainActivity.userPhoneNum + ".mp4/";
        userRecordingPathTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + CMainActivity.userPhoneNum + "_temp.mp4/";
        userRecordingPathFinal1 = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + CMainActivity.userPhoneNum + "_final1.ts";
        userRecordingPathFinal2 = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + CMainActivity.userPhoneNum + "_final2.ts";
        userRecordingPathFinal3 = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + CMainActivity.userPhoneNum + "_final3.mp4";

        btn_record_record = (Button) findViewById(R.id.btn_record_record);
        btn_record_play = (Button) findViewById(R.id.btn_record_play);
        btn_record_submit = (Button) findViewById(R.id.btn_record_submit);
        btn_record_delete = (Button) findViewById(R.id.btn_record_delete);
        text_timer = (TextView) findViewById(R.id.text_timer);

        btn_record_record.setOnClickListener(this);
        btn_record_delete.setOnClickListener(this);
        btn_record_play.setOnClickListener(this);
        btn_record_submit.setOnClickListener(this);

        btn_record_delete.setVisibility(View.GONE);
        btn_record_play.setVisibility(View.GONE);
        btn_record_submit.setVisibility(View.GONE);

        mHandler = new Handler();

    }

    private Runnable timerTask = new Runnable() {
        public void run() {
            text_timer.setText(getFormattedTime());
            mHandler.postDelayed(timerTask, 500); // delay 1/2 second
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_record_record:
                if(btn_record_record.getText().equals(getResources().getString(R.string.record_startrecording))) {
                    startAudioRecording(userRecordingPathTemp);
                    mStartTime = System.currentTimeMillis();
                    btn_record_record.setText(R.string.record_endrecording);
                    btn_record_record.setBackgroundColor(ContextCompat.getColor(this, R.color.play_button));

                    // start timer
                    try {
                        mHandler.removeCallbacks(timerTask);
                        mHandler.postDelayed(timerTask, 500); // delay 1/2 second
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    stopAudioRecording();
                    lenRecording = getFormattedTime();
                    btn_record_record.setVisibility(View.GONE);
                    btn_record_delete.setVisibility(View.VISIBLE);
                    btn_record_play.setVisibility(View.VISIBLE);
                    btn_record_submit.setVisibility(View.VISIBLE);

                    // stop timer
                    mHandler.removeCallbacks(timerTask);
                }
                break;

            case R.id.btn_record_delete:
                try {
                    stopAudioRecordingAndDelete(userRecordingPathTemp);
                    Toast.makeText(this, "Recording canceled! " + lenRecording, Toast.LENGTH_SHORT).show();
                    Intent intentTrimVideos = new Intent(this, ERecordActivity.class);
                    startActivity(intentTrimVideos);
                    ERecordActivity.this.finish();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.btn_record_play:
                try {
                    if (btn_record_play.getText().equals(getResources().getString(R.string.record_play_recording))) {

                        File audioFile = new File(userRecordingPathTemp);
                        if (audioFile.exists() && audioFile.isFile()) {
                            System.out.println("xxx: " + userRecordingPathTemp);
                            CommonUtils.getInstance().startAudioPlaying(userRecordingPathTemp);
                            btn_record_play.setText(R.string.record_pause_recording);
                        } else {
                            Toast.makeText(this, "File Not Found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        CommonUtils.getInstance().stopAudioPlaying();
                        btn_record_play.setText(R.string.record_play_recording);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "File Not Found!", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.btn_record_submit:
                try {
                    Toast.makeText(this, "Recording submitted! " + lenRecording, Toast.LENGTH_SHORT).show();
                    joinTwoRecordings(userRecordingPathTemp, userRecordingPath);
                    Toast.makeText(this, "Stitched!", Toast.LENGTH_SHORT).show();
                    CMainActivity.addToLog("record:" + lenRecording, "");

                    ERecordActivity.this.finish();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private String getFormattedTime() {
        long  diff = System.currentTimeMillis() - mStartTime;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        diff = diff % daysInMilli;
        diff = diff % hoursInMilli;
        long elapsedMinutes = diff / minutesInMilli;
        diff = diff % minutesInMilli;
        long elapsedSeconds = diff / secondsInMilli;

        return elapsedMinutes + ":" + elapsedSeconds;
    }

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
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
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
            Log.d(TAG, "xxx: recorder stopped");
        }
    }

    /**
     * This function will stop voice recording and release the resources and delete the temp file made.
     */
    public void stopAudioRecordingAndDelete(String voiceStoragePath) {
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            File file = new File(voiceStoragePath);
            file.delete();
            Log.d(TAG, "xxx: recorder stopped and file deleted" + mediaRecorder);
        }
    }

    /**
     * This function will concat two mp3
     */
    public void joinTwoRecordings(String recTempPath, String recMainPath) {
        CommonUtils.getInstance().loadFFMPEGBinary(this);   // xxx:here or in create?

        recMainPath = recMainPath.substring(0,recMainPath.length()-1);
        recTempPath = recTempPath.substring(0,recTempPath.length()-1);

        String[] join_command = {"-y", "-i", recMainPath, "-c", "copy", "-bsf:v", "h264_mp4toannexb", "-f", "mpegts", userRecordingPathFinal1};
        execFFmpegBinary(join_command);

        String[] join_command2 = {"-y", "-i", recTempPath, "-c", "copy", "-bsf:v", "h264_mp4toannexb", "-f", "mpegts", userRecordingPathFinal2};
        execFFmpegBinary(join_command2);

        String concatt = "concat:" + userRecordingPathFinal1 + "|" + userRecordingPathFinal2;
        String[] join_command3 = {"-f", "mpegts", "-i", concatt, "-c", "copy", "-bsf:a", "aac_adtstoasc", userRecordingPathFinal3};
        execFFmpegBinary(join_command3);
    }

    /**
     * This will execute the ffmpeg command
     *
     * @param command
     */
    private void execFFmpegBinary(final String[] command) {
        if(CommonUtils.getInstance().ffmpeg != null) {
            try {

                CommonUtils.getInstance().ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                    @Override
                    public void onFailure(String s) {
                        Log.d(TAG, "xxxFAILED with output : " + s);
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "xxxSUCCESS with output : " + s);
                    }

                    @Override
                    public void onProgress(String s) {
                        Log.d(TAG, "xxxprogress : " + s);
                    }

                    @Override
                    public void onStart() {
                        Log.d(TAG, "xxxStarted command");
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "xxxFinished command: ");
//                        File file1 = new File(userRecordingPathFinal1);
//                        if(file1.exists()) {
//                            Log.d(TAG, "xxxdeleting intermediate files1: "+ file1.delete());
//                        }
//                        File file2 = new File(userRecordingPathFinal2);
//                        if(file2.exists()) {
//                            Log.d(TAG, "xxxdeleting intermediate files2: "+ file2.delete());
//                        }
//                        File file3 = new File(userRecordingPathTemp);
//                        if(file3.exists()) {
//                            Log.d(TAG, "xxxdeleting intermediate files3: "+ file3.delete());
//                        }
                        File file4 = new File(userRecordingPath);
                        if(file4.exists()) {
                            Log.d(TAG, "xxxdeleting intermediate files4: "+ file4.delete());
                        }
                        File file5 = new File(userRecordingPathFinal3);
                        File file6 = new File(userRecordingPath);
                        if(file5.exists()) {
                            Log.d(TAG, "xxxdeleting intermediate files5: "+ file5.renameTo(file6));
                        }
                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
                Toast.makeText(this, "xxxFFMPEG Already running", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "xxxFFMPEG not loaded", Toast.LENGTH_SHORT).show();
        }
    }
}
