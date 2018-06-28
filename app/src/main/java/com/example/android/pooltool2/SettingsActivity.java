package com.example.android.pooltool2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mCurrentUser;

    private CircleImageView mImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusBtn;
    private Button mImageBtn;

    private ProgressDialog mProgress;

    private static final int GALLERY_PICK = 1;

    private StorageReference mImageStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCurrentUser = FirebaseAuth.getInstance();
        String uid = mCurrentUser.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mUserDatabase.keepSynced(true);

        mImageStorage = FirebaseStorage.getInstance().getReference().child("Profile_Image");

        mImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        mName = (TextView) findViewById(R.id.settings_username);
        mStatus = (TextView) findViewById(R.id.settings_user_status);

        mStatusBtn = (Button) findViewById(R.id.settings_change_profile_status);
        mImageBtn = (Button) findViewById(R.id.settings_change_profile_image_button);

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = mStatus.getText().toString();
                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                        */

            }
        });


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").toString();
                String status = dataSnapshot.child("user_status").getValue().toString();


                mName.setText(name);
                mStatus.setText(status);
                if(!image.equals("default")) {
                    Picasso.get().load(image).into(mImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(SettingsActivity.this);
                mProgress.setTitle("Uploading Image...");
                mProgress.setMessage("Please wait while we upload your image.");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                Uri resultUri = result.getUri();
                /*File thumb_filePath = new File(resultUri.getPath());
                Bitmap thumb_bitmap = null;
                byte[] thumb_byte = null;
                try {
                    thumb_bitmap = new Compressor(this).setMaxHeight(200).setMaxWidth(200).setQuality(75).compressToBitmap(thumb_filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    thumb_byte =  baos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                String uid = mCurrentUser.getCurrentUser().getUid();
                StorageReference filepath = mImageStorage.child("profile_images").child(uid+ ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(uid+ ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            @SuppressWarnings("VisibleForTests") final
                            String download_url = task.getResult().getDownloadUrl().toString();

                            mUserDatabase.child("user_image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mProgress.dismiss();
                                }
                            });
                        }else {
                            mProgress.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
