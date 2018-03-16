package io.gihub.varunj.sangoshthi_gallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import io.gihub.varunj.sangoshthi_gallery.MediaActivities.ImageActivity;
import io.gihub.varunj.sangoshthi_gallery.MediaActivities.SurveyActivity;
import io.gihub.varunj.sangoshthi_gallery.MediaActivities.VideoActivity;
import io.gihub.varunj.sangoshthi_gallery.R;

/**
 * Created by Varun Jain on 27-Sep-17.
 */

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    private final Context context;
    public static String audioPath = "";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_media;
        public CardView cv_media;
        ImageView iv_media;
        public ViewHolder(View itemView) {
            super(itemView);
            cv_media = (CardView) itemView.findViewById(R.id.cv_media);
            tv_media = (TextView) itemView.findViewById(R.id.tv_media);
            iv_media = (ImageView) itemView.findViewById(R.id.iv_media);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MediaListAdapter(Context context, ArrayList<String> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_media_list_row, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // set text
        String[] splitt = mDataset.get(position).split("/");
        final String temp = splitt[splitt.length-1];
        final String temp_topic = splitt[splitt.length-2];
//        holder.tv_media.setText(temp.substring(0, temp.length()-4));
        holder.tv_media.setText(temp.substring(3, temp.length()-9));

        // set media icon
//        if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("mp4"))
//            holder.iv_media.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_video));
//        if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("mp3"))
//            holder.iv_media.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_music));
//        if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("png"))
//            holder.iv_media.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_image));

        if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("mp4"))
            holder.iv_media.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_video));
        if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("mp3")) {
            if (mDataset.get(position).substring(mDataset.get(position).length()-8).contains("asha"))
                holder.iv_media.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_asha));
            else
                holder.iv_media.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_doctor));
        }
        if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("png"))
            holder.iv_media.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_image));



        // set click listener
        holder.cv_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("mp4")) {

                    // store list of read things
                    wrtieFileOnInternalStorage(context, temp_topic, temp);

                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("videoPath", mDataset.get(position));
                    context.startActivity(intent);
                }

                if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("mp3")) {

                    // store list of read things
                    wrtieFileOnInternalStorage(context, temp_topic, temp);

                    // old video type audio implementation
//                    Intent intent = new Intent(context, VideoActivity.class);
//                    intent.putExtra("videoPath", mDataset.get(position));
//                    context.startActivity(intent);

                    // new audio type audio implementation
                    Bundle bundle = new Bundle();
                    bundle.putString("audioPath", mDataset.get(position));
                    AudioPlaybackFragment playbackFragment = new AudioPlaybackFragment();
                    playbackFragment.setArguments(bundle);
                    FragmentTransaction transaction = ((FragmentActivity) context)
                            .getSupportFragmentManager()
                            .beginTransaction();
                    playbackFragment.show(transaction, "dialog_playback");
                }

                if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("png")) {

                    // store list of read things
                    wrtieFileOnInternalStorage(context, temp_topic, temp);

                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putExtra("imagePath", mDataset.get(position));
                    context.startActivity(intent);
                }

                if (mDataset.get(position).substring(mDataset.get(position).length()-3).equals("txt")) {

                    Intent intent = new Intent(context, SurveyActivity.class);
                    intent.putExtra("topicName", temp_topic);
                    context.startActivity(intent);
                }

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void wrtieFileOnInternalStorage(Context mcoContext, String folder_name, String file_name){
        File file = new File(mcoContext.getFilesDir(), folder_name);
        if(!file.exists()){
            file.mkdir();
        }
        File mypath= new File(file, file_name);

        try{
            FileOutputStream fileOutputStream = new FileOutputStream(mypath,true);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append("opened");
            writer.close();
            fileOutputStream.close();
            Log.d("System.out", "xxx: added to watched_file: " + folder_name + ", " + file_name);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
