package com.example.pantrypal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CommentsListAdapter extends ArrayAdapter<Comment> {
    private static final String TAG = "CommentsListAdapter";
    private Context mContext;
    private int mResource;

    public CommentsListAdapter(Context context, int resource, ArrayList<Comment> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).getTitle();
        String message = getItem(position).getMessage();

        Comment comment = new Comment(title, message);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView titleTV = (TextView) convertView.findViewById(R.id.commentTitle);
        TextView messageTV = (TextView) convertView.findViewById(R.id.commentMessage);

        titleTV.setText(title);
        messageTV.setText(message);

        return convertView;
    }
}
