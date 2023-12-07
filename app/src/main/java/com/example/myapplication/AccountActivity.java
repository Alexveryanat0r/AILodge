package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class AccountActivity extends AppCompatActivity implements ChatAdapter.OnChatClickListener  {

    private static final int PICK_IMAGE_REQUEST = 1;
    static final int STORAGE_PERMISSION_CODE = 123;

    DatabaseHelper dbHelper = new DatabaseHelper(this);
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> chatList;

    private ImageView avatarImageView;
    private ImageView editIcon;
    private static Uri selectedImageUri;
    private final DatabaseHelper database = new DatabaseHelper(this);

    private DatabaseReference usersRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                initializeUI();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }

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

        ImageView outButton = findViewById(R.id.outusers);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                out();
            }
        });
    }
    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("newChatKey", chat.getuidchat());
        startActivity(intent);
    }
    private void initializeUI() {
        avatarImageView = findViewById(R.id.avatarImageView);
        editIcon = findViewById(R.id.editIcon);

        TextView usernameTextView = findViewById(R.id.usernameTextView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("username");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.getValue(String.class);
                        usernameTextView.setText(username);
                    } else {}
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        storageRef = FirebaseStorage.getInstance().getReference().child("avatars").child(user.getUid());

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        loadAvatarImage();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            resizeImage(selectedImageUri);
       } else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для изменения размера изображения перед загрузкой
    private void resizeImage(Uri originalImageUri) {
        try {
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), originalImageUri);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 200, 200, true);
           uploadImageToFirebaseStorage(resizedBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для загрузки измененного изображения в Firebase Storage и обновления URL в Realtime Database
    private void uploadImageToFirebaseStorage(Bitmap resizedBitmap) {
        // Преобразование Bitmap в Uri
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), resizedBitmap, "Title", null);
        Uri resizedImageUri = Uri.parse(path);

        // Загрузка изображения в Firebase Storage и обновление URL в Realtime Database
        storageRef.putFile(resizedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        usersRef.child("avatar").setValue(uri.toString());
                    });
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    // Метод для загрузки аватарки из Firebase Storage
    private void loadAvatarImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            DatabaseReference avatarRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(uid)
                    .child("avatar");

            avatarRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!isDestroyed()) {
                        if (dataSnapshot.exists()) {
                            String imageUrl = dataSnapshot.getValue(String.class);
                            if (imageUrl != null) {
                                Glide.with(AccountActivity.this)
                                        .load(imageUrl)
                                        .apply(new RequestOptions().override(200, 200))
                                        .into(avatarImageView);
                            } else {
                                Toast.makeText(AccountActivity.this, "Image URL is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AccountActivity.this, "Avatar node does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AccountActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                Toast.makeText(AccountActivity.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AccountActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AccountActivity.this, "Error finding user", Toast.LENGTH_SHORT).show();
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

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("username");

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String myusername = dataSnapshot.getValue(String.class);
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
                                        Toast.makeText(AccountActivity.this, "Chat created successfully", Toast.LENGTH_SHORT).show();
                                        recreate();
                                    } else {
                                        Toast.makeText(AccountActivity.this, "Вы пытаетесь добавить себя.", Toast.LENGTH_SHORT).show();

                                    }
                                } else {}
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });

                    } else {
                        Toast.makeText(AccountActivity.this, "UID is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AccountActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AccountActivity.this, "Failed to get UID", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AccountActivity.this, "Ошибка при проверке наличия чата", Toast.LENGTH_SHORT).show();
                    }
                });

        return chatExists.get();
    }
    public void out() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
