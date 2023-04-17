package com.example.koffi.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koffi.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    EditText edtEmail;
    EditText edtPass;
    Button staffLogin;
    ImageButton closeStaffLogin;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        auth = FirebaseAuth.getInstance();
        userLogin();
    }

    private void userLogin() {
        String regexStr = "^[+]?[0-9]{9,13}$";
        EditText editText = findViewById(R.id.editText);
        Button loginBtn = findViewById(R.id.loginBtn_login);
        ImageButton loginClose = findViewById(R.id.login_closeBtn);
        Button ggBtn = findViewById(R.id.googleBtn);
        Button fbBtn = findViewById(R.id.facebookBtn);
        ProgressBar progressBar = findViewById(R.id.login_progress_bar);
        TextView staffLogin = findViewById(R.id.textViewStaffLogin);
        staffLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.fragment_staff_login);
                staffLogin();
            }
        });

        loginClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        loginBtn.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().toString().length() >= 9 && editText.getText().toString().length() <= 13
                        && editText.getText().toString().matches(regexStr)) {
                    loginBtn.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.purple_200));
                    loginBtn.setEnabled(true);
                } else {
                    loginBtn.setEnabled(false);
                    loginBtn.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.disable));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.INVISIBLE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+84" + editText.getText().toString(),
                        60, TimeUnit.SECONDS, LoginActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.INVISIBLE);
                                loginBtn.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                loginBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Xác thực đăng nhập thất bại", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.INVISIBLE);
                                loginBtn.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                                intent.putExtra("mobile", editText.getText().toString());
                                intent.putExtra("verificationId", s);
                                startActivity(intent);
                                super.onCodeSent(s, forceResendingToken);
                            }
                        });

            }
        });
        createRequest();
        ggBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, FacebookAuthActivity.class));
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Toast.makeText(this, "Đã đăng nhập, vui lòng đăng xuất để đăng nhập tài khoản khác!", Toast.LENGTH_LONG).show();
        }
    }

    private void createRequest() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1053884085279-q3fj4mkhg8oli5o7b38d51grkgp59p8q.apps.googleusercontent.com")
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void setTextChangedListener(EditText text) {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 6) {
                    staffLogin.setEnabled(true);
                    staffLogin.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.purple_200));
                } else {
                    staffLogin.setEnabled(false);
                    staffLogin.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.disable));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void staffLogin() {
        edtEmail = findViewById(R.id.editTextEmail);
        edtPass = findViewById(R.id.editTextPassword);
        staffLogin = findViewById(R.id.staffLoginBtn);
        closeStaffLogin = findViewById(R.id.staffLogin_closeBtn);
        pb = findViewById(R.id.staffLogin_progress_bar);
        staffLogin.setEnabled(false);
        setTextChangedListener(edtEmail);
        setTextChangedListener(edtPass);
        closeStaffLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.fragment_login);
                userLogin();
            }
        });
        TextView forgetPass = findViewById(R.id.forgetPass);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LoginActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottomsheet_forgotpassword);

                EditText edtEmailForgot = bottomSheetDialog.findViewById(R.id.forgot_email);
                ImageButton backBtn = bottomSheetDialog.findViewById(R.id.forgot_backBtn);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edtEmailForgot.setText("");
                        bottomSheetDialog.dismiss();
                    }
                });
                Button forgotBtn = bottomSheetDialog.findViewById(R.id.forgot_doneBtn);
                forgotBtn.setEnabled(false);
                edtEmailForgot.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String email = edtEmailForgot.getText().toString().trim();
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            forgotBtn.setEnabled(true);
                            forgotBtn.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.purple_200));
                        } else {
                            forgotBtn.setEnabled(false);
                            forgotBtn.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.disable));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                forgotBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = edtEmailForgot.getText().toString().trim();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("staff")
                                .whereEqualTo("email", email)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() == 0)
                                        Toast.makeText(LoginActivity.this, "Email này không có quyền hạn!", Toast.LENGTH_SHORT).show();
                                    else {
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(LoginActivity.this, "Vui lòng kiểm tra email để đặt lại mật khẩu", Toast.LENGTH_LONG).show();
                                                            bottomSheetDialog.dismiss();
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Email này chưa được đăng ký", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
                    }
                });
                bottomSheetDialog.show();
            }
        });
        staffLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                staffLogin.setVisibility(View.INVISIBLE);
                String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    pb.setVisibility(View.INVISIBLE);
                                    staffLogin.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, StaffActivity.class));
                                }
                                else {
                                    pb.setVisibility(View.INVISIBLE);
                                    staffLogin.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}