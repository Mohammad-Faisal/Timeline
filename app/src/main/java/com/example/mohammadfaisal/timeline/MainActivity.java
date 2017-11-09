package com.example.mohammadfaisal.timeline;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //log
    private static final String TAG = "MainActivity";

    //constants
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN  = 1;
    public static final int RC_PHOTO_PICKER = 2;
    public static final int RC_CAMERA_PICKER = 3;

    //widgets
    private ListView mPostListView;
    private PostAdapter mPostAdapter;
    private ImageButton mPhotoPickerButton;
    private EditText mPostEditText;
    private Button mSendButton;

    //related info
    private String mUsername;
    private String mUserID;
    private String image_url;
    private Profile profile_object;
    Uri selectedImageUri;
    private String caption;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mmDatabaseRef;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = "";
        profile_object = new Profile();
        selectedImageUri = null;
        image_url = "";

        //initialize firebase realtime database
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference().child("TimeLine").child("HomePage").child("Posts");


        //initialize Auth object
        mFirebaseAuth = FirebaseAuth.getInstance();

        //initialize Firebase Storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReference().child("TimeLine").child("users");


        //initialize views
        mPostListView = (ListView) findViewById(R.id.postListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mPostEditText = (EditText) findViewById(R.id.postEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);


        // Initialize message ListView and its adapter
        final List<TimeLinePost> timelinePosts = new ArrayList<>();
        mPostAdapter = new PostAdapter(this, R.layout.item_post, timelinePosts);
        mPostListView.setAdapter(mPostAdapter);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caption = mPostEditText.getText().toString();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(photoPickerIntent, RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send
        mPostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                    //caption = mPostEditText.getText().toString();
                } else {
                    mSendButton.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //caption = mPostEditText.getText().toString();
            }
        });
        mPostEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Post on click

                Log.d(TAG , "inside post OnClick method");

                if(mPostEditText.getText().toString().equals(""))
                {

                }
                else
                {
                    caption = mPostEditText.getText().toString();
                }

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a MMM d, ''yy");
                final String cur_time_and_date = sdf.format(c.getTime());
                if(selectedImageUri!=null)
                {
                    Log.d(TAG , "selected image uri is not null !!");
                    StorageReference photoRef = mStorageRef.child(selectedImageUri.getLastPathSegment());
                    photoRef.putFile(selectedImageUri).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downLoadURL = taskSnapshot.getDownloadUrl();
                                    image_url = downLoadURL.toString();
                                    TimeLinePost post = new TimeLinePost(mUsername, mUserID, cur_time_and_date,caption  , image_url,0,0);
                                    mDatabaseRef.push().setValue(post);

                                }
                            }
                    );
                }
                else
                {
                    Log.d(TAG , "selected image uri is null !!");
                    TimeLinePost post = new TimeLinePost(mUsername, mUserID, cur_time_and_date, caption , "",0,0);
                    mDatabaseRef.push().setValue(post);
                }
                mPostEditText.setText("");
            }
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if(mUser != null){
                    mUserID = mUser.getUid();
                    mmDatabaseRef = mDatabase.getReference().child("TimeLine").child("users").child(mUserID);
                    mmDatabaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            profile_object = dataSnapshot.getValue(Profile.class);
                            if(profile_object == null)
                            {
                                Intent intent = new Intent(MainActivity.this, SetProfile.class);
                                intent.putExtra("userID" ,mUserID );
                                startActivity(intent);
                            }
                            else
                            {
                                mUsername = profile_object.getUserName();
                                if(mUsername.equals(""))
                                {
                                    Intent intent = new Intent(MainActivity.this, SetProfile.class);
                                    intent.putExtra("userID" ,mUserID );
                                    startActivity(intent);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                    onSignedInInitialized(mUsername);
                }
                else{
                    //user is not signed in
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(true)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                mUserID = response.getIdpToken();
                return;
            }
            else if(resultCode == RESULT_CANCELED)
            {
                finish();
                return;
            }
            else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showToast("Sign in cancelled !");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showToast("no internet Connection :/ ");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showToast("unknown error ! :( ");
                    return;
                }
                showToast("unknown sign in response");
            }
        }
        else if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Log.d(TAG , "result code is RC_PHOTO_PICKER");
            selectedImageUri = data.getData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showToast(String a){
        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        Log.d(TAG , "inside onResume");
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause() {
        Log.d(TAG , "inside onPause");
        super.onPause();
        if(mAuthStateListener!=null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mPostAdapter.clear();
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    private void onSignedInInitialized(String username){
        Log.d(TAG , "inside onSignedInInitialized");
        mUsername = username;
        attatchDatabaseReadListener();
    }
    private void onSignedOutCleanUp(){
        Log.d(TAG , "inside onSignedOutCleanUp");
        mUsername = ANONYMOUS;
        mPostAdapter.clear();
    }
    private void attatchDatabaseReadListener(){
        Log.d(TAG , "inside attatchDatabaseReadListener");
        if(mChildEventListener==null)
        {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    TimeLinePost post = dataSnapshot.getValue(TimeLinePost.class);
                    mPostAdapter.add(post);
                    //mPostAdapter.insert(post,0);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabaseRef.addChildEventListener(mChildEventListener);
        }
    }
    private void detachDatabaseReadListener(){
        Log.d(TAG , "inside detachDatabaseReadListener");
        if(mChildEventListener!=null)
        {
            mDatabaseRef.removeEventListener(mChildEventListener);
            mChildEventListener= null;
        }
    }


}
