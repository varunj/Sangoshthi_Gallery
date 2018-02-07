package io.gihub.varunj.sangoshthi_gallery.MediaActivities;

import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

import io.gihub.varunj.sangoshthi_gallery.Activities.TopicsListActivity;
import io.gihub.varunj.sangoshthi_gallery.R;

/**
 * Created by Varun Jain on 27-Sep-17.
 */

public class VideoActivity extends AppCompatActivity {

    private static String pathh = "";
    private SurfaceView surfaceView;
    private ExoPlayer exoPlayer;
    private LinearLayout mediaController;
    private SeekBar seekPlayerProgress;
    private TextView txtCurrentTime;
    private TextView txtEndTime;
    private ImageButton btnRew;
    private ImageButton btnPlay;
    private ImageButton btnFwd;
    private boolean bIsPlaying=false;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Handler handler;
    private boolean bControlsActive=true;
    private int RENDERER_COUNT = 300000;
    private int minBufferMs =    250000;
    private final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private final int BUFFER_SEGMENT_COUNT = 256;
    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:40.0) Gecko/20100101 Firefox/40.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiovideo);

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);

        // Video Player Stuff
        setContentView(R.layout.activity_audiovideo);
        surfaceView = (SurfaceView) findViewById(R.id.sv_player);
        mediaController = (LinearLayout) findViewById(R.id.lin_media_controller);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pathh = getIntent().getStringExtra("videoPath");
        initPlayer(0, pathh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        TopicsListActivity.addToLog("close", pathh);
        exoPlayer.stop();
        finish();
    }

    private void initMediaControls() {
        initSurfaceView();
        initSeekBar();
        initTxtTime();
        initBtnRew();
        initBtnPlay();
        initBtnFwd();
    }

    private void initSurfaceView() {
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMediaControls();
            }
        });
    }

    private void initSeekBar() {
        seekPlayerProgress = (SeekBar) findViewById(R.id.mediacontroller_progress);
        seekPlayerProgress.requestFocus();
        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to the progress bar's position.
                    return;
                }
                exoPlayer.seekTo(progress*1000);
                TopicsListActivity.addToLog("seek:" + exoPlayer.getCurrentPosition(), pathh);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
    }

    private void initTxtTime() {
        txtCurrentTime = (TextView) findViewById(R.id.time_current);
        txtEndTime = (TextView) findViewById(R.id.player_end_time);
    }

    private void initBtnRew() {
        btnRew = (ImageButton) findViewById(R.id.rew);
        btnRew.requestFocus();
        btnRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition()-3000);
                TopicsListActivity.addToLog("seek:" + exoPlayer.getCurrentPosition(), pathh);
            }
        });
    }

    private void initBtnPlay() {
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bIsPlaying){
                    exoPlayer.setPlayWhenReady(false);
                    bIsPlaying=false;
                    btnPlay.setImageResource(android.R.drawable.ic_media_play);
                    TopicsListActivity.addToLog("pause:" + exoPlayer.getCurrentPosition(), pathh);
                }
                else {
                    exoPlayer.setPlayWhenReady(true);
                    bIsPlaying=true;
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                    setProgress();
                    TopicsListActivity.addToLog("play:" + exoPlayer.getCurrentPosition(), pathh);
                }
            }
        });
    }

    private void initBtnFwd() {
        btnFwd = (ImageButton) findViewById(R.id.ffwd);
        btnFwd.requestFocus();
        btnFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition()+3000);
                TopicsListActivity.addToLog("seek:" + exoPlayer.getCurrentPosition(), pathh);
            }
        });
    }

    private String stringForTime(int timeMs) {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        }
        else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
        seekPlayerProgress.setProgress(0);
        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
        handler = new Handler();
        //Make sure Seekbar is updated only on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && bIsPlaying ) {
                    seekPlayerProgress.setMax(0);
                    seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void toggleMediaControls() {
        if(bControlsActive){
            hideMediaController();
            bControlsActive=false;
        }
        else {
            showController();
            bControlsActive=true;
            setProgress();
        }
    }

    private void showController() {
        mediaController.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideMediaController() {
        mediaController.setVisibility(View.GONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initPlayer(int position, String path) {
        Allocator allocator = new DefaultAllocator(minBufferMs);
        DataSource dataSource = new DefaultUriDataSource(this, null, userAgent);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                Uri.fromFile(
                        new File(path)
                ),
                dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(this, sampleSource, MediaCodecSelector.DEFAULT,
                MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);

        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);
        exoPlayer = ExoPlayer.Factory.newInstance(RENDERER_COUNT);
        exoPlayer.prepare(videoRenderer, audioRenderer);
        exoPlayer.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surfaceView.getHolder().getSurface());
        exoPlayer.seekTo(position);
        initMediaControls();
    }
}
