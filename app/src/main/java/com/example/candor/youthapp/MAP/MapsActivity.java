package com.example.candor.youthapp.MAP;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.candor.youthapp.GENERAL.MainActivity;
import com.example.candor.youthapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    //------- MAP RELATED STUFF -//
    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private LocationRequest mLocationRequest;
    private Marker mCurrentLocationMarker;
    private Location mLastLocation;
    private View mMapVIew;
    public static final int REQUEST_LOCATION_CODE = 99;
    private String mSelectedTag = "all";
    boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;


    //- widgets -----//
    private ImageButton mSearchButton;
    private EditText mSearchText;
    private Spinner map_spinner;

    // ------- FIREBASE ----//
    DatabaseReference mRootRef;
    private String mUserID, mUserName;
    int flag = 0;


    ArrayList<UserLocation> mUserLocationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //--widgets
        mSearchButton = findViewById(R.id.map_search_button);
        mSearchText = findViewById(R.id.map_search_text);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mLocationPermissionGranted = checkLocaitonPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapVIew = mapFragment.getView();
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //firebase
        mUserID = MainActivity.mUserID;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("map_locations").keepSynced(true);
        mRootRef.child("map_locations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserLocation t = dataSnapshot.getValue(UserLocation.class);
                LatLng mLatLng  = new LatLng(t.getLat() ,  t.getLng());
                MarkerOptions mMarkerOptions = new MarkerOptions();
                mMarkerOptions.position(mLatLng);
                mMarkerOptions.title(dataSnapshot.child("userName").getValue().toString());
                mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                //mMap.addMarker(mMarkerOptions);

                mCurrentLocationMarker = mMap.addMarker(mMarkerOptions);
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
        });


        //for spinner
        addListenerOnSpinnerItemSelection();


    }


    public void addListenerOnSpinnerItemSelection() {
        map_spinner = findViewById(R.id.map_spinner);
        map_spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mClient == null) {
                            buildGoogleApiCLient();
                            ;
                        }
                        mMap.setMyLocationEnabled(true);
                        mLocationPermissionGranted = true;
                    } else {
                        Toast.makeText(this, "Permission Denied !", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


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
                            // Logic to handle location object
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();
                            mMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                            //pushing this data to firebase
                            UserLocation mUserLocation = new UserLocation(location.getLatitude() , location.getLongitude() , mUserID, MainActivity.mUserName);
                            mRootRef.child("map_locations").child(mUserID).setValue(mUserLocation);
                        }
                    }
                });

        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiCLient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
        }

        if (mMapVIew != null &&
                mMapVIew.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mMapVIew.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }

    protected synchronized void buildGoogleApiCLient() {
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mClient.connect();
        flag= 1;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, this);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean checkLocaitonPermission(){
        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , REQUEST_LOCATION_CODE);
            }else{
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , REQUEST_LOCATION_CODE);
            }
            return false;
        }else{

            return true;
        }
    }

    public void onClick(View v)
    {
        if(v.getId() == R.id.map_search_button)
        {
            String place = mSearchText.getText().toString();
            List <Address> mAddressList = null;
            if(!place.equals(""))
            {
                Geocoder mGeoCoder = new Geocoder(this);
                try {
                    mAddressList = mGeoCoder.getFromLocationName(place , 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            LatLng latlng = new LatLng(mAddressList.get(0).getLatitude() , mAddressList.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.animateCamera(CameraUpdateFactory.zoomBy(15));

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(mCurrentLocationMarker!=null){
            mCurrentLocationMarker.remove();
        }
        //pushing this data to firebase
        String userName = MainActivity.mUserName;
        UserLocation mUserLocation = new UserLocation(location.getLatitude() , location.getLongitude() , mUserID, userName);
        mRootRef.child("map_locations").child(mUserID).setValue(mUserLocation);


        LatLng mLatLng  = new LatLng(location.getLatitude() ,  location.getLongitude());
        MarkerOptions mMarkerOptions = new MarkerOptions();
        mMarkerOptions.position(mLatLng);
        mMarkerOptions.title(MainActivity.mUserName);
        mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        //mMap.addMarker(mMarkerOptions);

        mCurrentLocationMarker = mMap.addMarker(mMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(15));

        if(mClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mClient  , this);
        }
    }
}


class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}

