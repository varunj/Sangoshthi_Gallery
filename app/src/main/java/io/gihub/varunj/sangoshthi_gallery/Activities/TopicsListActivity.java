package io.gihub.varunj.sangoshthi_gallery.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.gihub.varunj.sangoshthi_gallery.Adapters.TopicsListAdapter;
import io.gihub.varunj.sangoshthi_gallery.R;

public class TopicsListActivity extends AppCompatActivity {

    private static String userName, userPhoneNum, userEmail, userLogPath;
    private Boolean userLoggedIn;
    private ArrayList<String> topicsList = new ArrayList<>();
    private static ArrayList<String> logsToDropbox = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics_list);

        // read cached user data
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = pref.getString("name", "defaultName");
        userPhoneNum = pref.getString("phoneNum", "0000000000");
        userLoggedIn = pref.getBoolean("isLoggedIn", false);
        userEmail = pref.getString("googleEmail", "defaultEmail");
        userLogPath = Environment.getExternalStorageDirectory().getAbsolutePath() +  getString(R.string.dropbox_logs_path) + userPhoneNum + ".txt/";
        Log.d("System.out", "xxx: user creden: " + userName + "   " + userPhoneNum + "   " + userEmail + "   " + userLoggedIn);

        // log
        TopicsListActivity.addToLog("app_open", "");

        // fetch list of topics authorised to access
        topicsList.clear();
        String pathAccessFile = Environment.getExternalStorageDirectory().getAbsolutePath() +  getString(R.string.dropbox_access_path);
        File directoryAccess = new File(pathAccessFile);
        try {
            BufferedReader br = new BufferedReader(new FileReader(directoryAccess));
            String line;
            String[] splitt;
            while ((line = br.readLine()) != null) {
                splitt = line.split(",");
                if (splitt[0].equals(userPhoneNum)) {
                    for (String eachString : splitt) {
                        topicsList.add(eachString);
                    }
                    topicsList.remove(userPhoneNum);
                }
            }
            br.close();
        }
        catch (IOException e) {
            Toast.makeText(TopicsListActivity.this, getString(R.string.toast_access_file), Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        TopicsListActivity.addToLog("app_close", "");
        transferToDropbox();
        logsToDropbox.clear();
        super.onBackPressed();
    }

    public static void addToLog(String action, String fileName) {
        if (fileName.length() > 0) {
            String[] splitt = fileName.split("/");
            fileName = splitt[splitt.length - 2] + "/" + splitt[splitt.length - 1];
        }
        logsToDropbox.add(new SimpleDateFormat("dd/MM/yyyy kk:mm:ss").format(new Date()) + "," + fileName + "," + action + "\n");
        Log.d("System.out", "xxx: logs length: " + logsToDropbox.size());
    }

    public static void transferToDropbox() {
        File file = new File(userLogPath);
        if (file.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                for (String x : logsToDropbox) {
                    writer.append(x);
                }
                writer.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
