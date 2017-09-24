package io.gihub.varunj.sangoshthi_gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gihub.varunj.sangoshthi_gallery.gallery.GalleryHome;
import io.gihub.varunj.sangoshthi_gallery.sync.SyncHome;
import io.gihub.varunj.sangoshthi_gallery.synclibrary.ErrorListener;
import io.gihub.varunj.sangoshthi_gallery.synclibrary.Listener;
import io.gihub.varunj.sangoshthi_gallery.synclibrary.MiniDownloader;
import io.gihub.varunj.sangoshthi_gallery.synclibrary.Task;

public class MainActivity extends AppCompatActivity {

    private ImageButton home_sync;
    private ImageButton home_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home_sync = (ImageButton) findViewById(R.id.home_sync);
        home_gallery = (ImageButton) findViewById(R.id.home_gallery);

        home_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SyncHome.class);
                setResult(Activity.RESULT_OK, intent);
                startActivityForResult(intent, 1);
            }
        });
        home_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryHome.class);
                setResult(Activity.RESULT_OK, intent);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
