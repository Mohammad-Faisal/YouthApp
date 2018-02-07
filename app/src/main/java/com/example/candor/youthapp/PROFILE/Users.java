package com.example.candor.youthapp.PROFILE;

/**
 * Created by Mohammad Faisal on 2/5/2018.
 */

public class Users {
    private String name;
    private String location;
    private String image;
    private  String thumb_image;

    public Users() {
    }

    private String bio;
    private String blood_group;
    private String date_of_birth;
    private String device_id;
    private String email;
    private  String phone_number;
    private  long online;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public long getOnline() {
        return online;
    }

    public void setOnline(long online) {
        this.online = online;
    }

    public Users(String name, String location, String image, String thumb_image, String bio, String blood_group, String date_of_birth, String device_id, String email, String phone_number, long online) {

        this.name = name;
        this.location = location;
        this.image = image;
        this.thumb_image = thumb_image;
        this.bio = bio;
        this.blood_group = blood_group;
        this.date_of_birth = date_of_birth;
        this.device_id = device_id;
        this.email = email;
        this.phone_number = phone_number;
        this.online = online;
    }
}
