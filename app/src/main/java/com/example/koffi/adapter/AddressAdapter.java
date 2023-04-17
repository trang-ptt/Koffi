package com.example.koffi.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.koffi.R;
import com.example.koffi.fragment.other.AddressFragment;
import com.example.koffi.models.Address;

import java.util.ArrayList;

public class AddressAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Address> addressArray;

    public AddressAdapter(Context context, ArrayList addressArray) {
        this.context = context;
        this.addressArray = addressArray;
    }

    @Override
    public int getCount() {
        return addressArray.size();
    }

    @Override
    public Object getItem(int i) {
        return addressArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.listview_address,null);
        String docId= AddressFragment.idListAddress.get(i);
        TextView name = view.findViewById(R.id.address_nameText);
        name.setText(addressArray.get(i).name);

        TextView address = view.findViewById(R.id.address_addressText);
        address.setText(addressArray.get(i).address);
        ImageView img=view.findViewById(R.id.edit_address);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("type","editAddress");
                bundle.putString("doc",docId);
                Navigation.findNavController(view).navigate(R.id.action_addressFragment2_to_addAddressFragment,bundle);
            }
        });
        return view;
    }
}