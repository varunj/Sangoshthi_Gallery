package io.gihub.varunj.sangoshthi_gallery.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import io.gihub.varunj.sangoshthi_gallery.R;
import io.gihub.varunj.sangoshthi_gallery.synclibrary.MiniDownloader;

public class GalleryHome extends AppCompatActivity {

    GalleryAdapter mAdapter;
    RecyclerView mRecyclerView;

    ArrayList<ImageModel> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_home);

        // build all locally cached media
        ArrayList<String> resourceList = new ArrayList<String>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download" + File.separator;
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            resourceList.add(path+files[i].getName());
        }

        int c = 0;
        for (String eachResource : resourceList) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + c);
            imageModel.setUrl(eachResource);
            data.add(imageModel);
            c = c + 1;
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);


        mAdapter = new GalleryAdapter(GalleryHome.this, data);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(GalleryHome.this, DetailActivity.class);
                        intent.putParcelableArrayListExtra("data", data);
                        intent.putExtra("pos", position);
                        startActivity(intent);

                    }
                }));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
