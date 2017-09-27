package io.gihub.varunj.sangoshthi_gallery.MediaActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import io.gihub.varunj.sangoshthi_gallery.Activities.TopicsListActivity;
import io.gihub.varunj.sangoshthi_gallery.R;

/**
 * Created by Varun Jain on 27-Sep-17.
 */

public class ImageActivity extends AppCompatActivity {

    private static String pathh = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        try {
            pathh = getIntent().getStringExtra("imagePath");
            Bitmap bmImg = BitmapFactory.decodeFile(pathh);
            ImageView iv = (ImageView) findViewById(R.id.imageview_full);
            iv.setImageBitmap(bmImg);
            TopicsListActivity.addToLog("open", pathh);
        }
        catch (Exception e) {
            Toast.makeText(this, "Invalid Path", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        TopicsListActivity.addToLog("close", pathh);
        super.onBackPressed();
    }
}
