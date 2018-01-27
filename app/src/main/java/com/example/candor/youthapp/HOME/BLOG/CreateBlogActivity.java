package com.example.candor.youthapp.HOME.BLOG;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.candor.youthapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class CreateBlogActivity extends AppCompatActivity {




    //---------VARIABLES ----//
    private String mUserID;
    private String image_download_url;
    private Uri selectedImageUri;



    //---------FIREBASE-----//
    private DatabaseReference mPostsDatabaseRef , mRootRef;
    private FirebaseUser mUser;
    private StorageReference mPostsStorageRef;


    //-----WIDGETS ---//
    private Button mPostButton;
    private ImageButton mCameraPickButton , mGalleryPickButton;
    private EditText mTitleSettingText , mDescriptionSettingText;
    private ImageView mImageVIew;
    private ProgressDialog mProgress;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blog);


        //------WIDGETS ---//
        mPostButton = findViewById(R.id.create_blog_post_button);
        mCameraPickButton = findViewById(R.id.create_blog_image_button);
        mTitleSettingText = findViewById(R.id.create_blog_title);
        mDescriptionSettingText = findViewById(R.id.create_blog_description);
        mImageVIew = findViewById(R.id.create_blog_image_view);


        //----- FIREBASE -------//
        mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPostsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("blogs");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostsStorageRef = FirebaseStorage.getInstance().getReference().child("blogs");


        mCameraPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(5,3)
                        .setAllowRotation(true)
                        .start(CreateBlogActivity.this);
            }
        });

        //starting m post button
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                //-----GETTING CAPTION ----//
                final String title  = mDescriptionSettingText.getText().toString()+"\n";
                //-----GETTNG TIME AND DATE ----//
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a MMM d, ''yy");
                final String cur_time_and_date = sdf.format(c.getTime());
                //------GETTING LOCATION----//
                //final String location = mLocationSettingText.getText().toString();

                if(selectedImageUri!=null){


                    mProgress = new ProgressDialog(CreateBlogActivity.this);
                    mProgress.setTitle("Posting .....");
                    mProgress.setMessage("please wait while we upload your post");
                    mProgress.show();


                    StorageReference imageFilePath = mPostsStorageRef.child(mUserID).child(mUserID+".jpg");
                    imageFilePath.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                Uri downloadUrlImage = task.getResult().getDownloadUrl();
                                final String image_download_url  = downloadUrlImage.toString();
                                //--------IMAGE UPLOADING IS DONE -----//
                                final String post_push_id = mRootRef.child("blogs").push().getKey();
                                final Blogs blog = new Blogs(mUserID, cur_time_and_date ,title ,image_download_url,"0","Dhaka , Bangladesh",post_push_id );
                                mRootRef.child("blogs").child(post_push_id).setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CreateBlogActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                        mProgress.dismiss();
                                        mRootRef.child("blogs").child(post_push_id).child("timestamp").setValue(-1 * new Date().getTime());
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CreateBlogActivity.this, "Some Error Occured !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                Toast.makeText(CreateBlogActivity.this, "Error while uploading image !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(CreateBlogActivity.this, "You need to select an image :)", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //end of m post button
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imagetUri = result.getUri();
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
                selectedImageUri = imagetUri;
                mImageVIew.setImageURI(imagetUri);

                StorageReference imageFilePath = mPostsStorageRef.child(mUserID).child(mUserID+".jpg");
                imageFilePath.putFile(imagetUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Uri downloadUrlImage = task.getResult().getDownloadUrl();
                            final String downLoadUriStringImage  = downloadUrlImage.toString();
                            image_download_url = downLoadUriStringImage;
                        }
                        else{
                            Toast.makeText(CreateBlogActivity.this, "Error !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }

}
