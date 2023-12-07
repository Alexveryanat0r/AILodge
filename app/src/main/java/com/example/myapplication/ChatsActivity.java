package com.example.myapplication;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatsActivity extends AppCompatActivity implements ChatAdapter.OnChatClickListener {
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        chatList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        loadChatsFromFirebase(user.getUid());

        adapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(adapter);


        adapter.setOnChatClickListener(this);
    }
    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("newChatKey", chat.getuidchat());
        startActivity(intent);
    }
    private void loadChatsFromFirebase(String uid) {
        DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("chats");

        userChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatKey = chatSnapshot.getKey();
                    if (chatKey != null) {
                        String newChatKey = chatSnapshot.getValue(String.class);
                        chatList.add(new Chat(chatKey, newChatKey));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatsActivity.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        final EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();

                DatabaseReference allUsersRef = FirebaseDatabase.getInstance().getReference("allusers");
                allUsersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            addParticipantToChat(username);
                        } else {
                            Toast.makeText(ChatsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ChatsActivity.this, "Error finding user", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialog.dismiss();
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                recreate();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void addParticipantToChat(String otherUsername) {
        DatabaseReference allUsersRef = FirebaseDatabase.getInstance().getReference("allusers");

        allUsersRef.child(otherUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String otherUserId = dataSnapshot.getValue(String.class);
                    if (otherUserId != null) {

                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = currentUser.getUid();

                        String myusername = dbHelper.getUsernameByUid(currentUserId);
                        if (!otherUsername.equals(myusername)) {

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");

                            String newChatKey = chatsRef.push().getKey();

                            List<String> participants = new ArrayList<>();
                            participants.add(currentUserId);
                            participants.add(otherUserId);

                            Map<String, Object> chatMap = new HashMap<>();
                            chatMap.put("participants", participants);

                            chatsRef.child(newChatKey).setValue(chatMap);

                            usersRef.child(currentUserId).child("chats").child(otherUsername).setValue(newChatKey);
                            usersRef.child(otherUserId).child("chats").child(myusername).setValue(newChatKey);
                            Toast.makeText(ChatsActivity.this, "Chat created successfully", Toast.LENGTH_SHORT).show();
                            recreate();
                        } else {
                            Toast.makeText(ChatsActivity.this, "Вы пытаетесь добавить себя.", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(ChatsActivity.this, "UID is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatsActivity.this, "Failed to get UID", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean doesChatExist(String currentUserId, String otherUserId) {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        AtomicBoolean chatExists = new AtomicBoolean(false);

        chatsRef.orderByChild("participants/" + currentUserId).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                            if (chatSnapshot.child("participants").child(otherUserId).exists()) {
                                chatExists.set(true);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ChatsActivity.this, "Ошибка при проверке наличия чата", Toast.LENGTH_SHORT).show();
                    }
                });

        return chatExists.get();
    }

}
