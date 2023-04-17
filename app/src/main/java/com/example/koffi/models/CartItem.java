package com.example.koffi.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CartItem implements Parcelable {


    public String cartID;
    public String item;
    public int quantity;
    public Long price;
    public String size;
    public ArrayList<Topping> toppings;
    public String note;

    public CartItem(String cartID, String item, int quantity, Long price, String size, ArrayList<Topping> toppings, String note) {
        this.cartID = cartID;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.size = size;
        this.toppings = toppings;
        this.note = note;
    }
    public CartItem() {
    }



    protected CartItem(Parcel in) {
        cartID = in.readString();
        item = in.readString();
        quantity = in.readInt();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readLong();
        }
        size = in.readString();
        toppings = in.createTypedArrayList(Topping.CREATOR);
        note = in.readString();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cartID);
        parcel.writeString(item);
        parcel.writeInt(quantity);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(price);
        }
        parcel.writeString(size);
        parcel.writeTypedList(toppings);
        parcel.writeString(note);
    }
}
