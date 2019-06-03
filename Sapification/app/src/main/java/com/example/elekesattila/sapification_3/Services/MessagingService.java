package com.example.elekesattila.sapification_3.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.elekesattila.sapification_3.MainActivity;
import com.example.elekesattila.sapification_3.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.app.NotificationChannel.DEFAULT_CHANNEL_ID;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "SapificationService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived");
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        sendMessage();
    }

    @Override
    public void onNewToken(String s){
        Log.d(TAG, "onNewToken");
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate");
    }

    private void showNotification(String title, String body) {
        Log.d(TAG, "showNotification " + body);
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);
        Log.d(TAG, "Notification build");

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
        Log.d(TAG, "Notifying");
    }

    //FOR ACTIVITY
    private void sendMessage() {
        Intent intent = new Intent("new_notification");
        // add data
        intent.putExtra("message", "data");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
