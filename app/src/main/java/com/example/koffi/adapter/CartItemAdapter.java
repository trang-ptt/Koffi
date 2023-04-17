package com.example.koffi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.koffi.R;
import com.example.koffi.models.CartItem;
import com.example.koffi.models.Topping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CartItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CartItem> itemArray;
    private boolean isEditable;

    public CartItemAdapter(Context context, ArrayList itemArray,boolean isEditable) {
        this.context = context;
        this.itemArray = itemArray;
        this.isEditable = isEditable;
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

    FirebaseFirestore db;
    String docID = "";

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.listview_cartitem,null);

        db = FirebaseFirestore.getInstance();
        LinearLayout deleteBtn =  view.findViewById(R.id.cart_deleteBtn);
        if (!isEditable)
            deleteBtn.setVisibility(View.GONE);
        else {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Query query = db.collection("cartItems")
                            .whereEqualTo("cartID", itemArray.get(i).cartID)
                            .whereEqualTo("item", itemArray.get(i).item)
                            .whereEqualTo("size", itemArray.get(i).size)
                            .whereEqualTo("toppings", itemArray.get(i).toppings);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    docID = documentSnapshot.getId();
                                }
                                System.out.println("Doc: " + docID);
                                db.collection("cartItems").document(docID)
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                        itemArray.remove(i);
                                            notifyDataSetChanged();
                                            Toast.makeText(context.getApplicationContext(), "Xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }


        TextView quantityText = view.findViewById(R.id.cart_quantity);
        quantityText.setText(itemArray.get(i).quantity+"x");

        TextView nameText = view.findViewById(R.id.cart_name);
        nameText.setText(itemArray.get(i).item);

        String toppingNote = "";
        for (Topping topping : itemArray.get(i).toppings) {
            toppingNote += ", " + topping.name;
        }
        if (!itemArray.get(i).note.isEmpty())
            toppingNote += ", " + itemArray.get(i).note;
        TextView noteText = view.findViewById(R.id.cart_note);
        noteText.setText(itemArray.get(i).size + toppingNote);

        TextView priceText = view.findViewById(R.id.cart_price);
        priceText.setText(itemArray.get(i).price+"đ");

        return view;
    }
}
