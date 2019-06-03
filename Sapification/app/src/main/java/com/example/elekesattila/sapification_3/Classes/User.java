package com.example.elekesattila.sapification_3.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String mName;
    private String mEmail;
    private int mPrivilege;

    public User(String mName, String mEmail, int mPermission) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.mPrivilege = mPermission;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public int getPrivilege() {
        return mPrivilege;
    }

    public void setPrivilege(int privilege) {
        this.mPrivilege = privilege;
    }

    @Override
    public String toString() {
        return "User{" +
                "Name='" + mName + '\'' +
                ", Email='" + mEmail + '\'' +
                ", Privilege=" + mPrivilege +
                '}';
    }

    // Parcelling part
    public User(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.mName = data[0];
        this.mEmail = data[1];
        this.mPrivilege = Integer.parseInt(data[2]);
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.mName,
                this.mEmail,
                String.valueOf(this.mPrivilege)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
