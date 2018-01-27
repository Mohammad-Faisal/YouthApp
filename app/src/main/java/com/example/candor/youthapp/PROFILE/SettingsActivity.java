package com.example.candor.youthapp.PROFILE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.candor.youthapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private StorageReference mStorage;

    //widgets
    private ImageView mProfileBlurImage , mProfileImage;
    private EditText mProfileName , mProfileLocation , mProfilePhoneNumber , mProfileEmail , mProfileBloodGroup , mProfileDateOfBirth , mProfileBio ;
    private Button mProfileSaveButton , mProfilePicEditButton;
    private ProgressDialog mProgress;

    //variables
    private String mUserID;
    Uri imagetUri;
    private static final int  GALLERY_PICK = 1;
    private  static final int MAX_LENGTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        //widgets
        mProfileName = findViewById(R.id.profile_name_edit);
        mProfileLocation  = findViewById(R.id.profile_location_edit);
        mProfilePhoneNumber = findViewById(R.id.profile_mobile_no_edit);
        mProfileEmail  = findViewById(R.id.profile_email_edit);
        mProfileDateOfBirth = findViewById(R.id.profile_date_of_birth_edit);
        mProfileBloodGroup = findViewById(R.id.profile_blood_group_edit);
        mProfileBio = findViewById(R.id.profile_bio_edit);

        mProfileImage = findViewById(R.id.profile_image_edit);
        mProfilePicEditButton =findViewById(R.id.profile_image_change_button_edit);
        mProfileSaveButton = findViewById(R.id.profile_edit_save_button);



        //firebase initialize
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserID = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("users").child(mUserID);
        mStorage = FirebaseStorage.getInstance().getReference();


        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String name = dataSnapshot.child("name").getValue().toString();
                String phone_number = dataSnapshot.child("phone_number").getValue().toString();
                String email  =  dataSnapshot.child("email").getValue().toString();
                String date_of_birth  =  dataSnapshot.child("date_of_birth").getValue().toString();
                String blood_group =  dataSnapshot.child("blood_group").getValue().toString();
                String bio = dataSnapshot.child("bio").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();

                if(!name.equals("Name")){
                    mProfileName.setText(name);
                }
                if(!phone_number.equals("default")){
                    mProfilePhoneNumber.setText(phone_number);
                }
                if(!email.equals("default")){
                    mProfileEmail.setText(email);
                }
                if(!date_of_birth.equals("default")){
                    mProfileDateOfBirth.setText(date_of_birth);
                }
                if(!blood_group.equals("default")){
                    mProfileBloodGroup.setText(blood_group);
                }
                if(!bio.equals("default")){
                    mProfileBio.setText(bio);
                }
                if(!location.equals("default")){
                    mProfileLocation.setText(location);
                }

                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                if(thumb_image.equals("default")){
                }
                else{
                    Picasso.with(SettingsActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_blank_profile).into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            //do nothing if an image is found offline
                        }
                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(thumb_image).placeholder(R.drawable.ic_blank_profile).into(mProfileImage);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        mProfilePicEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //using the library of crop image
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

        mProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //i want to store some data before moving on to next page
                DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUserID);
                String deviceTokenID = FirebaseInstanceId.getInstance().getToken();

                String name = mProfileName.getEditableText().toString();
                String phone_number = mProfilePhoneNumber.getEditableText().toString();
                String email  =  mProfileEmail.getEditableText().toString();
                String date_of_birth  =  mProfileDateOfBirth.getEditableText().toString();
                String blood_group =  mProfileBloodGroup.getEditableText().toString();
                String bio = mProfileBio.getEditableText().toString();
                String location = mProfileLocation.getEditableText().toString();
                Map hashMap = new HashMap<>();



                hashMap.put("name"  , name);
                hashMap.put("phone_number" , phone_number);
                hashMap.put("email" , email);
                hashMap.put("bio" , bio);
                hashMap.put("date_of_birth" , date_of_birth);
                hashMap.put("blood_group" , blood_group);
                hashMap.put("location" , location);
                hashMap.put("device_id" , deviceTokenID);


                mDatabaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, "Changes Saved Succesfully !", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(SettingsActivity.this, "Some Error occured!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //showing the progress dialog
            mProgress = new ProgressDialog(SettingsActivity.this);
            mProgress.setTitle("Uploading Image...");
            mProgress.setMessage("please wait while we upload your image");
            mProgress.show();


            if (resultCode == RESULT_OK) {


                imagetUri = result.getUri();
                //compressing image
                Bitmap thumb_bitmap = null;
                File thumb_file = new File(imagetUri.getPath());
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(60)
                            .compressToBitmap(thumb_file);
                }catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                final byte[] thumb_byte = baos.toByteArray();

                //uplaoading real to firebase storage
                StorageReference imageFilePath = mStorage.child("users").child(mUserID).child("Profile").child("profile_images").child(mUserID+".jpg");
                final StorageReference thumbFilePath = mStorage.child("users").child(mUserID).child("Profile").child("thumb_images").child(mUserID+".jpg");

                //প্রথমে মেইন ইমেজ টা আপলোড করবে এবং এটার ডাউনলোড URL টা নিবে । তারপরে থাম্বনিল ইমেজ টা আপলোড করবে এবং এইটার ডাউনলোড URL টা নিবে
                //সর্বশেষে এই দুইটা URl firebase Databse এ আপলোড করে দিবে

                imageFilePath.putFile(imagetUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrlImage = taskSnapshot.getDownloadUrl();
                                final String downLoadUriStringImage  = downloadUrlImage.toString();


                                //uploading the bitmap image
                                UploadTask uploadThumbTask = thumbFilePath.putBytes(thumb_byte);

                                uploadThumbTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        Toast.makeText(SettingsActivity.this, "Some Error occured while uplaoding the image", Toast.LENGTH_SHORT).show();
                                        mProgress.dismiss();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                        Uri downloadUrlThumb = taskSnapshot.getDownloadUrl();
                                        String  downLoadUriStringThumb  = downloadUrlThumb.toString();


                                        //create a new hash map to put the file into firebase database
                                        //এইখানে একটা কথা বলা ভালো যদি আমরা এইখানে HashMap use করি তাইলে কাজ করবেনা কারন সেই ক্ষেত্রে ফায়ারবেজ ডাটা আপডেট করবেনা
                                        //এইখানে Map ইউজ করতে হবে আর setValue() function এর বদলে updateChildren() ব্যাবহার করা লাগবে
                                        Map updateHashmap = new HashMap<>();
                                        updateHashmap.put("image"  , downLoadUriStringImage);
                                        updateHashmap.put("thumb_image" , downLoadUriStringThumb);


                                        mDatabaseReference.updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProgress.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "uploading is successful !", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(SettingsActivity.this, "Some Error Occured!", Toast.LENGTH_SHORT).show();
                                                    mProgress.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                mProgress.dismiss();
                            }
                        });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.child("online").setValue("true");//offline or online
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabaseReference.child("online").setValue(ServerValue.TIMESTAMP);//offline or online
    }

}
