package com.example.candor.youthapp.GENERAL;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.candor.youthapp.COMMUNICATE.CommunicationFragment;
import com.example.candor.youthapp.HOME.HomeFragment;
import com.example.candor.youthapp.MAP.MapsActivity;
import com.example.candor.youthapp.NotificationFragment.NotificationFragment;
import com.example.candor.youthapp.PROFILE.ProfileFragment;
import com.example.candor.youthapp.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 1;


    public static String mUserID = "";
    public static String mUserName = "";
    public static String mUserThumbImage = "";


    //----------- FIREBASE -----//
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    //--------- WIDGETS -----//
    private FrameLayout mFrameHome;
    private BottomNavigationView mNavigation;


    //------------ VARIABLES ----------//


    //------- FRAGMENTS ----//
    private HomeFragment mHomeFragment;
    private CommunicationFragment mCommunicationFragment;
    private ProfileFragment mProfileFragment;
    private NotificationFragment mNotificationFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(mHomeFragment);
                    return true;
                case R.id.navigation_explore:
                    setFragment(mCommunicationFragment);
                    return true;
                case R.id.navigation_location:
                    Intent mapsIntent  = new Intent(MainActivity.this , MapsActivity.class);
                    startActivity(mapsIntent);
                    return true;
                case R.id.navigation_notifications:
                    ShortcutBadger.removeCount(MainActivity.this);
                    setFragment(mNotificationFragment);
                    return true;
                case R.id.navigation_profile:
                    setFragment(mProfileFragment);
                    /*Intent profileIntent = new Intent(MainActivity.this , ProfileActivity.class);
                    profileIntent.putExtra("userID" , mUserID);
                    startActivity(profileIntent);*/
                    //setFragment(mProfileFragment);
                    return true;
            }
            return false;
        }
    };

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(
                R.id.frame_layout_navigation_home , fragment
        );
        fragmentTransaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------- BOTTOM NAVIGATION HANDLING ------//
        mNavigation =findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //------------ FRAME LAYOUT ----------------//
        mFrameHome = findViewById(R.id.frame_layout_navigation_home);



        // ----------- GETTING FRAGMENTS ---//
        mHomeFragment = new HomeFragment();
        setFragment(mHomeFragment);
        mProfileFragment = new ProfileFragment();
        mNotificationFragment = new NotificationFragment();
        mCommunicationFragment = new CommunicationFragment();

        //-------- CHECKING AUTH STATE -------//
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    mUserID = mUser.getUid();
                } else {
                    //user is not signed in
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        int badgeCount = 1;
        ShortcutBadger.applyCount(MainActivity.this, badgeCount); //for 1.1.4+
        //ShortcutBadger.with(getApplicationContext()).count(badgeCount);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(response==null) Log.d(TAG, "onActivityResult: MainActivity");

            // Successfully signed in
            if (resultCode == RESULT_OK && response!=null) {
                mUserID = response.getIdpToken();


                FirebaseUser mUser = mAuth.getCurrentUser();
                if(mUser!=null)
                {

                    mUserID = mAuth.getCurrentUser().getUid();
                    //if an user is logged in for the first time i am inserting some default information about him
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mUserID)){

                            }
                            else{

                                // ----------- STORE SOME DEFAULT DATA ------------//
                                DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUserID);
                                String deviceTokenID = FirebaseInstanceId.getInstance().getToken();

                                HashMap<String  , String> hashMap = new HashMap<>();
                                hashMap.put("name"  , "Name");
                                hashMap.put("id", mUserID);
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
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "no internet Connection :/ ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "\"unknown error ! :( \"", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "unknown sign in response", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
