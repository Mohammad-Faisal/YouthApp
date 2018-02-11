package com.example.candor.youthapp.MAP;

import com.firebase.ui.auth.User;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mohammad Faisal on 2/10/2018.
 */

public class UserLocation {
    private double lat;
    private double lng;
    private String userID;

    public UserLocation(){};

    public UserLocation(double lat, double lng, String userID) {
        this.lat = lat;
        this.lng = lng;
        this.userID = userID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
