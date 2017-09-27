package io.gihub.varunj.sangoshthi_gallery.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        // get topic name
        Intent intent = getIntent();
        topicName = intent.getStringExtra("topicName");
        Log.d("System.out", "xxx: selec topic: " + topicName);

        // fetch list of media related to topic
        resourceList.clear();
        String pathMedia = Environment.getExternalStorageDirectory().getAbsolutePath() +  getString(R.string.dropbox_path) + topicName + "/";
        File directoryMedia = new File(pathMedia);
        File[] files = directoryMedia.listFiles();
        for (int i = 0; i < files.length; i++) {
            resourceList.add(pathMedia+files[i].getName());
        }
        Collections.sort(resourceList, String.CASE_INSENSITIVE_ORDER);
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
