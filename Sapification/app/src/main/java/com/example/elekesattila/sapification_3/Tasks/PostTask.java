package com.example.elekesattila.sapification_3.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class PostTask extends AsyncTask<Pair<JSONObject, String>, Void, Response> {
    private static final String TAG = "SapificationPostTask";
    private OnPostRequestResponseListener mOnPostRequestResponseListener;

    @Override
    protected void onPreExecute(){
        Log.d(TAG, "AsyncTask started");
    }

    @Override
    protected Response doInBackground(Pair<JSONObject, String>... params) {
        //Using OkHttp3
        JSONObject jsonObject = params[0].first;
        String url = params[0].second;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        Log.d(TAG, "OkHttpClient created");

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Log.d(TAG, "RequestBody created");

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.d(TAG, "Request created");

        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            Log.d(TAG, "Call execution error: " + e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response response){
        super.onPostExecute(response);
        try {
            String string = response.body().string()/*.replace("\"","\\\"")*/;
            Log.d(TAG, "Response:"  + string);
            mOnPostRequestResponseListener.onPostResponse(string);
        } catch (Exception e) {
            Log.d(TAG, "Response fail: " + e.getMessage());
        }
        Log.d(TAG, "AsyncTask ended");
    }

    public interface OnPostRequestResponseListener {
        void onPostResponse(Object response);
    }

    public void setOnPostRequestResponseListener(OnPostRequestResponseListener listener){
        this.mOnPostRequestResponseListener = listener;
    }
}

