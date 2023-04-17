package com.example.koffi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.koffi.R;
import com.example.koffi.models.Category;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    Context context;
    ArrayList<Category> menuArray;

    public CategoryAdapter(Context context, ArrayList<Category> menuArray) {
        this.context = context;
        this.menuArray = menuArray;
    }

    @Override
    public int getCount() {
        if (!menuArray.isEmpty()) {
            return menuArray.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return menuArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.category_gridview_item,null);

        TextView categoryName = view.findViewById(R.id.categoryName);
        categoryName.setText(menuArray.get(i).name);

        ImageView categoryImage = view.findViewById(R.id.categoryImage);
        int drawableId = view.getResources().getIdentifier(menuArray.get(i).image, "drawable", context.getPackageName());
        categoryImage.setImageResource(drawableId);

        return view;
    }
}
