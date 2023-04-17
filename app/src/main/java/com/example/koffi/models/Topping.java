package com.example.koffi.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Topping implements Parcelable {
    public Topping(String id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Topping() {
    }

    public String id;
    public String name;
    public long price;

    protected Topping(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readLong();
    }

    public static final Creator<Topping> CREATOR = new Creator<Topping>() {
        @Override
        public Topping createFromParcel(Parcel in) {
            return new Topping(in);
        }

        @Override
        public Topping[] newArray(int size) {
            return new Topping[size];
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
        parcel.writeLong(price);
    }
}
