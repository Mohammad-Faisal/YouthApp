package com.example.candor.youthapp.HOME.POST;

/**
 * Created by Mohammad Faisal on 1/21/2018.
 */

public class Posts {


    private String uid;
    private String time_and_date;
    private String caption;
    private String post_image_url;
    private String like_cnt;
    private String location;
    private String post_push_id;
    private long timestamp;

    public Posts() {
    }

    public String getPost_push_id() {
        return post_push_id;
    }

    public void setPost_push_id(String post_push_id) {
        this.post_push_id = post_push_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Posts(String uid, String time_and_date, String caption, String post_image_url, String like_cnt, String  location , String post_push_id , long timestamp) {

        this.uid = uid;
        this.time_and_date = time_and_date;
        this.caption = caption;
        this.post_image_url = post_image_url;
        this.like_cnt = like_cnt;
        this.location = location;
        this.post_push_id = post_push_id;
        this.timestamp = timestamp;

    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime_and_date() {
        return time_and_date;
    }

    public void setTime_and_date(String time_and_date) {
        this.time_and_date = time_and_date;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPost_image_url() {
        return post_image_url;
    }

    public void setPost_image_url(String post_image_url) {
        this.post_image_url = post_image_url;
    }

    public String getLike_cnt() {
        return like_cnt;
    }

    public void setLike_cnt_cnt(String like_cnt) {
        this.like_cnt = like_cnt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
