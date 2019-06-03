package com.example.elekesattila.sapification_3.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.elekesattila.sapification_3.Classes.JsonConverter;
import com.example.elekesattila.sapification_3.Classes.Topic;
import com.example.elekesattila.sapification_3.MainActivity;
import com.example.elekesattila.sapification_3.Tasks.PostTask;
import com.example.elekesattila.sapification_3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewNotificationFragment extends Fragment implements PostTask.OnPostRequestResponseListener {
    private static final String TAG = "SapificationNewNotif";
    private static final int GET_TOPICS = 0;
    private static final int SEND_NOTIFICATION = 1;
    private NewNotificationOnFragmentInteractionListener mListener;
    private Button mButtonSend;
    private Button mButtonDelete;
    private Spinner mTopicSelect;
    private EditText mText;
    private EditText mTitle;
    private Map<String, Topic> topics;
    private int method;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.button_send:
                    sendNotification();
                    break;
                case R.id.button_delete_text:
                    mTitle.setText("");
                    mText.setText("");
                    break;
            }
        }
    };

    public NewNotificationFragment() {
        // Required empty public constructor
    }


    public static NewNotificationFragment newInstance() {
        NewNotificationFragment fragment = new NewNotificationFragment();
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
        View view = getView();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_new_notification, container, false);
        }

        mButtonDelete = (Button) view.findViewById(R.id.button_delete_text);
        mButtonSend = (Button) view.findViewById(R.id.button_send);
        mTopicSelect = (Spinner) view.findViewById(R.id.spinner_topics);
        mText = (EditText) view.findViewById(R.id.edit_text_message);
        mTitle = (EditText) view.findViewById(R.id.edit_text_title);

        mButtonSend.setOnClickListener(mOnClickListener);
        mButtonDelete.setOnClickListener(mOnClickListener);

        getTopics();

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mText.setText(savedInstanceState.getString("text"));
            mTitle.setText(savedInstanceState.getString("title"));
            ArrayList<String> keys = savedInstanceState.getStringArrayList("keys");
            ArrayList<Topic> values = savedInstanceState.getParcelableArrayList("values");

            List<String> topicList = new ArrayList<>();
            for (int i=0; i< keys.size(); ++i){
                topics.put(keys.get(i), values.get(i));
                topicList.add(values.get(i).getTopicName());
            }
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                    (getContext(), android.R.layout.simple_list_item_1, topicList);
            // DataBind ListView with items from ArrayAdapter
            mTopicSelect.setAdapter(arrayAdapter);
        }
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        try{
            outState.putStringArrayList("keys", new ArrayList<String>(topics.keySet()));
            outState.putParcelableArrayList("values", new ArrayList<Topic>(topics.values()));
            outState.putString("text", mText.getText().toString());
            outState.putString("title", mTitle.getText().toString());
        }
        catch (Exception e){
            Log.d(TAG, e.getMessage());
        }

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.replaceFragment(R.layout.fragment_new_notification);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewNotificationOnFragmentInteractionListener) {
            mListener = (NewNotificationOnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewNotificationOnFragmentInteractionListener");
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
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response.toString());
        } catch (JSONException e) {
            Log.d(TAG, "Response fail: " + e.getMessage());
        }
        switch(method){
            case GET_TOPICS:
                topics = new JsonConverter(jsonObject).convertToTopicMap();
                List<String> topicsList = new ArrayList<>();
                for (Map.Entry<String, Topic> entry : topics.entrySet()) {
                    topicsList.add(entry.getValue().getTopicName());
                }
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                        (getContext(), android.R.layout.simple_spinner_item, topicsList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // DataBind ListView with items from ArrayAdapter
                mTopicSelect.setAdapter(arrayAdapter);
                break;
            case SEND_NOTIFICATION:
                Map responseMap = new JsonConverter(jsonObject).convertToMap();
                if (responseMap.get("response").toString().equalsIgnoreCase("200")){
                    Log.d(TAG, "Notification sent");
                    Toast.makeText(getActivity(), R.string.new_notification_succes, Toast.LENGTH_LONG).show();
                    mTitle.setText("");
                    mText.setText("");
                    ((MainActivity)getActivity()).replaceFragment(R.layout.fragment_history);
                }
                else{
                    Log.d(TAG, "Notification send failed");
                    Toast.makeText(getActivity(), R.string.failure, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void getTopics(){
        method = GET_TOPICS;
        //pair first
        Map<String, String> map = new HashMap<>();
        SignInFragment signInFragment = (SignInFragment)getFragmentManager().findFragmentByTag("sign_in_fragment");
        map.put("authorizationToken", signInFragment.getAuthorizationToken());

        //pair second
        String url = "http://192.168.43.16/FCMManager/api/topic/topicManagement.php?method=getTopics";

        Pair<JSONObject, String> data = new Pair<JSONObject, String>(new JsonConverter(map).convertToJson(), url);

        PostTask postTask = new PostTask();
        postTask.setOnPostRequestResponseListener(this);
        postTask.execute(data);
    }

    private void sendNotification(){
        method = SEND_NOTIFICATION;
        //Pair first
        Map<String, String> map = new HashMap<>();
        SignInFragment signInFragment = (SignInFragment)getFragmentManager().findFragmentByTag("sign_in_fragment");
        map.put("authorizationToken", signInFragment.getAuthorizationToken());
        map.put("userId", signInFragment.getUserIdToken());
        Integer id = topics.get(mTopicSelect.getSelectedItem()).getId();
        map.put("topic", id.toString());
        if (mTitle.getText().toString().equals("")){
            Toast.makeText(getActivity(), R.string.add_title_to_notification, Toast.LENGTH_LONG).show();
            return;
        }
        map.put("title", mTitle.getText().toString());
        if (mText.getText().toString().equals("")){
            Toast.makeText(getActivity(), R.string.add_body_to_notification, Toast.LENGTH_LONG).show();
            return;
        }
        map.put("body", mText.getText().toString());

        //pair second
        String url = "http://192.168.43.16/FCMManager/api/notification/sendNotification.php";

        Pair<JSONObject, String> data = new Pair<JSONObject, String>(new JsonConverter(map).convertToJson(), url);

        PostTask postTask = new PostTask();
        postTask.setOnPostRequestResponseListener(this);
        postTask.execute(data);
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
    public interface NewNotificationOnFragmentInteractionListener {
        void replaceFragment(int fragment);
    }
}
