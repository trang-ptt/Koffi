package com.example.koffi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.example.koffi.R;
import com.example.koffi.fragment.staff.OrderDetailFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CancelOrderDialog extends Dialog {

    public Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CancelOrderDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cancel_order);
        EditText lydo=findViewById(R.id.cancel_reason);
        TextView noBtn = findViewById(R.id.cancel_noBtn);
        TextView yesBtn = findViewById(R.id.cancel_yesBtn);

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("order").document(OrderDetailFragment.docID)
                        .update("status",5);
                db.collection("order").document(OrderDetailFragment.docID)
                        .update("deliveryNote",lydo.getText().toString());

                dismiss();

            }
        });
    }


}
