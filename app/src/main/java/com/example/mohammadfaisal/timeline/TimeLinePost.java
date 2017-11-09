package com.example.mohammadfaisal.timeline;

/**
 * Created by Mohammad Faisal on 11/8/2017.
 */

public class TimeLinePost {

    private String name;
    private String uid;
    private String time_and_date;
    private String Caption;
    private String image_url;
    private int thumbs_up_cnt;
    private int thumbs_down_cnt;


    public TimeLinePost() {
    }

    public TimeLinePost(String name, String uid, String time_and_date, String caption, String image_url, int thumbs_up_cnt, int thumbs_down_cnt) {
        this.name = name;
        this.time_and_date = time_and_date;
        Caption = caption;
        this.uid = uid;
        this.image_url = image_url;
        this.thumbs_up_cnt = thumbs_up_cnt;
        this.thumbs_down_cnt = thumbs_down_cnt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime_and_date() {
        return time_and_date;
    }

    public void setTime_and_date(String time_and_date) {
        this.time_and_date = time_and_date;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getThumbs_up_cnt() {
        return thumbs_up_cnt;
    }

    public void setThumbs_up_cnt(int thumbs_up_cnt) {
        this.thumbs_up_cnt = thumbs_up_cnt;
    }

    public int getThumbs_down_cnt() {
        return thumbs_down_cnt;
    }

    public void setThumbs_down_cnt(int thumbs_down_cnt) {
        this.thumbs_down_cnt = thumbs_down_cnt;
    }
}
