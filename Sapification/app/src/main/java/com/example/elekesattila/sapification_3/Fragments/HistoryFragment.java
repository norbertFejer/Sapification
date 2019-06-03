package com.example.elekesattila.sapification_3.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.elekesattila.sapification_3.Classes.JsonConverter;
import com.example.elekesattila.sapification_3.Classes.Notification;
import com.example.elekesattila.sapification_3.MainActivity;
import com.example.elekesattila.sapification_3.Adapters.NotificationListAdapter;
import com.example.elekesattila.sapification_3.Tasks.PostTask;
import com.example.elekesattila.sapification_3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryOnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements Serializable,
        PostTask.OnPostRequestResponseListener {
    private HistoryOnFragmentInteractionListener mListener;

    private static final String TAG = "SapifictionHistory";
    private final static int GET_NOTIFICATIONS = 0;
    private final static int SEEN = 1;

    private ListView mListNotification;
    private ArrayList<Notification> mNotifications;
    private NotificationListAdapter mNotificationListAdapter;
    private int mMethod;
    private int mSelectedNotification;
    private int mUnseen;

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Selected list_user item: " + position);
            mSelectedNotification = position;
            new AlertDialog.Builder(getContext())
                    .setTitle(mNotifications.get(mSelectedNotification).getTitle())
                    .setMessage(mNotifications.get(mSelectedNotification).getBody())

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            seen();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    };

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_history, container, false);
        }

        mListNotification = (ListView) view.findViewById(R.id.list_view_notifications);
        mListNotification.setOnItemClickListener(mOnItemClickListener);

        getNotifications();

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //notifications = (ArrayList<Pair<String,String>>) savedInstanceState.getSerializable("notification");
            mNotifications = savedInstanceState.getParcelableArrayList("notification");
        }
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        try{
            //outState.putSerializable("notification", (Serializable) notifications);
            outState.putParcelableArrayList("notification", mNotifications);
        }
        catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.replaceFragment(R.layout.fragment_history);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HistoryOnFragmentInteractionListener) {
            mListener = (HistoryOnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HistoryOnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPostResponse(Object response) {
        Log.d(TAG, "onPostResponse");
        switch (mMethod){
            case GET_NOTIFICATIONS:
                JSONArray jsonArray = null;
                try {
                    Log.d(TAG, "Response: " + response.toString());
                    jsonArray = new JSONArray(response.toString());
                } catch (JSONException e) {
                    Log.d(TAG, "Response fail: " + e.getMessage());
                }
                Map<String, Notification> notifications = new JsonConverter(jsonArray).convertToNotificationMap();
                mNotifications = new ArrayList<>();
                for (Map.Entry<String, Notification> entry : notifications.entrySet()) {
                    mNotifications.add(entry.getValue());
                }
                Collections.reverse(mNotifications);
                /*final ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>
                        (getContext(), android.R.layout.simple_list_item_1, usersList);*/
                // DataBind ListView with items from ArrayAdapter
                mNotificationListAdapter = new NotificationListAdapter(getActivity(), mNotifications);
                mListNotification.setAdapter(mNotificationListAdapter);
                countUnseen();
                ((MainActivity)getActivity()).setNewNotificationNum(mUnseen);
                break;
            case SEEN:
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.toString());
                    if (jsonObject.get("response").toString().equalsIgnoreCase("200")){
                        Log.d(TAG, "Seen");
                        mNotifications.get(mSelectedNotification).seen();
                        mUnseen--;
                        mNotificationListAdapter.notifyDataSetChanged();
                        ((MainActivity)getActivity()).decrementNewNotificationNum();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Response fail: " + e.getMessage());
                }
                break;
        }
    }

    private void getNotifications(){
        //Pair first
        mMethod = GET_NOTIFICATIONS;
        Map<String, String> map = new HashMap<>();
        SignInFragment signInFragment = (SignInFragment)getFragmentManager().findFragmentByTag("sign_in_fragment");
        map.put("authorizationToken", signInFragment.getAuthorizationToken());
        map.put("userId", signInFragment.getUserIdToken());

        //pair second
        String url = "http://192.168.43.16/FCMManager/api/notification/notificationManagement.php?method=getMyNotifications";

        Pair<JSONObject, String> data = new Pair<JSONObject, String>(new JsonConverter(map).convertToJson(), url);

        PostTask postTask = new PostTask();
        postTask.setOnPostRequestResponseListener(this);
        postTask.execute(data);
    }

    private void seen(){
        //Pair first
        mMethod = SEEN;
        Map<String, String> map = new HashMap<>();
        SignInFragment signInFragment = (SignInFragment)getFragmentManager().findFragmentByTag("sign_in_fragment");
        map.put("authorizationToken", signInFragment.getAuthorizationToken());
        map.put("userId", signInFragment.getUserIdToken());
        map.put("notificationId", mNotifications.get(mSelectedNotification).getId());

        //pair second
        String url = "http://192.168.43.16/FCMManager/api/notification/notificationManagement.php?method=setNotificationToSeen";

        Pair<JSONObject, String> data = new Pair<JSONObject, String>(new JsonConverter(map).convertToJson(), url);

        PostTask postTask = new PostTask();
        postTask.setOnPostRequestResponseListener(this);
        postTask.execute(data);
    }

    private void countUnseen(){
        mUnseen = 0;
        for (int i=0; i<mNotifications.size(); ++i){
            //inverse logic
            if (mNotifications.get(i).getSeen().equals("1")){
                ++mUnseen;
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface HistoryOnFragmentInteractionListener {
        void replaceFragment(int fragment);
    }
}
