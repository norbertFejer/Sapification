package com.example.elekesattila.sapification_3;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.elekesattila.sapification_3.Fragments.HistoryFragment;
import com.example.elekesattila.sapification_3.Fragments.NewNotificationFragment;
import com.example.elekesattila.sapification_3.Fragments.NewTopicFragment;
import com.example.elekesattila.sapification_3.Fragments.SignInFragment;
import com.example.elekesattila.sapification_3.Fragments.SubscribeFragment;
import com.example.elekesattila.sapification_3.Fragments.UsersFragment;


public class MainActivity extends AppCompatActivity implements SignInFragment.SignInOnFragmentInteractionListener,
        SubscribeFragment.SubscribeOnFragmentInteractionListener,
        NewTopicFragment.NewTopicOnFragmentInteractionListener,
        HistoryFragment.HistoryOnFragmentInteractionListener,
        UsersFragment.UsersOnFragmentInteractionListener,
        NewNotificationFragment.NewNotificationOnFragmentInteractionListener {
    private static final String TAG = "SapiMain";
    private static final int VIEW_USERS = 0;
    private static final int VIEW_TOPICS = 1;
    private static final int NEW_TOPIC = 2;
    private static final int VIEW_HISTORY = 3;
    private static final int SIGN_OUT = 4;
    private static final int NEW_NOTIFICATION = 5;
    private SignInFragment mSignInFragment;
    private SubscribeFragment mSubscribeFragment;
    private NewTopicFragment mNewTopicFragment;
    private HistoryFragment mHistoryFragment;
    private UsersFragment mUsersFragment;
    private NewNotificationFragment mNewNotificationFragment;
    private Button mButtonNotification;
    private Menu mMenu;
    private int mPrivilege;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        //setContentView(R.layout.activity_main);
        Log.d(TAG, "Sapification start.");
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        //startService(new Intent(this, MessagingService.class));

        mSignInFragment = SignInFragment.newInstance();
        mSubscribeFragment = SubscribeFragment.newInstance();
        mNewTopicFragment = NewTopicFragment.newInstance();
        mHistoryFragment = HistoryFragment.newInstance();
        mUsersFragment = UsersFragment.newInstance();
        mNewNotificationFragment = NewNotificationFragment.newInstance();
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                mSignInFragment = (SignInFragment)
                        getSupportFragmentManager().findFragmentByTag("sign_in_fragment");
                mSubscribeFragment = (SubscribeFragment)
                        getSupportFragmentManager().findFragmentByTag("subscribe_fragment");
                mNewTopicFragment = (NewTopicFragment)
                        getSupportFragmentManager().findFragmentByTag("new_topic_fragment");
                mHistoryFragment = (HistoryFragment)
                        getSupportFragmentManager().findFragmentByTag("history_fragment");
                mUsersFragment = (UsersFragment)
                        getSupportFragmentManager().findFragmentByTag("users_fragment");
                mNewNotificationFragment = (NewNotificationFragment)
                        getSupportFragmentManager().findFragmentByTag("new_notification_fragment");
                Log.d(TAG, "Fragment loaded.");
            }
            else{
                getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, mSignInFragment, "sign_in_fragment").commit();
                Log.d(TAG, "Fragment added.");
            }
        }
        else{
            Log.d(TAG, "Fragment container not found.");
        }

        mButtonNotification = (Button) findViewById(R.id.button_notification);
        mButtonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(R.layout.fragment_history);
            }
        });

    }

    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /*View notificationCounter = menu.findItem(R.id.notification_counter).getActionView();
        mTextViewNotificationCounter = (TextView) notificationCounter.findViewById(R.id.notif_count);*/
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        addMenuItems();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case VIEW_USERS:
                replaceFragment(R.layout.fragment_users);
                break;
            case VIEW_TOPICS:
            replaceFragment(R.layout.fragment_subscribe);
            break;
            case NEW_TOPIC:
                replaceFragment(R.layout.fragment_new_topic);
                break;
            case VIEW_HISTORY:
                replaceFragment(R.layout.fragment_history);
                break;
            case SIGN_OUT:
                mSignInFragment.signOut();
                mMenu.clear();
                break;
            case NEW_NOTIFICATION:
                replaceFragment(R.layout.fragment_new_notification);
                break;
        }
        return false;
    }

    public void addMenuItems(){
        if (mSignInFragment.hasActiveUser()){
            Log.d(TAG,"User privilege: " + mPrivilege);
            mMenu.clear();
            mMenu.add(0, VIEW_HISTORY, Menu.NONE, R.string.history);
            mMenu.add(0, VIEW_TOPICS, Menu.NONE, R.string.view_topics);
            if (mPrivilege == 1){
                mMenu.add(0, NEW_TOPIC, Menu.NONE, R.string.new_topic);
                mMenu.add(0, NEW_NOTIFICATION, Menu.NONE, R.string.new_notification);
            }
            if (mPrivilege == 2) {
                mMenu.add(0, NEW_TOPIC, Menu.NONE, R.string.new_topic);
                mMenu.add(0, NEW_NOTIFICATION, Menu.NONE, R.string.new_notification);
                mMenu.add(0, VIEW_USERS, Menu.NONE, R.string.users);
            }
            mMenu.add(0, SIGN_OUT, Menu.NONE, R.string.sign_out);
        }
        else{
            mMenu.clear();
        }
    }

    public void setPrivilege(String privilege) {
        this.mPrivilege = Integer.parseInt(privilege);
        Log.d(TAG,"User privilege: " + mPrivilege);
    }

    //FRAGMENT
    public void replaceFragment(int fragment){
        Log.d(TAG, "Replacing fragment with");
        switch(fragment){
            case R.layout.fragment_sign_in:
                Log.d(TAG, "    Sign in fragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mSignInFragment, "sign_in_fragment").addToBackStack(null).commit();
                break;
            case R.layout.fragment_subscribe:
                Log.d(TAG, "    Subscribe fragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mSubscribeFragment, "subscribe_fragment").addToBackStack(null).commit();
                break;
            case R.layout.fragment_new_topic:
                Log.d(TAG, "    New topic fragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mNewTopicFragment, "new_topic_fragment").addToBackStack(null).commit();
                break;
            case R.layout.fragment_history:
                Log.d(TAG, "    History fragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mHistoryFragment, "history_fragment").addToBackStack(null).commit();
                break;
            case R.layout.fragment_users:
                Log.d(TAG, "    Users fragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mUsersFragment, "users_fragment").addToBackStack(null).commit();
                break;
            case R.layout.fragment_new_notification:
                Log.d(TAG, "    Users fragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mNewNotificationFragment, "new_notification_fragment").addToBackStack(null).commit();
                break;
        }
    }

    public void setNewNotificationNum(int num){
        mButtonNotification.setText(String.valueOf(num));
    }

    public void decrementNewNotificationNum(){
        Integer num = Integer.parseInt(mButtonNotification.getText().toString());
        if (num > 0){
            num--;
        }
        mButtonNotification.setText(num.toString());
    }

    //SERVICE
    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("new_notification"));
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            Log.d(TAG, "new notification");
            Integer num = Integer.parseInt(mButtonNotification.getText().toString());
            num++;
            mButtonNotification.setText(num.toString());
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
    //CHECK CONNECTION
    /*public void checkMobileDataConnection(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); //TYPE_WIFI
        boolean isMobileConn = networkInfo.isConnected();

        Log.d(TAG, "Mobile connected: " + isMobileConn);
    }*/
}
