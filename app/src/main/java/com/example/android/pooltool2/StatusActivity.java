package com.example.android.pooltool2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mStatus;
    private Button mSaveBtn;

    private ProgressDialog mProgress;

    private DatabaseReference mStatusDatabase;
    private FirebaseAuth mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser = FirebaseAuth.getInstance();
        String uid = mCurrentUser.getCurrentUser().getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mToolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value");

        mStatus = (EditText) findViewById(R.id.status_input);
        mSaveBtn = (Button) findViewById(R.id.save_status_change_button);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes...");
                mProgress.setMessage("Please wait while we save the changes.");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                String status = mStatus.getText().toString();
                ChangeProfileStatus(status);
            }
        });
    }
    public void ChangeProfileStatus(String new_status) {
        if(TextUtils.isEmpty(new_status)) {
            Toast.makeText(StatusActivity.this,"Please Write Your Status",Toast.LENGTH_LONG).show();
        }
        else {
            mStatusDatabase.child("user_status").setValue(new_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mProgress.dismiss();
                        Intent settingsIntent = new Intent(StatusActivity.this,SettingsActivity.class);
                        startActivity(settingsIntent);
                        
                    }
                    else {
                        mProgress.hide();
                        Toast.makeText(StatusActivity.this, "Error Updating Status.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
