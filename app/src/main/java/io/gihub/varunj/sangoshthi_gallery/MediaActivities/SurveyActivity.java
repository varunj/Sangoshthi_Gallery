package io.gihub.varunj.sangoshthi_gallery.MediaActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import io.gihub.varunj.sangoshthi_gallery.Activities.TopicsListActivity;
import io.gihub.varunj.sangoshthi_gallery.Adapters.MediaListAdapter;
import io.gihub.varunj.sangoshthi_gallery.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by Varun Jain on 27-Sep-17.
 */

public class SurveyActivity extends AppCompatActivity {

    private static String pathh = "";
    DiscreteSeekBar seeekBar1;
    DiscreteSeekBar seeekBar2;
    DiscreteSeekBar seeekBar3;
    DiscreteSeekBar seeekBar4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        pathh = getIntent().getStringExtra("topicName");

        seeekBar1 = (DiscreteSeekBar) findViewById(R.id.seekBar1);
        seeekBar2 = (DiscreteSeekBar) findViewById(R.id.seekBar2);
        seeekBar3 = (DiscreteSeekBar) findViewById(R.id.seekBar3);
         seeekBar4 = (DiscreteSeekBar) findViewById(R.id.seekBar4);
    }

    @Override
    public void onBackPressed() {
        String ans = seeekBar1.getProgress() + ":" + seeekBar2.getProgress() + ":" + seeekBar3.getProgress() + ":" + seeekBar4.getProgress();
        TopicsListActivity.logsToDropbox.add(new SimpleDateFormat("dd/MM/yyyy kk:mm:ss").format(new Date()) + "," + pathh + "," + ans + "\n");
        super.onBackPressed();
    }
}
