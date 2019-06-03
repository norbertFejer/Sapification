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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.elekesattila.sapification_3.Classes.JsonConverter;
import com.example.elekesattila.sapification_3.Classes.Topic;
import com.example.elekesattila.sapification_3.Tasks.PostTask;
import com.example.elekesattila.sapification_3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscribeOnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscribeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscribeFragment extends Fragment implements PostTask.OnPostRequestResponseListener {
    private static final String TAG = "SapificationSubscribe";
    private static final int FALSE = 0;
    private static final int TRUE = 1;
    private static final int TOPIC_LIST = 2;
    private static final int SUBSCRIBE = 3;
    private static final int UNSUBSCRIBE = 4;
    private SubscribeOnFragmentInteractionListener mListener;
    private ListView mTopicListView;
    private Button mButtonSubscribeManager;
    private Map<String, Topic> topics;
    private int mSelectedTopic;
    private int mMethod;

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Selected list_user item: " + position);
            mSelectedTopic = position;
            if (topics.get(mTopicListView.getItemAtPosition(position)).getSubscribed() == 1){
                Toast.makeText(getActivity(), R.string.subscribed, Toast.LENGTH_LONG).show();
                mButtonSubscribeManager.setText(R.string.unsubscribe);
            }
            else{
                Toast.makeText(getActivity(), R.string.not_subscribed, Toast.LENGTH_LONG).show();
                mButtonSubscribeManager.setText(R.string.subscribe);
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.button_subscribe_manager:
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.sure)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Log.d(TAG, "Button pressed");
                                    manageSubscribe();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    break;
            }
        }
    };

    /*private Boolean manageSubscribe(){
        Boolean result;
        return result;
    }*/

    public SubscribeFragment() {
        // Required empty public constructor
    }

    public static SubscribeFragment newInstance() {
        SubscribeFragment fragment = new SubscribeFragment();
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
            view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        }

        getTopicList();

        mTopicListView = (ListView) view.findViewById(R.id.list_view_topic);
        mButtonSubscribeManager = (Button) view.findViewById(R.id.button_subscribe_manager);
        // Create an ArrayAdapter from List
        mTopicListView.setOnItemClickListener(mOnItemClickListener);
        mButtonSubscribeManager.setOnClickListener(mOnClickListener);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<String> keys = savedInstanceState.getStringArrayList("keys");
            ArrayList<Topic> values = savedInstanceState.getParcelableArrayList("values");
            List<String> topicList = new ArrayList<>();

            for (int i=0; i< keys.size(); ++i){
                topics.put(keys.get(i), values.get(i));
                topicList.add(values.get(i).getTopicName());
            }
            mButtonSubscribeManager.setText(savedInstanceState.getCharSequence("button_text"));
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                    (getContext(), android.R.layout.simple_list_item_1, topicList);
            // DataBind ListView with items from ArrayAdapter
            mTopicListView.setAdapter(arrayAdapter);
        }
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        try{
            outState.putStringArrayList("keys", new ArrayList<String>(topics.keySet()));
            outState.putParcelableArrayList("values", new ArrayList<Topic>(topics.values()));
            outState.putCharSequence("button_text", mButtonSubscribeManager.getText());
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.replaceFragment(R.layout.fragment_subscribe);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SubscribeOnFragmentInteractionListener) {
            mListener = (SubscribeOnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignInOnFragmentInteractionListener");
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
        switch(mMethod){
            case TOPIC_LIST:
                topics = new JsonConverter(jsonObject).convertToTopicMap();
                List<String> topicsList = new ArrayList<>();
                for (Map.Entry<String, Topic> entry : topics.entrySet()) {
                    topicsList.add(entry.getValue().getTopicName());
                }
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                        (getContext(), android.R.layout.simple_list_item_1, topicsList);
                // DataBind ListView with items from ArrayAdapter
                mTopicListView.setAdapter(arrayAdapter);
                break;
            case SUBSCRIBE:
                Map mapSubscribe = new JsonConverter(jsonObject).convertToMap();
                if (mapSubscribe.get("response").toString().equalsIgnoreCase("200")){
                    Log.d(TAG, "Subscribe succes");
                    topics.get(mTopicListView.getItemAtPosition(mSelectedTopic)).setSubscribed(TRUE);
                    mButtonSubscribeManager.setText(R.string.unsubscribe);
                    Toast.makeText(getActivity(), R.string.subscribe_success, Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d(TAG, "Subscribe failure");
                    Toast.makeText(getActivity(), R.string.subscribe_failure, Toast.LENGTH_LONG).show();
                }
                break;
            case UNSUBSCRIBE:
                Map mapUnsubscribe = new JsonConverter(jsonObject).convertToMap();
                if (mapUnsubscribe.get("response").toString().equalsIgnoreCase("200")){
                    Log.d(TAG, "Unsubscribe succes");
                    topics.get(mTopicListView.getItemAtPosition(mSelectedTopic)).setSubscribed(FALSE);
                    mButtonSubscribeManager.setText(R.string.subscribe);
                    Toast.makeText(getActivity(), R.string.unsubscribe_success, Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d(TAG, "Unsubscribe failure");
                    Toast.makeText(getActivity(), R.string.unsubscribe_failure, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private void getTopicList(){
        //Pair first
        mMethod = TOPIC_LIST;
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

    private void manageSubscribe(){
        //Pair first
        Map<String, String> map = new HashMap<>();
        SignInFragment signInFragment = (SignInFragment)getFragmentManager().findFragmentByTag("sign_in_fragment");
        map.put("authorizationToken", signInFragment.getAuthorizationToken());

        //pair second
        String url = "http://192.168.43.16/FCMManager/api/topic/topicManagement.php";

        if (topics.get(mTopicListView.getItemAtPosition(mSelectedTopic)).getSubscribed() == FALSE){
            Log.d(TAG, "Subscribing");
            mMethod = SUBSCRIBE;
            url += "?method=subscribe" + "&topic=" + topics.get(
                    mTopicListView.getItemAtPosition(mSelectedTopic)).getId();
        }
        else{
            Log.d(TAG, "Unsubscribing");
            mMethod = UNSUBSCRIBE;
            url += "?method=unsubscribe" + "&topic=" + topics.get(
                    mTopicListView.getItemAtPosition(mSelectedTopic)).getId();
        }
        Log.d(TAG, "URL: " + url);
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
    public interface SubscribeOnFragmentInteractionListener {
        void replaceFragment(int fragment);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
