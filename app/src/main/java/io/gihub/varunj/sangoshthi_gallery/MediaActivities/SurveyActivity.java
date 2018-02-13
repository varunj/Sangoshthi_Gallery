package io.gihub.varunj.sangoshthi_gallery.MediaActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.gihub.varunj.sangoshthi_gallery.Activities.CMainActivity;
import io.gihub.varunj.sangoshthi_gallery.R;

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
        CMainActivity.logsToDropbox.add(new SimpleDateFormat("dd/MM/yyyy kk:mm:ss").format(new Date()) + "," + pathh + "," + ans + "\n");
        super.onBackPressed();
    }
}
