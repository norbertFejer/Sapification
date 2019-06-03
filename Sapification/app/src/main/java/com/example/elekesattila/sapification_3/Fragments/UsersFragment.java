package com.example.elekesattila.sapification_3.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.example.elekesattila.sapification_3.Classes.JsonConverter;
import com.example.elekesattila.sapification_3.Classes.User;
import com.example.elekesattila.sapification_3.Tasks.PostTask;
import com.example.elekesattila.sapification_3.R;
import com.example.elekesattila.sapification_3.Adapters.UserListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UsersFragment extends Fragment implements PostTask.OnPostRequestResponseListener {
    private static final String TAG = "SapificationUsers";
    private static final int USER = 0;
    private static final int SUPER_USER = 1;
    private static final int ADMIN = 2;
    private static final int GET_USERS = 3;
    private static final int SET_PRIVILEGE = 4;
    private UsersOnFragmentInteractionListener mListener;

    private ListView mListUsers;
    private ArrayList<User> mUsers;
    private int mSelected;
    private Dialog mDialog;
    private UserListAdapter mUserListAdapter;
    private int mMethod;
    private int mPrivilege;

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, final int checkedId) {
            Log.d(TAG, "Checked RadioButton: " + checkedId);
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.modify_privilege)
                    .setMessage(R.string.modify_privilege_alert)

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (checkedId){
                                case R.id.RadioButton0:
                                    mPrivilege = USER;
                                    setPrivilege();
                                    break;
                                case R.id.RadioButton1:
                                    mPrivilege = SUPER_USER;
                                    setPrivilege();
                                    break;
                                case R.id.RadioButton2:
                                    mPrivilege = ADMIN;
                                    setPrivilege();
                                    break;
                            }
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            mDialog.dismiss();
            mDialog = null;
        }
    };

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Log.d(TAG, "Clicked Item: " + position);
            mSelected = position;
            mDialog = new Dialog(getContext());
            //mDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
            mDialog.setContentView(R.layout.layout_radio_button_group);
            mDialog.setTitle(R.string.modify_privilege);

            RadioGroup radioGroup = (RadioGroup) mDialog.findViewById(R.id.radio_group);
            radioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);

            mDialog.show();
        }
    };

    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_users, container, false);
        }

        mListUsers = (ListView) view.findViewById(R.id.list_view_users);
        mListUsers.setOnItemClickListener(mOnItemClickListener);

        getUsers();

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //notifications = (ArrayList<Pair<String,String>>) savedInstanceState.getSerializable("notification");
            mUsers = savedInstanceState.getParcelableArrayList("user");
        }
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        try{
            //outState.putSerializable("notification", (Serializable) notifications);
            outState.putParcelableArrayList("user", mUsers);
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.replaceFragment(R.layout.fragment_users);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UsersOnFragmentInteractionListener) {
            mListener = (UsersOnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UsersOnFragmentInteractionListener");
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
        switch(mMethod) {
            case GET_USERS:
                JSONArray jsonArray = null;
                try {
                    Log.d(TAG, "Response: " + response.toString());
                    jsonArray = new JSONArray(response.toString());
                } catch (JSONException e) {
                    Log.d(TAG, "Response fail: " + e.getMessage());
                }
                Map<String, User> users = new JsonConverter(jsonArray).convertToUserMap();
                mUsers = new ArrayList<>();
                for (Map.Entry<String, User> entry : users.entrySet()) {
                    mUsers.add(entry.getValue());
                }
                /*final ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>
                        (getContext(), android.R.layout.simple_list_item_1, usersList);*/
                // DataBind ListView with items from ArrayAdapter
                mUserListAdapter = new UserListAdapter(getActivity(), mUsers);
                mListUsers.setAdapter(mUserListAdapter);
                break;
            case SET_PRIVILEGE:
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.toString());
                    if (jsonObject.get("response").toString().equalsIgnoreCase("200")){
                        Log.d(TAG, "Privilege modified");
                        mUsers.get(mSelected).setPrivilege(mPrivilege);
                        mUserListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Response fail: " + e.getMessage());
                }
                break;
        }
    }

    private void getUsers(){
        //Pair first
        mMethod = GET_USERS;
        Map<String, String> map = new HashMap<>();
        SignInFragment signInFragment = (SignInFragment)getFragmentManager().findFragmentByTag("sign_in_fragment");
        map.put("authorizationToken", signInFragment.getAuthorizationToken());
        map.put("userId", signInFragment.getUserIdToken());

        //pair second
        String url = "http://192.168.43.16/FCMManager/api/user/userManagement.php?method=getUsers";

        Pair<JSONObject, String> data = new Pair<JSONObject, String>(new JsonConverter(map).convertToJson(), url);

        PostTask postTask = new PostTask();
        postTask.setOnPostRequestResponseListener(this);
        postTask.execute(data);
    }

    private void setPrivilege(){
        //Pair first
        mMethod = SET_PRIVILEGE;
        Map<String, String> map = new HashMap<>();
        SignInFragment signInFragment = (SignInFragment)getFragmentManager().findFragmentByTag("sign_in_fragment");
        map.put("authorizationToken", signInFragment.getAuthorizationToken());
        map.put("userEmail", mUsers.get(mSelected).getEmail());
        String privilege = "";
        switch (mPrivilege){
            case USER:
                privilege = "user";
                break;
            case SUPER_USER:
                privilege = "superuser";
                break;
            case ADMIN:
                privilege = "admin";
                break;
        }
        map.put("privilege", privilege);

        //pair second
        String url = "http://192.168.43.16/FCMManager/api/user/userManagement.php?method=setUserPrivilegeByEmail";

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
    public interface UsersOnFragmentInteractionListener {
        void replaceFragment(int fragment);
    }
}
