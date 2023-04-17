package com.example.koffi.models;

import java.util.ArrayList;

public class Category {
    public String id;
    public String name;
    public String image;
    public ArrayList<Item> items;

    public Category(String id, String name, String image, ArrayList<Item> items) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.items = items;
    }
}
