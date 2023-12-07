package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements BottomSheetFragment.OnItemClickListener, MessageAdapter.OnItemClickListener {

    private EditText editTextMessage;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String newChatKey;

    private ImageView imageViewAvatar;
    private TextView textViewUsername;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = findViewById(R.id.editTextMessage);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        textViewUsername = findViewById(R.id.textViewUsername);
        btnBack = findViewById(R.id.btnBack);

        newChatKey = getIntent().getStringExtra("newChatKey");

        messageList = new ArrayList<>();

        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClick(v);
            }
        });

        recyclerViewMessages.setClickable(true);
        messageAdapter.setOnItemClickListener(this);

        recyclerViewMessages.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                showBottomSheetDialog();
            }
        });

        loadUserInfo();
        loadMessages();
    }

    private void showBottomSheetDialog() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void onSendClick(View view) {
        String text = editTextMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String senderId = currentUser.getUid();
                sendMessage(senderId, text, false, " ");
                editTextMessage.setText("");
            }
        }
    }

    private void sendMessage(String senderId, String text, boolean isHistory, String url) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("message").child(newChatKey);
        String messageId = messagesRef.push().getKey();

        long timestamp = System.currentTimeMillis();
        Message message = new Message(senderId, text, timestamp, isHistory, url);
        messagesRef.child(messageId).setValue(message);
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
    }
    private void loadMessages() {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("message").child(newChatKey);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        message.setKey(messageSnapshot.getKey());
                        messageList.add(message );
                    }
                }

                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Не удалось загрузить сообщения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserInfo() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(newChatKey);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> participants = new ArrayList<>();
                    for (DataSnapshot participantSnapshot : dataSnapshot.child("participants").getChildren()) {
                        participants.add(participantSnapshot.getValue(String.class));
                    }

                    for (String participantUid : participants) {
                        if (!participantUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(participantUid);

                            userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String username = dataSnapshot.getValue(String.class);
                                    if (username != null && !username.isEmpty()) {
                                        textViewUsername.setText(username);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(ChatActivity.this, "Не удалось загрузить имя пользователя", Toast.LENGTH_SHORT).show();
                                }
                            });

                            userRef.child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String avatarUrl = dataSnapshot.getValue(String.class);
                                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(16));
                                        Glide.with(ChatActivity.this)
                                                .load(avatarUrl)
                                                .apply(requestOptions)
                                                .into(imageViewAvatar);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(ChatActivity.this, "Не удалось загрузить аватар", Toast.LENGTH_SHORT).show();
                                }
                            });

                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Не удалось загрузить информацию о пользователе", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemClick(View view, int position) {
        // Обработка клика в BottomSheetFragment
        // Можете добавить свою логику для обработки клика в истории
    }

    private void deleteMessage(int position) {
        if (position >= 0 && position < messageList.size()) {
            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("message").child(newChatKey);
            String messageId = messageList.get(position).getKey();

            messagesRef.child(messageId).removeValue();

            messageList.remove(position);
            messageAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void onItemClick(HistoryItem historyItem) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        sendMessage(user.getUid(), historyItem.getText(), true, historyItem.getImageUrl());
    }


}
