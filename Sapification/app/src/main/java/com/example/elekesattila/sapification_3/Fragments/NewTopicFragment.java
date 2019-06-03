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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.elekesattila.sapification_3.Classes.JsonConverter;
import com.example.elekesattila.sapification_3.MainActivity;
import com.example.elekesattila.sapification_3.Tasks.PostTask;
import com.example.elekesattila.sapification_3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewTopicOnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewTopicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTopicFragment extends Fragment implements PostTask.OnPostRequestResponseListener {
    private static final String TAG = "SapificationNewTopic";
    private NewTopicOnFragmentInteractionListener mListener;

    private EditText mEditTextTopicName;
    private Button mButtonAdd;
    private Button mButtonDelete;
    private String text;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.button_add_topic:
                    addTopic();
                    break;
                case R.id.button_delete_text:
                    mEditTextTopicName.setText("");
                    break;
            }
        }
    };

    private void addTopic() {
        Log.d(TAG, "Sending user sign in data");

        //Pair first
        Map<String, String> map = new HashMap<>();
        SignInFragment signInFragment = (SignInFragment)getFragmentManager().findFragmentByTag("sign_in_fragment");
        map.put("authorizationToken", signInFragment.getAuthorizationToken());

        if (mEditTextTopicName.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(), R.string.add_title_to_topic, Toast.LENGTH_LONG).show();
            return;
        }
        //pair second
        String url = "http://192.168.43.16/FCMManager/api/topic/topicManagement.php" +
                "?method=addTopic&topic=" +
                mEditTextTopicName.getText().toString().trim();

        Pair<JSONObject, String> data = new Pair<JSONObject, String>(new JsonConverter(map).convertToJson(), url);

        PostTask postTask = new PostTask();
        postTask.setOnPostRequestResponseListener(this);
        postTask.execute(data);
    }

    public NewTopicFragment() {
        // Required empty public constructor
    }

    public static NewTopicFragment newInstance() {
        NewTopicFragment fragment = new NewTopicFragment();
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
            view = inflater.inflate(R.layout.fragment_new_topic, container, false);
        }

        mEditTextTopicName = (EditText) view.findViewById(R.id.edit_text_topic);
        mButtonAdd = (Button) view.findViewById(R.id.button_add_topic);
        mButtonDelete = (Button) view.findViewById(R.id.button_delete_text);
        mButtonAdd.setOnClickListener(mOnClickListener);
        mButtonDelete.setOnClickListener(mOnClickListener);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            text = savedInstanceState.getString("text");
        }
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        try{
            outState.putString("text", text);
        }
        catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.replaceFragment(R.layout.fragment_new_topic);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewTopicOnFragmentInteractionListener) {
            mListener = (NewTopicOnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewTopicOnFragmentInteractionListener");
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
        String responseString = new JsonConverter(jsonObject).convertToMap().get("response");
        switch (Integer.parseInt(responseString)){
            case 200:
                Toast.makeText(getActivity(), R.string.new_topic_succes, Toast.LENGTH_LONG).show();
                mEditTextTopicName.setText("");
                ((MainActivity)getActivity()).replaceFragment(R.layout.fragment_history);
                break;
            case 400:
                Toast.makeText(getActivity(), R.string.failure, Toast.LENGTH_LONG).show();
                break;
            case 406:
                Toast.makeText(getActivity(), R.string.new_topic_exists, Toast.LENGTH_LONG).show();
                break;
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
    public interface NewTopicOnFragmentInteractionListener {
        void replaceFragment(int fragment);
    }
}
