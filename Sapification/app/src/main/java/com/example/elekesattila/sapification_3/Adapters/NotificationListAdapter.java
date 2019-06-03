package com.example.elekesattila.sapification_3.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.elekesattila.sapification_3.Classes.Notification;
import com.example.elekesattila.sapification_3.R;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationListAdapter extends ArrayAdapter<Notification> {
    private static final String TAG = "SapificationNLA";

    private final Activity context;
    private final ArrayList<Notification> mNotifications;

    public NotificationListAdapter(Activity context, ArrayList<Notification> notifications){
        super(context, R.layout.list_user, notifications);

        this.context = context;
        this.mNotifications = notifications;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_user, null,true);

        TextView title = (TextView) rowView.findViewById(R.id.text_view_main);
        TextView message = (TextView) rowView.findViewById(R.id.text_view_sub);
        TextView date = (TextView) rowView.findViewById(R.id.text_view_additional);

        try{
            title.setText(mNotifications.get(position).getTitle());
            message.setText(mNotifications.get(position).getBody());
            long notifDate = mNotifications.get(position).getDate().getTime();
            long today = Calendar.getInstance().getTime().getTime();
            long dateDiffInMillis = today - notifDate;
            long dateDiffInDays = dateDiffInMillis/(1000*60*60*24);
            if (dateDiffInDays == 0){
                date.setText(R.string.today);
            }
            else{
                String text = "" + dateDiffInDays + " days ago";
                date.setText(text);
            }

            if (mNotifications.get(position).getSeen().equals("1")){
                rowView.setBackgroundColor(Color.GRAY);
            }
        }
        catch(Exception e){
            Log.d(TAG, e.getMessage());
        }

        return rowView;
    }
}
