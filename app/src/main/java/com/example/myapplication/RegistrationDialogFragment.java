package com.example.myapplication;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegistrationDialogFragment extends DialogFragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference firDatabase;

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnRegister;



    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_registration, container, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        firDatabase = FirebaseDatabase.getInstance().getReference("allusers");

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void registerUser() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            try {
                                saveUserToFirebaseDatabase(username, userId, " ");
                                firDatabase.child(username).setValue(userId);
                                if (dbHelper.saveUserToLocalDatabase(username, userId, getContext())) {
                                    dbHelper.addUsernameColumnToChatsTable(username);
                                    Toast.makeText(requireContext(), "Регистрация успешна", Toast.LENGTH_SHORT).show();
                                    dismiss(); // Закрываем диалоговое окно после успешной регистрации
                                } else {
                                    Toast.makeText(requireContext(), "Ошибка регистрации: При добавление пользователя в БД возникла ошибка", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(requireContext(), "Ошибка регистрации: При добавление пользователя в БД возникла ошибка", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    private void saveUserToFirebaseDatabase(String username, String uid, String avatar) {
        User user = new User(username, uid, avatar);
        mDatabase.child(uid).setValue(user);
    }
}