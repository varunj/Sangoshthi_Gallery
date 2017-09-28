package io.gihub.varunj.sangoshthi_gallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.gihub.varunj.sangoshthi_gallery.Activities.MediaListActivity;
import io.gihub.varunj.sangoshthi_gallery.R;

/**
 * Created by Varun Jain on 27-Sep-17.
 */

public class TopicsListAdapter extends RecyclerView.Adapter<TopicsListAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    private final Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_topics;
        public CardView cv_topics;
        ImageView iv_topics;
        public ViewHolder(View itemView) {
            super(itemView);
            cv_topics = (CardView) itemView.findViewById(R.id.cv_topics);
            tv_topics = (TextView) itemView.findViewById(R.id.tv_topics);
            iv_topics = (ImageView) itemView.findViewById(R.id.iv_topics);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TopicsListAdapter(Context context, ArrayList<String> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_topics_list_row, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // set topic name
        holder.tv_topics.setText(mDataset.get(position));

        // set topic icon
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + context.getResources().getString(R.string.dropbox_icons_path) + mDataset.get(position) + "_icon.png/");
        if (file.exists()) {
            holder.iv_topics.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }

        holder.cv_topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MediaListActivity.class);
                intent.putExtra("topicName", mDataset.get(position));
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
