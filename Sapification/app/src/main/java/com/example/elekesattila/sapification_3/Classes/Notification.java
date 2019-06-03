package com.example.elekesattila.sapification_3.Classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification implements Parcelable {
    private static final String TAG = "SapificationNotif";
    private String mNotificationId;
    private String mNotificationTitle;
    private String mNotificationBody;
    private Date mNotificationDate;
    private String mSeen;

    public Notification(String id, String title, String body, String date, String seen) {
        this.mNotificationId = id;
        this.mNotificationTitle = title;
        this.mNotificationBody = body;
        DateFormat format = new SimpleDateFormat("yyyy/M/d", Locale.ENGLISH);
        try {
            String tempDate = date.substring(0, 10);
            this.mNotificationDate = format.parse(tempDate);
        } catch (Exception e) {
            Log.d(TAG, "Date error: " + e.getMessage());
        }
        this.mSeen = seen;
    }

    public String getId() {
        return mNotificationId;
    }

    public String getTitle() {
        return mNotificationTitle;
    }

    public String getBody() {
        return mNotificationBody;
    }

    public Date getDate() {
        return mNotificationDate;
    }

    public String getSeen() {
        return mSeen;
    }

    public void seen() {
        mSeen = "0";
    }

    // Parcelling part
    public Notification(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.mNotificationId = data[0];
        this.mNotificationTitle = data[1];
        this.mNotificationBody = data[2];
        DateFormat format = new SimpleDateFormat("yyyy-M-d", Locale.ENGLISH);
        try {
            this.mNotificationDate = format.parse(data[3]);
        } catch (ParseException e) {
            Log.d(TAG, "Date error: " + e.getMessage());
        }
        this.mSeen = data[2];
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.mNotificationId,
                this.mNotificationTitle,
                this.mNotificationBody,
                this.mNotificationDate.toString(),
                this.mSeen});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}
