package com.example.koffi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.koffi.R;
import com.example.koffi.models.Store;

import java.util.ArrayList;

public class StoreAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Store> storeArray;

    public StoreAdapter(Context context, ArrayList storeArray) {
        this.context = context;
        this.storeArray = storeArray;
    }

    @Override
    public int getCount() {
        return storeArray.size();
    }
    public void filteredList (ArrayList<Store> filteredList){
        storeArray = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int i) {
        return storeArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.store_listitem,null);

        TextView addressText = (TextView) view.findViewById(R.id.address);
        addressText.setText(storeArray.get(i).address);
        ImageView storeImage = view.findViewById(R.id.storeImage);
        int drawableId = view.getResources().getIdentifier(storeArray.get(i).image, "drawable", context.getPackageName());
        storeImage.setImageResource(drawableId);

        return view;
    }
}
