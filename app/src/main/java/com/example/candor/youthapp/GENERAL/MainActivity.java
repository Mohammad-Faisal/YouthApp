package com.example.candor.youthapp.GENERAL;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.candor.youthapp.COMMUNICATE.CommunicationFragment;
import com.example.candor.youthapp.HOME.HomeFragment;
import com.example.candor.youthapp.HOME.POST.LOAD.NewsFeedFragment;
import com.example.candor.youthapp.MAP.MapsActivity;
import com.example.candor.youthapp.MAP.UserLocation;
import com.example.candor.youthapp.NotificationFragment.NotificationFragment;
import com.example.candor.youthapp.PROFILE.ProfileFragment;
import com.example.candor.youthapp.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private NewsFeedFragment mNewsFeedFragment;;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(mNewsFeedFragment);
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
       // mHomeFragment = new HomeFragment();
        mProfileFragment = new ProfileFragment();
        mNotificationFragment = new NotificationFragment();
        mCommunicationFragment = new CommunicationFragment();
        mNewsFeedFragment = new NewsFeedFragment();
        setFragment(mNewsFeedFragment);


        //---------- CHECKING THE PERMISSION FOR LOCATION ----//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }

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



        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //  return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            String userName = mUserName;
                            UserLocation mUserLocation = new UserLocation(location.getLatitude() , location.getLongitude() , mUserID, userName);
                            mRootRef.child("map_locations").child(mUserID).setValue(mUserLocation);
                        }
                    }
                });


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

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }
}
