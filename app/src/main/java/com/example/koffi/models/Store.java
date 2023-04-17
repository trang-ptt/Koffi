package com.example.koffi.models;

public class Store {
    public String id;
    public String address;
    public String image;
    public String phoneNumber;
    public double latitude;
    public double longitude;

    public Store(String id,String address, String image, String phoneNumber,double latitude,double longitude) {
        this.id = id;
        this.address = address;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
