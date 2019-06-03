package com.example.elekesattila.sapification_3.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.elekesattila.sapification_3.Classes.JsonConverter;
import com.example.elekesattila.sapification_3.MainActivity;
import com.example.elekesattila.sapification_3.Tasks.PostTask;
import com.example.elekesattila.sapification_3.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignInOnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment implements PostTask.OnPostRequestResponseListener {
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "SapificationSignIn";
    private Button mSignInButton;
    private SignInOnFragmentInteractionListener mListener;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String mAuthorizationToken;
    private String mDeviceToken;
    private String mUIdToken;

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_sign_in:
                signIn();
                break;
            }
        }
    };

    private OnCompleteListener mOnCompleteListener = new OnCompleteListener<InstanceIdResult>(){
        @Override
        public void onComplete(@NonNull Task<InstanceIdResult> task) {
            if (!task.isSuccessful()) {
                Log.w(TAG, "getInstanceId failed", task.getException());
                return;
            }
            else{
                mDeviceToken = task.getResult().getToken();
                Log.d(TAG, "Device token in onCompleteListener: " + mDeviceToken);
                if (mDeviceToken == null){
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(mOnCompleteListener);
                }
                else {
                    getUserData();
                }
            }
        }
    };

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance() {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureGoogleSingIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getView();
        if (view == null){
            view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        }
        mSignInButton = (Button) view.findViewById(R.id.button_sign_in);
        mSignInButton.setOnClickListener(mOnClickListener);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.replaceFragment(R.layout.fragment_sign_in);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignInOnFragmentInteractionListener) {
            mListener = (SignInOnFragmentInteractionListener) context;
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
        Map map = new JsonConverter(jsonObject).convertToMap();
        ((MainActivity)getActivity()).setPrivilege(map.get("userPrivilege").toString());
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
    public interface SignInOnFragmentInteractionListener {
        void replaceFragment(int fragment);
    }

    private void configureGoogleSingIn(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mAuth = FirebaseAuth.getInstance();

        if (!hasActiveUser()){
            //signIn();
        }
        else{
            //mEmail = mAuth.getCurrentUser().getEmail();
            //FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(mOnSuccessListener);
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(mOnCompleteListener);
            //Log.d(TAG, "getUserData call from configureGoogleSingIn");
            //getUserData();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Google sign in succes");
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Firebase sign in succes");
                            //getUserData();
                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(mOnCompleteListener);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "Firebase sign in failed: " + task.getException().toString());
                        }
                    }
                });
    }

    private void getUserData(){
        FirebaseUser mUser = mAuth.getCurrentUser();
        mUIdToken = mUser.getUid();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            mAuthorizationToken = task.getResult().getToken();
                            Log.d(TAG, "Token: " + mAuthorizationToken);
                            sendSignInData();
                        } else {
                            Log.d(TAG, "Token: " + task.getException());
                        }
                    }
                });
    }

    private void sendSignInData(){
        Log.d(TAG, "Sending user sign in data");

        //Pair first
        Map<String, String> map = new HashMap<>();
        map.put("deviceId", "\"" + mDeviceToken + "\"");
        map.put("authorizationToken", mAuthorizationToken);
        map.put("userId", mUIdToken);

        //pair second
        String url = "http://192.168.43.16/FCMManager/api/user/getPrivilege.php";

        Pair<JSONObject, String> data = new Pair<JSONObject, String>(new JsonConverter(map).convertToJson(), url);

        PostTask postTask = new PostTask();
        postTask.setOnPostRequestResponseListener(this);
        postTask.execute(data);
        ((MainActivity)getActivity()).addMenuItems();
        ((MainActivity)getActivity()).replaceFragment(R.layout.fragment_history);
    }

    public void signOut(){
        Log.d(TAG, "Signing out.");
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        ((MainActivity)getActivity()).replaceFragment(R.layout.fragment_sign_in);
        try{
            ((MainActivity)getActivity()).setNewNotificationNum(0);
        }
        catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public Boolean hasActiveUser(){
        if (mAuth != null && mAuth.getCurrentUser() != null){
            return true;
        }
        else {
            return false;
        }
    }

    public String getAuthorizationToken() {
        return mAuthorizationToken;
    }
    public String getUserIdToken() {
        return mUIdToken;
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
