package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.lama.Filters.DefaultPrompts;
import com.example.myapplication.lama.DynamicPrompt_;
import com.example.myapplication.lama.Filters.OptionsPrompts;
import com.example.myapplication.lama.LamaInteraction;
import com.example.myapplication.lama.Filters.TextStylePrompts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {
    private String uid;
    private TextView nameTextView;
    private TextView nameHotelTextView;
    private TextView dessai;
    private Switch switch1;
    private Button openWebsiteButton;
    private View regenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Retrieve uid from Intent
        uid = getIntent().getStringExtra("uid");

        nameTextView = findViewById(R.id.namehotel);
        switch1 = findViewById(R.id.switch1);
        nameHotelTextView = findViewById(R.id.nameHotelTextView);
        dessai = findViewById(R.id.dessai);
        openWebsiteButton = findViewById(R.id.openwebsite);
        regenButton = findViewById(R.id.button);

        dessai.setMovementMethod(new ScrollingMovementMethod());
        nameHotelTextView.setMovementMethod(new ScrollingMovementMethod());

        LamaInteraction Lama = new LamaInteraction(0.7f, 200, false, 60);


        DynamicPrompt_ p1 = new DynamicPrompt_();
        p1.addPrompt(DefaultPrompts.Classic);
//        p1.addPrompt(TextStylePrompts.Bro);
        p1.addPrompt(OptionsPrompts.mainTheme.build("Historical value"));
//        StaticPrompt p1 = new StaticPrompt("Below is an instruction that describes a task. Write a response that appropriately completes the request. Stick to an informal style, be sure to make jokes. Paste writing style with form. (You should apply irony), make jokes. Communicate in the language of a (semi-professional comic). With all of the above, your main task is to briefly describe the hotel. More IRONIC jokes and pranks.");
//        StaticPrompt p2 = new StaticPrompt("Below is an instruction that describes a task. Write a response that appropriately completes the request. You should write in literary and poetic language using aphorisms. Add interesting, rare sentences, copying Tolstoy's style. You describe the hotel, make a brief excerpt with stick to the given style, VERY BRIEFLY AND SUCCINCTLY ");

        Lama.setPrompt(p1);



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Generation").child(uid);
        regenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dessai.setText("generating...");
                dessai.setText(p1.toString());
//                Lama.Write(nameHotelTextView.getText().toString(), dessai);
            }
        });

        // Add ValueEventListener for "name"
        reference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nameTextView.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Add ValueEventListener for "dess"
        reference.child("dess").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nameHotelTextView.setText(dataSnapshot.getValue(String.class) + "\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Add ValueEventListener for "dessAi"
        reference.child("dessAi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dessai.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            nameHotelTextView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Add listener for "Перейти на MTC travel" button
        openWebsiteButton.setOnClickListener(v -> openWebsite());
    }

    public void openEdit(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }

    private void openWebsite() {
        DatabaseReference websiteReference = FirebaseDatabase.getInstance().getReference("Generation").child(uid).child("ssilka");
        websiteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String websiteUrl = dataSnapshot.getValue(String.class);
                    if (websiteUrl != null && !websiteUrl.isEmpty()) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                        startActivity(browserIntent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
