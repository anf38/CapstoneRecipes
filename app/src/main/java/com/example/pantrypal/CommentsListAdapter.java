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

        // Comment comment = new Comment(id, title, message); // Remove this line

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView titleTV = convertView.findViewById(R.id.commentTitle);
        TextView messageTV = convertView.findViewById(R.id.commentMessage);
        TextView reportTV = convertView.findViewById(R.id.report);

        titleTV.setText(title);
        messageTV.setText(message);

        reportTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportDialog(title, message);
            }
        });

        return convertView;
    }


    private void showReportDialog(final String title, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Report Comment");
        builder.setMessage("Please select the reason for reporting:");

        // Inflate custom view for radio buttons
        View view = LayoutInflater.from(mContext).inflate(R.layout.report_dialog_layout, null);
        builder.setView(view);

        // Find radio buttons in the custom layout
        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = view.findViewById(selectedId);
                if (radioButton != null) {
                    String reason = radioButton.getText().toString();
                    // Submit report action here with selected reason
                    Toast.makeText(mContext, "Report submitted for: " + title + "\nReason: " + reason, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Please select a reason for reporting.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}