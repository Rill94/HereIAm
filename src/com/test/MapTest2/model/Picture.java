package com.test.MapTest2.model;

/**
 * Created by Valerie on 17.07.2014.
 */
public class Picture {

    float lat;
    float log;
    String date;
    String time;

    public Picture(){}
    public Picture(float log, float lat, String date, String time)
    {
        this.log = log;
        this.lat = lat;
        this.date = date;
        this.time = time;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLog() {
        return log;
    }

    public void setLog(float log) {
        this.log = log;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
