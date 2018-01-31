package com.example.candor.youthapp.HOME.POST.LIKE;

/**
 * Created by Mohammad Faisal on 1/30/2018.
 */

public class Likes {
    String Uid;
    String notificationID;

    public Likes() {}

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public Likes(String uid , String notificationID) {
        this.Uid = uid;
        this.notificationID = notificationID;

    }

    public String getUid() {

        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
