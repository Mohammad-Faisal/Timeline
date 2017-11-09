package com.example.mohammadfaisal.timeline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetProfile extends AppCompatActivity {


    TextView name_text;
    Button set_button;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    Profile profile_object;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        //setting up view objects
        name_text = (TextView) findViewById(R.id.txt_name_set_profile);
        set_button = (Button) findViewById(R.id.btn_save_set_profile);

        //initialize profile object and get the user ID
        profile_object = new Profile("" , "");
        userID = getIntent().getStringExtra("userID");


        //setting up datebase
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference().child("TimeLine").child("users").child(userID);

        set_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = name_text.getText().toString();
                profile_object.setUserID(userID);
                profile_object.setUserName(p);
                mDatabaseRef.setValue(profile_object).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
            }
        });


    }
}
