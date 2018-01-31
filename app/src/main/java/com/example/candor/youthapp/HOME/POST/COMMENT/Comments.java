package com.example.candor.youthapp.HOME.POST.COMMENT;

import org.w3c.dom.Comment;

/**
 * Created by Mohammad Faisal on 1/29/2018.
 */

public class Comments {

    private String comment;
    private String uid;
    private String postID;
    private String notificationID;
    private String time_stamp;

    public Comments(){}

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public Comments(String comment, String uid , String postID , String notificationID , String time_stamp) {
        this.comment = comment;
        this.uid = uid;
        this.postID = postID;
        this.notificationID  = notificationID;
        this.time_stamp = time_stamp;


    }


    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUid() {

        return uid;

    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {

        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
