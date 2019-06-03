package com.example.elekesattila.sapification_3.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.elekesattila.sapification_3.Classes.User;
import com.example.elekesattila.sapification_3.R;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<User> {
    private static final String TAG = "SapificationUserAdapt";

    private final Activity context;
    private final ArrayList<User> mUsers;

    public UserListAdapter(Activity context, ArrayList<User> users){
        super(context, R.layout.list_user, users);

        this.context = context;
        this.mUsers = users;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_user, null,true);

        TextView name = (TextView) rowView.findViewById(R.id.text_view_main);
        TextView email = (TextView) rowView.findViewById(R.id.text_view_sub);
        TextView permission = (TextView) rowView.findViewById(R.id.text_view_additional);

        name.setText(mUsers.get(position).getName());
        Log.d(TAG, "Name: " + name);
        email.setText(mUsers.get(position).getEmail());
        switch (mUsers.get(position).getPrivilege()){
            case 0:
                permission.setText(R.string.user);
                break;
            case 1:
                permission.setText(R.string.super_user);
                break;
            case 2:
                permission.setText(R.string.admin);
                break;
        }
        return rowView;
    }
}

