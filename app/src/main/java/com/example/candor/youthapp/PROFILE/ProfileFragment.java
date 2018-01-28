package com.example.candor.youthapp.PROFILE;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.candor.youthapp.CHAT.ChatActivity;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {



    //widgets
    private ImageView mProfileBlurImage , mProfileImage;
    private ImageButton mProfileImageChangeButton;
    private TextView mProfileName , mProfileLocation , mProfilePhoneNumber , mProfileEmail , mProfileBloodGroup , mProfileDateOfBirth , mProfileBio ;
    private Button mProfileEditButton;
    private EditText mProfilePhoneNumberEdit;

    //toolbar
    private ProgressDialog mProgress;
    ProgressDialog mProgressDialog;

    //variables
    private String mUserID;
    private String mCurrentUserID;


    //firebase
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        //firebase
        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserID=mCurrentUserID;
        //mCurrentUserID = getIntent().getStringExtra("userID"); //jar profile e amra dhuksi
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUserID);
        mStorage = FirebaseStorage.getInstance().getReference();


        //widgets
        mProfileName = view.findViewById(R.id.profile_name);
        mProfileLocation  = view.findViewById(R.id.profile_work);
        mProfilePhoneNumber = view.findViewById(R.id.profile_mobile_no);
        mProfileEmail  = view.findViewById(R.id.profile_email);
        mProfileDateOfBirth = view.findViewById(R.id.profile_date_of_birth);
        mProfileBloodGroup = view.findViewById(R.id.profile_blood_group);
        mProfileBio = view.findViewById(R.id.profile_bio);


        //mProfilePhoneNumberEdit = findViewById(R.id.profile_mobile_no_edit);



        mProfileImage = view.findViewById(R.id.profile_image);
        mProfileBlurImage = view.findViewById(R.id.profile_blur_image);
        mProfileImageChangeButton = view.findViewById(R.id.profile_image_change_button);
        mProfileEditButton = view.findViewById(R.id.profile_edit_button);


        mProfileImageChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent  = new Intent(getContext(), ChatActivity.class);
                chatIntent.putExtra("userID" , mCurrentUserID );
                startActivity(chatIntent);
            }
        });


       /* mProfileImageChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //using the library of crop image
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(ProfileActivity.this);
            }
        });*/

        mProfileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent (getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });


        if(mUserID.equals(mCurrentUserID)){//own id
            mProfileImageChangeButton.setVisibility(View.GONE);
            mProfileEditButton.setVisibility(View.VISIBLE);
        }
        else{
            mProfileImageChangeButton.setVisibility(View.VISIBLE);
            mProfileEditButton.setVisibility(View.GONE);
        }


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

                mProfileName.setText(name);
                mProfileLocation.setText(location);
                mProfilePhoneNumber.setText(phone_number);
                mProfileEmail.setText(email);
                mProfileDateOfBirth.setText(date_of_birth);
                mProfileBloodGroup.setText(blood_group);
                mProfileBio.setText(bio);


                final String image = dataSnapshot.child("image").getValue().toString();
                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                if(image.equals("default")){
                }
                else{
                    Picasso.with(getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_blank_profile).into(mProfileBlurImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            //do nothing if an image is found offline
                        }
                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(image).placeholder(R.drawable.ic_blank_profile).into(mProfileBlurImage);
                        }
                    });

                    Picasso.with(getContext()).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_blank_profile).into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            //do nothing if an image is found offline
                        }
                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(thumb_image).placeholder(R.drawable.ic_blank_profile).into(mProfileImage);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





        return view;
    }



    @Override
     public void onStart() {
        super.onStart();
    };

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //showing the progress dialog
            mProgress = new ProgressDialog(getContext());
            mProgress.setTitle("Uploading Image...");
            mProgress.setMessage("please wait while we upload your image");
            mProgress.show();


            if (resultCode == RESULT_OK) {
                Uri imagetUri = result.getUri();
                //compressing image
                Bitmap thumb_bitmap = null;
                File thumb_file = new File(imagetUri.getPath());
                try {
                    thumb_bitmap = new Compressor(getContext())
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
                                        //Toast.makeText(ProfileActivity.this, "Some Error occured while uplaoding the image", Toast.LENGTH_SHORT).show();
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
                                                    //Toast.makeText(getContext(), "uploading is successful !", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    //Toast.makeText(getContext(), "Some Error Occured!", Toast.LENGTH_SHORT).show();
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

}
