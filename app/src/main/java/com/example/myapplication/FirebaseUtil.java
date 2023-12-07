package com.example.myapplication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtil {
    public static Task<String> getUidByUsername(String username) {
        DatabaseReference allUsersRef = FirebaseDatabase.getInstance().getReference("allusers");

        TaskCompletionSource<String> source = new TaskCompletionSource<>();

        allUsersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.getValue(String.class);
                source.setResult(uid != null ? uid : "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                source.setException(databaseError.toException());
            }
        });

        return source.getTask();
    }
    public static Task<String> getUsernameByUid(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("username");

        TaskCompletionSource<String> source = new TaskCompletionSource<>();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                source.setResult(username != null ? username : "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                source.setException(databaseError.toException());
            }
        });

        return source.getTask();
    }
}