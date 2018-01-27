package com.example.candor.youthapp.HOME.BLOG;

/**
 * Created by Mohammad Faisal on 1/21/2018.
 */

public class Blogs {

    private String uid;
    private String time_and_date;
    private String title;
    private String blog_image_url;
    private String thumbs_up_cnt;
    private String description;
    private String blog_push_id;

    public Blogs(){

    }

    public Blogs(String uid, String time_and_date, String title, String blog_image_url, String thumbs_up_cnt, String description  , String blog_push_id) {
        this.uid = uid;
        this.time_and_date = time_and_date;
        this.title = title;
        this.blog_image_url = blog_image_url;
        this.thumbs_up_cnt = thumbs_up_cnt;
        this.blog_push_id = blog_push_id;
        this.description = description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBlog_image_url() {
        return blog_image_url;
    }

    public void setBlog_image_url(String post_image_url) {
        this.blog_image_url = post_image_url;
    }

    public String getThumbs_up_cnt() {
        return thumbs_up_cnt;
    }

    public void setThumbs_up_cnt(String thumbs_up_cnt) {
        this.thumbs_up_cnt = thumbs_up_cnt;
    }

    public String getBlog_push_id() {
        return blog_push_id;
    }

    public void setBlog_push_id(String post_push_id) {
        this.blog_push_id = post_push_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
