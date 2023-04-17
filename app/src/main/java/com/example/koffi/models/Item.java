package com.example.koffi.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    public String id;
    public String name;
    public String image;
    public Long price;
    public String description;

    public Item() {
    }

    public Item(String id, String name, String image, Long price, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
    }

    protected Item(Parcel in) {
        id = in.readString();
        name = in.readString();
        image = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readLong();
        }
        description = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(image);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(price);
        }
        parcel.writeString(description);
    }
}
