package com.example.koffi.models;

import java.util.Date;

public class Order implements Comparable<Order>{
    public Order(String orderID, String userID, String name, String storeID, Date date, int status, String address, String phoneNumber, long subtotal, long ship, long total, String deliveryNote, int method) {
        this.orderID = orderID;
        this.userID = userID;
        this.name = name;
        this.storeID = storeID;
        this.date = date;
        this.status = status;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.subtotal = subtotal;
        this.ship = ship;
        this.total = total;
        this.deliveryNote = deliveryNote;
        this.method = method;
    }

    public String orderID;
    public String userID;
    public String name;
    public String storeID;
    public Date date;
    public int status; //0: cart, 1: ordered, 2: confirmed, 3: served, 4: delivered, 5: canceled
    public String address;
    public String phoneNumber;
    public long subtotal;
    public long ship;
    public long total;
    public String deliveryNote;
    public int method; //0: delivery, 1: takeaway
    public Date confirmTime;
    public Date serveTime;
    public Date deliTime;
    public Date cancelTime;

    public Order(String userID, String name, String storeID, Date date, int status, String address,
                 String phoneNumber, long subtotal, long ship, long total, String deliveryNote, int method) {
        this.userID = userID;
        this.name = name;
        this.storeID = storeID;
        this.date = date;
        this.status = status;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.subtotal = subtotal;
        this.ship = ship;
        this.total = total;
        this.deliveryNote = deliveryNote;
        this.method = method;
    }

    public Order(String userID, int status) {
        this.userID = userID;
        this.status = status;
    }

    @Override
    public int compareTo(Order order) {
        return this.date.compareTo(order.date);
    }
}
