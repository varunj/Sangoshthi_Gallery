package io.gihub.varunj.sangoshthi_gallery.sync;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gihub.varunj.sangoshthi_gallery.synclibrary.ErrorListener;
import io.gihub.varunj.sangoshthi_gallery.synclibrary.Listener;
import io.gihub.varunj.sangoshthi_gallery.synclibrary.MiniDownloader;
import io.gihub.varunj.sangoshthi_gallery.synclibrary.Task;
import io.gihub.varunj.sangoshthi_gallery.R;

public class SyncHome extends AppCompatActivity {

    private ListView taskListView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();

    private Listener listener = new Listener() {
        @Override
        public void onWait(Task task) {
            refreshData();
        }

        @Override
        public void onStart(Task task) {
            refreshData();
        }

        @Override
        public void onProgressUpdate(Task task) {
            refreshData();
        }

        @Override
        public void onStop(Task task) {
            refreshData();
        }

        @Override
        public void onFinish(Task task) {
            refreshData();
        }

        @Override
        public void onDelete(Task task) {
            refreshData();
        }
    };
    private ErrorListener errorListener = new ErrorListener() {

        @Override
        public void onError(Task task, Exception error) {
            showError(error.getMessage());
            refreshData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_home);
        MiniDownloader.getInstance().init(this);
        MiniDownloader.getInstance().setDebuggable(true);
        taskListView = (ListView) findViewById(R.id.layout_task);
        taskList.addAll(getTaskList());
        taskAdapter = new TaskAdapter(this, taskList);
        taskListView.setAdapter(taskAdapter);

        taskAdapter.setOnEventListener(new TaskAdapter.OnEventListener() {
            @Override
            public void onStart(Task task) {
                MiniDownloader.getInstance().start(task);
            }

            @Override
            public void onStop(Task task) {
                MiniDownloader.getInstance().stop(task);
            }

            @Override
            public void onDelete(Task task) {
                MiniDownloader.getInstance().delete(task);
            }
        });
    }

    private List<Task> getTaskList() {
        Set<Task> taskSet = new HashSet<>();
        taskSet.addAll(getUnfinishedTasks());
        taskSet.addAll(createNewTask());
        return new ArrayList<>(taskSet);
    }

    private List<Task> createNewTask() {
        List<Task> taskList = new ArrayList<>();

        // build what all available at server
        ArrayList<String> resourceServerlist = new ArrayList<String>();
        resourceServerlist.add("https://varunj.github.io/airgestar_img/demo_video_0_watermark.mp4");
        resourceServerlist.add("https://varunj.github.io/airgestar_img/demo_video_3_watermark.mp4");
        resourceServerlist.add("ftp://10.0.0.2:3721/a.mp4");
        resourceServerlist.add("ftp://192.168.164.169:3721/a.mp4");

        // build what all locally cached
        ArrayList<String> toRemove = new ArrayList<String>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download" + File.separator;
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            for (String toDownload : resourceServerlist) {
                if (toDownload.toLowerCase().contains(files[i].getName().toLowerCase())) {
                    toRemove.add(toDownload);
                }
            }
        }

        for (String eachRemove : toRemove) {
            resourceServerlist.remove(eachRemove);
        }

        // build what to download
        for (String eachResource : resourceServerlist) {
            String[] bits = eachResource.split("/");
            taskList.add(
                    new Task(eachResource,
                            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download" + File.separator + bits[bits.length-1],
                            listener, errorListener)
            );
        }


        return taskList;
    }

    private List<Task> getUnfinishedTasks() {
        List<Task> taskList = new ArrayList<>();
        taskList.addAll(MiniDownloader.getInstance().getStoppedTaskList());
        for (int i = 0; i < taskList.size(); i++) {
            taskList.get(i).setListener(listener);
            taskList.get(i).setErrorListener(errorListener);
        }
        return taskList;
    }

    private void refreshData() {
        taskAdapter.notifyDataSetChanged();
    }

    private void showError(final String msg) {
        Toast.makeText(SyncHome.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        MiniDownloader.getInstance().quit();
        super.onDestroy();
    }
}
