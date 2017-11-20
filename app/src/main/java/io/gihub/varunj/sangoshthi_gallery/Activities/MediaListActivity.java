package io.gihub.varunj.sangoshthi_gallery.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import io.gihub.varunj.sangoshthi_gallery.Adapters.MediaListAdapter;
import io.gihub.varunj.sangoshthi_gallery.R;

/**
 * Created by Varun Jain on 27-Sep-17.
 */

public class MediaListActivity extends AppCompatActivity {

    private String topicName;
    private ArrayList<String> resourceList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView topics_actvity_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        callFromResumeandCreate();

    }

    @Override
    public void onResume(){
        super.onResume();
        callFromResumeandCreate();
    }

    public int countFiles(Context mcoContext, String folder_name){
        try {
            File file =  new File(mcoContext.getFilesDir(), folder_name);
            Log.d("System.out", "xxx: nos of files in watched_file: " + file.list().length);
            return file.list().length;
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public void callFromResumeandCreate() {
        // get topic name
        Intent intent = getIntent();
        topicName = intent.getStringExtra("topicName");
        Log.d("System.out", "xxx: selec topic: " + topicName);

        // set title
        topics_actvity_title = (TextView) findViewById(R.id.topics_actvity_title);
        topics_actvity_title.setText(topicName.toUpperCase());

        // fetch list of media related to topic
        resourceList.clear();
        String pathMedia = Environment.getExternalStorageDirectory().getAbsolutePath() +  getString(R.string.dropbox_path) + topicName + "/";
        File directoryMedia = new File(pathMedia);
        File[] files = directoryMedia.listFiles();
        for (int i = 0; i < files.length; i++) {
            resourceList.add(pathMedia+files[i].getName());
        }
        Collections.sort(resourceList, String.CASE_INSENSITIVE_ORDER);

        // fetch if eligible for survey
        ArrayList<String> deleteCandidates = new ArrayList<>();
        if (countFiles(MediaListActivity.this, topicName) != files.length-1) {
            for (String x : resourceList) {
                if (x.contains("txt")) {
                    deleteCandidates.add(x);
                }
            }
            for (String x : deleteCandidates) {
                resourceList.remove(x);
            }
        }


        Log.d("System.out", "xxx: topic files: " + resourceList.size() + "   " + resourceList);

        // create recycler view for listing topics
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_media);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MediaListAdapter(this, resourceList);
        mRecyclerView.setAdapter(mAdapter);
    }

}
