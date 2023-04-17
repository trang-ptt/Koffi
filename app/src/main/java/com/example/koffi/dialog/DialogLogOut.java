package com.example.koffi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.koffi.R;
import com.google.firebase.auth.FirebaseAuth;

public class DialogLogOut extends Dialog {
    public Context context;

    public DialogLogOut(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_logout);

        TextView cancelBtn = findViewById(R.id.logout_cancelBtn);
        TextView logoutBtn = findViewById(R.id.logout_logoutBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }
}
