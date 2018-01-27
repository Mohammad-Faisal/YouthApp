package com.example.candor.youthapp;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Mohammad Faisal on 1/28/2018.
 */

public class YouthApp extends Application {

    //offline er jonno lagbe
    DatabaseReference mUserDatabase;
    FirebaseAuth mAuth;
    //end

    String mUserID;


    @Override
    public void onCreate() {
        super.onCreate();
        // FirebaseApp app  = FirebaseApp.initializeApp(Android.App.Candor.Context);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Picasso offline
        File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().cache(cache);
        Picasso.Builder picassoBuilder = new Picasso.Builder(getApplicationContext());
        picassoBuilder.downloader(new OkHttp3Downloader(clientBuilder.build()));
        Picasso picasso = picassoBuilder.build();
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
        }



        //end


        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            mUserID = mAuth.getCurrentUser().getUid();

            //if an user is logged in for the first time i am inserting some default information about him
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(mUserID)){

                    }
                    else{

                        //i want to store some data before moving on to next page
                        DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUserID);
                        String deviceTokenID = FirebaseInstanceId.getInstance().getToken();

                        HashMap<String  , String> hashMap = new HashMap<>();
                        hashMap.put("name"  , "Name");
                        hashMap.put("image" , "default");
                        hashMap.put("thumb_image" , "default");
                        hashMap.put("device_id" , deviceTokenID);
                        hashMap.put("phone_number" , "default");
                        hashMap.put("email" , "default");
                        hashMap.put("date_of_birth" , "default");
                        hashMap.put("blood_group" , "default");
                        hashMap.put("bio" , "default");
                        hashMap.put("location" , "location");



                        mFirebaseDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                }
                                else{
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



      /*  //online or offline feature
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser!=null){
            String mUserID = mAuth.getCurrentUser().getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUserID);
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot !=null){
                        mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
*/
        //online or offline feature offline feature

    }

}