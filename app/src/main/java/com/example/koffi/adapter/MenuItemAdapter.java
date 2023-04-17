package com.example.koffi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.koffi.R;
import com.example.koffi.models.Item;

import java.util.ArrayList;

public class MenuItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> itemArray;

    public MenuItemAdapter(Context context, ArrayList itemArray) {
        this.context = context;
        this.itemArray = itemArray;
    }

    @Override
    public int getCount() {
        return itemArray.size();
    }

    @Override
    public Object getItem(int i) {
        return itemArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.listview_menu_item,null);

        TextView nameText = view.findViewById(R.id.itemName);
        nameText.setText(itemArray.get(i).name);

        TextView priceText = view.findViewById(R.id.priceText);
        priceText.setText(itemArray.get(i).price+"đ");

        ImageView itemImage = view.findViewById(R.id.itemImage);
        int drawableId = view.getResources().getIdentifier(itemArray.get(i).image, "drawable", context.getPackageName());
        itemImage.setImageResource(drawableId);

        /*ImageButton addBtn = view.findViewById(R.id.addBtn);
        view.setClickable(false);
        addBtn.setClickable(false);*/

        return view;
    }
}
