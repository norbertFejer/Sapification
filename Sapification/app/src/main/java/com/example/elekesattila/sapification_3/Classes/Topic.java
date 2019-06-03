package com.example.elekesattila.sapification_3.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Topic implements Parcelable{
    private int mId;
    private String mTopicName;
    private int mSubscribed;

    public Topic(int id, String topicName, int subscribed) {
        this.mId = id;
        this.mTopicName = topicName;
        this.mSubscribed = subscribed;
    }
    
    // Parcelling part
    public Topic(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.mId = Integer.parseInt(data[0]);
        this.mTopicName = data[1];
        this.mSubscribed = Integer.parseInt(data[2]);
    }

    public int getId() {
        return mId;
    }

    public String getTopicName() {
        return mTopicName;
    }

    public int getSubscribed() {
        return mSubscribed;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public void setTopicName(String topicName) {
        this.mTopicName = topicName;
    }

    public void setSubscribed(int subscribed) {
        this.mSubscribed = subscribed;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + mId +
                ", topicName='" + mTopicName + '\'' +
                ", subscribed=" + mSubscribed +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(this.mId),
                this.mTopicName,
                String.valueOf(this.mSubscribed)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
