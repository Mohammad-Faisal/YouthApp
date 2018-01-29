package com.example.candor.youthapp.HOME.POST;

import org.w3c.dom.Comment;

/**
 * Created by Mohammad Faisal on 1/29/2018.
 */

public class Comments {

    private String comment;
    private String uid;

    public Comments(){}

    public Comments(String comment, String uid) {
        this.comment = comment;
        this.uid = uid;
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
