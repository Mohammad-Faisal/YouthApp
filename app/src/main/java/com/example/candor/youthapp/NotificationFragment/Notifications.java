package com.example.candor.youthapp.NotificationFragment;

/**
 * Created by Mohammad Faisal on 1/30/2018.
 */

public class Notifications {

    private String type;
    private String user_id;
    private String content_id;
    private String time_stamp;
    private String seen;

    public Notifications(){}

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public Notifications(String type, String user_id, String content_id, String time_stamp , String seen) {

        this.type = type;
        this.user_id = user_id;
        this.content_id = content_id;
        this.time_stamp = time_stamp;
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
