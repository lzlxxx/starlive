package com.starlive.org.pojo;


public class Location {
    // Getters 和 Setters
    private double latitude;
    private double longitude;

    // 构造器
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
