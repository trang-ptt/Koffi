package com.example.koffi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.koffi.R;
import com.example.koffi.models.Topping;

import java.util.ArrayList;

public class ToppingAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<Topping> toppingArray;

    public ToppingAdapter(Context context, ArrayList<Topping> toppingArray) {
        this.context = context;
        this.toppingArray = toppingArray;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getCount() {
        return toppingArray.size();
    }

    @Override
    public Object getItem(int i) {
        return toppingArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.listview_topping,null);

        CheckBox checkBox = view.findViewById(R.id.checkBox);
        checkBox.setText(toppingArray.get(i).name);
        TextView price = view.findViewById(R.id.toppingPrice);
        price.setText(toppingArray.get(i).price + "đ");


        return view;
    }

}
