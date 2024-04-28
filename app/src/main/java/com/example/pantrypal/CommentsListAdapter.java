package com.example.pantrypal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CommentsListAdapter extends ArrayAdapter<Comment> {
    private static final String TAG = "CommentsListAdapter";
    private Context mContext;
    private int mResource;

    private ArrayList<Comment> mCommentsList;

    public CommentsListAdapter(Context context, int resource, ArrayList<Comment> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mCommentsList = objects;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String id = getItem(position).getId();
        String title = getItem(position).getTitle();
        String message = getItem(position).getMessage();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView titleTV = convertView.findViewById(R.id.commentTitle);
        TextView messageTV = convertView.findViewById(R.id.commentMessage);

        titleTV.setText(title);
        messageTV.setText(message);

        // Set the id of the comment
        Comment comment = getItem(position);
        comment.setId(id);

        return convertView;
    }
}