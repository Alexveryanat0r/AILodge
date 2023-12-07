package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginDialog extends Dialog {

    private final EditText etEmail;
    private final EditText etPassword;
    private final Button btnLogin;
    private final Button btnregis;
    private final FragmentManager fragmentManager;
    private final FirebaseAuth mAuth;

    public LoginDialog(@NonNull Context context, FragmentManager fragmentManager) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.fragmentManager = fragmentManager; // Инициализируйте fragmentManager в конструкторе
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnregis = findViewById(R.id.Regist);
        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                openRegist();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            AuthResult authResult = mAuth.signInWithEmailAndPassword(email, password).getResult();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                checkUserInFirebase(user.getUid(), new UserCheckCallback() {
                    @Override
                    public void onUserChecked(String username) {
                        if ("None".equals(username)) {
                            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                        } else {
                            dismiss();
                            saveUserToLocalDatabase(username, user.getUid(), " ");
                        }
                    }
                });
            } else {
                Log.e("LoginDialog", "Error: FirebaseUser == null");
                Toast.makeText(getContext(), "Error logging in: FirebaseUser == null", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        } catch (Exception e) {
            Log.e("LoginDialog", "Login error: " + e.getMessage());
            dismiss();
        }

    }

    private boolean saveUserToLocalDatabase(String username, String uid, String avatar) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        try {
            SQLiteDatabase db = dbHelper.openDatabase(getContext());

            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("uid", uid);
            values.put("avatar", avatar);
            db.insert("users", null, values);
            db.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void checkUserInFirebase(String uid, UserCheckCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    callback.onUserChecked(username);
                } else {
                    callback.onUserChecked("None");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                callback.onUserChecked("None");
            }
        });
    }

    interface UserCheckCallback {
        void onUserChecked(String username);
    }
    public void openRegist() {
        RegistrationDialogFragment dialogFragment = new RegistrationDialogFragment();
        dialogFragment.show(fragmentManager, "registration_dialog");
    }


}
