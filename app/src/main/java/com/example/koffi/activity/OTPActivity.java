package com.example.koffi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koffi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    EditText input1, input2, input3, input4, input5, input6;
    TextView mTextField;
    ProgressBar progressBar;
    String verificationId;
    TextView resend;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("(" + millisUntilFinished / 1000 + "s)");
                resend.setVisibility(View.GONE);
            }

            public void onFinish() {
                mTextField.setText("");
                resend.setVisibility(View.VISIBLE);
            }

        }.start();
        mTextField = findViewById(R.id.timer);
        TextView mobileText = findViewById(R.id.textViewMobile);
        String mobile = getIntent().getStringExtra("mobile");
        mobileText.setText("Một mã xác thực gồm 6 số đã được gửi đến số điện thoại "
        + String.format("+84-%s", mobile));
        ImageButton close = findViewById(R.id.otp_closeBtn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OTPActivity.this, LoginActivity.class));
            }
        });
        LinearLayout layout = findViewById(R.id.linearLayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllTextChangedListener();
                setAllKeyListener();
            }
        });
        progressBar = findViewById(R.id.progress_bar);
        verificationId = getIntent().getStringExtra("verificationId");
        resend = findViewById(R.id.textResend);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+84" + mobile,
                        60, TimeUnit.SECONDS, OTPActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTPActivity.this, "Xác thực đăng nhập thất bại", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = s;
                                Toast.makeText(OTPActivity.this, "Đã gửi mã xác nhận, vui lòng chờ trong giây lát", Toast.LENGTH_LONG).show();
                                super.onCodeSent(s, forceResendingToken);
                                new CountDownTimer(60000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        mTextField.setText("(" + millisUntilFinished / 1000 + "s)");
                                        resend.setVisibility(View.GONE);
                                    }

                                    public void onFinish() {
                                        mTextField.setText("");
                                        resend.setVisibility(View.VISIBLE);
                                    }

                                }.start();
                            }
                        });

            }
        });

        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        input3 = findViewById(R.id.input3);
        input4 = findViewById(R.id.input4);
        input5 = findViewById(R.id.input5);
        input6 = findViewById(R.id.input6);
        setupOTPInputs();
    }

    private void setupOTPInputs() {
        initFocus();

        setAllTextChangedListener();

        setAllKeyListener();
    }
    private void initFocus() {
        input1.setEnabled(true);
        input1.requestFocus();
        input2.setEnabled(false);
        input3.setEnabled(false);
        input4.setEnabled(false);
        input5.setEnabled(false);
        input6.setEnabled(false);
    }
    private void setAllKeyListener() {
        setKeyListener(input6, input5);
        setKeyListener(input5, input4);
        setKeyListener(input4, input3);
        setKeyListener(input3, input2);
        setKeyListener(input2, input1);
    }
    private void setAllTextChangedListener() {
        setTextChanged(input1, input2);
        setTextChanged(input2, input3);
        setTextChanged(input3, input4);
        setTextChanged(input4, input5);
        setTextChanged(input5, input6);
        textFilled(input6);
    }
    private void setTextChanged(EditText from, EditText to) {
        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    to.setEnabled(true);
                    to.requestFocus();
                    from.clearFocus();
                    from.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void textFilled(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setEnabled(false);
                OTPVerify();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void setKeyListener(EditText from, EditText back) {
        from.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    back.setEnabled(true);
                    back.requestFocus();
                    back.setText("");
                    from.clearFocus();
                    from.setEnabled(false);
                }
                return false;
            }
        });
    }
    private void reset() {
        input1.setEnabled(false);
        input2.setEnabled(false);
        input3.setEnabled(false);
        input4.setEnabled(false);
        input5.setEnabled(false);
        input6.setEnabled(false);

        input1.setText("");
        input2.setText("");
        input3.setText("");
        input4.setText("");
        input5.setText("");
        input6.setText("");

        initFocus();
    }
    private void OTPVerify() {
        if (!input1.getText().toString().isEmpty() && !input2.getText().toString().isEmpty() &&
                !input3.getText().toString().isEmpty() && !input4.getText().toString().isEmpty() &&
                !input5.getText().toString().isEmpty() && !input6.getText().toString().isEmpty()) {
            String otp = input1.getText().toString() + input2.getText().toString() + input3.getText().toString() +
                    input4.getText().toString() + input5.getText().toString() + input6.getText().toString();
            if (verificationId != null) {
                progressBar.setVisibility(View.VISIBLE);
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otp);
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            Toast.makeText(OTPActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(OTPActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(OTPActivity.this, "Mã xác thực không hợp lệ hoặc quá hiệu lực! Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                            reset();
                        }
                    }
                });
            }
        }

    }
}