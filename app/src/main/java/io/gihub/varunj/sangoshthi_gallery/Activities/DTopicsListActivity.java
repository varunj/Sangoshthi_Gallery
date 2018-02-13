package io.gihub.varunj.sangoshthi_gallery.Activities;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import io.gihub.varunj.sangoshthi_gallery.Adapters.TopicsListAdapter;
import io.gihub.varunj.sangoshthi_gallery.R;

public class DTopicsListActivity extends AppCompatActivity {

    private ArrayList<String> topicsList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics_list);

        // fetch list of topics authorised to access
        topicsList.clear();
        String pathAccessFile = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_access_path);
        File directoryAccess = new File(pathAccessFile);
        try {
            BufferedReader br = new BufferedReader(new FileReader(directoryAccess));
            String line;
            String[] splitt;
            while ((line = br.readLine()) != null) {
                splitt = line.split(",");
                if (splitt[0].equals(CMainActivity.userPhoneNum)) {
                    for (String eachString : splitt) {
                        topicsList.add(eachString);
                    }
                    topicsList.remove(CMainActivity.userPhoneNum);
                }
            }
            br.close();
        } catch (IOException e) {
            Toast.makeText(DTopicsListActivity.this, getString(R.string.toast_access_file), Toast.LENGTH_LONG).show();
        }
        Log.d("System.out", "xxx: auth topics: " + topicsList);

        // create recycler view for listing topics
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_topics);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TopicsListAdapter(this, topicsList);
        mRecyclerView.setAdapter(mAdapter);

    }

}
