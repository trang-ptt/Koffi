package com.example.koffi.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.koffi.R;
import com.example.koffi.models.Address;
import com.example.koffi.models.Order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Order> orderArray;

    public OrderAdapter(Context context, ArrayList orderArray) {
        this.context = context;
        this.orderArray = orderArray;
    }

    @Override
    public int getCount() {
        return orderArray.size();
    }

    @Override
    public Object getItem(int i) {
        return orderArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.listview_orders,null);

        //Init
        TextView idTxt = view.findViewById(R.id.orderId);
        TextView dateTxt = view.findViewById(R.id.orderDate);
        TextView nameTxt = view.findViewById(R.id.orderName);
        TextView priceTxt = view.findViewById(R.id.orderPrice);
        LinearLayout stateBtn = view.findViewById(R.id.order_stateBtn);
        TextView stateTxt = view.findViewById(R.id.orderStateText);

        //Handle
        //Còn id nữa
        idTxt.setText(orderArray.get(i).orderID);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dateTxt.setText(df.format(orderArray.get(i).date));
        nameTxt.setText(orderArray.get(i).name);
        priceTxt.setText(Long.toString(orderArray.get(i).total)+"đ");
        switch (orderArray.get(i).status) {
            case 1:
                stateBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.primary)));
                stateTxt.setText("Xác nhận");
                break;
            case 2:
                stateBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.koffiOrange)));
                stateTxt.setText("Chuẩn bị xong");
                break;
            case 3:
                stateBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.koffiBlue)));
                stateTxt.setText("Đang giao");
                break;
            case 4:
                stateBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.koffiGreen)));
                stateTxt.setText("Hoàn thành");
                break;
            case 5:
                stateBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.koffiGrey)));
                stateTxt.setText("Đã hủy");
                break;
        }
        return view;
    }

    public void filteredList(ArrayList<Order> filteredList) {
        orderArray = filteredList;
        notifyDataSetChanged();
    }
}
