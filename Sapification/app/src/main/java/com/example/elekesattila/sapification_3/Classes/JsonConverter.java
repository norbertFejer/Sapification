package com.example.elekesattila.sapification_3.Classes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonConverter {
    private static final String TAG = "SapificationJson";
    private Map<String, String> mData;
    private Map<String, Topic> mTopic;
    private Map<String, User> mUser;
    private Map<String, Notification> mNotification;
    private JSONObject mJsonObject;
    private JSONArray mJsonArray;

    public JsonConverter(Map data) {
        this.mData = data;
        mJsonObject = new JSONObject();
        mJsonArray = new JSONArray();
    }

    public JsonConverter(JSONObject jsonObject){
        this.mData = new HashMap<>();
        this.mJsonObject = jsonObject;
        mJsonArray = new JSONArray();
    }

    public JsonConverter(JSONArray jsonArray){
        this.mData = new HashMap<>();
        this.mJsonObject = new JSONObject();
        mJsonArray = jsonArray;
    }

    public JSONObject convertToJson(){
        for(String key: mData.keySet()){
            try {
                Log.d(TAG, "Put to JSON: " + key + " " + mData.get(key));
                mJsonObject.put(key, mData.get(key));
            } catch (JSONException e) {
                Log.d(TAG, "JSON Object fail: " + e.getMessage());
            }
        }
        Log.d(TAG, mJsonObject.toString());
        return mJsonObject;
    }

    public Map<String, String> convertToMap(){
        Iterator<String> iterator = mJsonObject.keys();
        String key;
        String value;
        while (iterator.hasNext()){
            key = iterator.next();
            try {
                value = mJsonObject.get(key).toString();
                mData.put(key, value);
            } catch (JSONException e) {
                Log.d(TAG, "JSON Object fail: " + e.getMessage());
            }
        }
        return mData;
    }

    public Map<String, Topic> convertToTopicMap(){
        mTopic = new HashMap<>();
        Iterator<String> iterator = mJsonObject.keys();
        String key;
        JSONObject value;
        while (iterator.hasNext()){
            key = iterator.next();
            try {
                value = (JSONObject) mJsonObject.get(key);
                int id = value.getInt("id");
                String topicName = value.getString("topicName");
                int subscribed = value.optInt("subscribed");
                Topic topic = new Topic(id, topicName, subscribed);
                Log.d(TAG, topic.toString());
                mTopic.put(topicName, topic);
            } catch (JSONException e) {
                Log.d(TAG, "JSON Object fail: " + e.getCause() + " " + e.getMessage());
            }
        }
        return mTopic;
    }

    public Map<String, User> convertToUserMap(){
        mUser = new HashMap<>();
        JSONObject value;
        for(int i=0; i<mJsonArray.length(); i++){
            try {
                value = (JSONObject) mJsonArray.getJSONObject(i);
                String name = value.getString("userName");
                String email = value.getString("userEmail");
                int privilege = value.optInt("privilege");
                User user = new User(name, email, privilege);
                Log.d(TAG, user.toString());
                mUser.put(email, user);
            } catch (JSONException e) {
                Log.d(TAG, "JSON Object fail: " + e.getCause() + " " + e.getMessage());
            }
        }
        return mUser;
    }

    public Map<String, Notification> convertToNotificationMap(){
        mNotification = new HashMap<>();
        JSONObject value;
        for(int i=0; i<mJsonArray.length(); i++){
            try {
                value = (JSONObject) mJsonArray.getJSONObject(i);
                String id = value.getString("notificationId");
                String title = value.getString("notificationTitle");
                String body = value.getString("notificationBody");
                String date = value.getString("notificationDate");
                String seen = value.getString("seen");
                Notification notification = new Notification(id, title, body, date, seen);
                Log.d(TAG, notification.toString());
                mNotification.put(id, notification);
            } catch (JSONException e) {
                Log.d(TAG, "JSON Object fail: " + e.getCause() + " " + e.getMessage());
            }
        }
        return mNotification;
    }
}
