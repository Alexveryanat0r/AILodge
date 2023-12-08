package com.example.myapplication;

import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deepl.api.Translator;
import com.example.myapplication.lama.Filters.DefaultPrompts;
import com.example.myapplication.lama.DynamicPrompt_;
import com.example.myapplication.lama.Filters.OptionsPrompts;
import com.example.myapplication.lama.LamaInteraction;
import com.example.myapplication.lama.Filters.TextStylePrompts;
import com.example.myapplication.lama.UserOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;

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


        String deeplapi = "946828e6-db14-c27b-0f57-ac7f85f32365:fx";
        Translator trans = new Translator(deeplapi);

        LamaInteraction Lama = new LamaInteraction(this, trans, 0.7f, 260, false, 60);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Generation").child(uid);

        DataBaseHelperSetting db_helper = new DataBaseHelperSetting(this);
        HotelData.ID = db_helper.getIdByHotelNameAndCity(HotelData.name, HotelData.city);
        String combinated_hotel_info = db_helper.getCombinedInfoForHotel(HotelData.name, HotelData.city);

        regenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DynamicPrompt_ p1 = new DynamicPrompt_();

                if (Objects.equals(UserOptions.mainStyle, "Формальный стиль") || Objects.equals(UserOptions.emotionalTone, null)) {
                    p1.addPrompt(TextStylePrompts.Formal);
                } else if (Objects.equals(UserOptions.mainStyle, "Публицистический стиль")) {
                    p1.addPrompt(TextStylePrompts.Journalistic);
                } else if (Objects.equals(UserOptions.mainStyle, "Комик-полупрофи")) {
                    p1.addPrompt(TextStylePrompts.Joker);
                } else if (Objects.equals(UserOptions.mainStyle, "Менее поэтичный публицистический стиль")) {
                    p1.addPrompt(TextStylePrompts.JournalisticDown);
                } else if (Objects.equals(UserOptions.mainStyle, "Крайне неформально-молодёжный стиль")) {
                    p1.addPrompt(TextStylePrompts.Bro);
                }

                if(!Objects.equals(UserOptions.emotionalTone, "Нет") && !Objects.equals(UserOptions.emotionalTone, null)) {
                    p1.addPrompt(OptionsPrompts.emotions.build(UserOptions.emotionalTone));
                }

                if(!Objects.equals(UserOptions.formalityLevel, "Нет") && !Objects.equals(UserOptions.formalityLevel, null)) {
                    p1.addPrompt(OptionsPrompts.descriptionStyle.build(UserOptions.formalityLevel));
                }

                if(!Objects.equals(UserOptions.highlightFeatures, "Нет") && !Objects.equals(UserOptions.highlightFeatures, null)) {
                    p1.addPrompt(OptionsPrompts.mainTheme.build(UserOptions.highlightFeatures));
                }

                if(!Objects.equals(UserOptions.promptEneble, "") && !Objects.equals(UserOptions.promptEneble, null)) {
                    p1.addPrompt(OptionsPrompts.useWords.buildt2(UserOptions.promptEneble));
//                    p1.addPrompt(OptionsPrompts..build(UserOptions.promptEneble));
                }

                if(!Objects.equals(UserOptions.promptUneneble, "") && !Objects.equals(UserOptions.promptUneneble, null)) {
                    p1.addPrompt(OptionsPrompts.dontUseWords.buildt(UserOptions.promptUneneble));
//                    p1.addPrompt(OptionsPrompts..build(UserOptions.promptUneneble));
                }

                if(!Objects.equals(UserOptions.targetAudience, "Нет") && !Objects.equals(UserOptions.targetAudience, null)) {
                    p1.addPrompt(OptionsPrompts.audience.build(UserOptions.targetAudience));
                }

//
//                p1.addPrompt(TextStylePrompts.Joker);
//                p1.addPrompt(OptionsPrompts.seasons.build("Winter"));

                Lama.setPrompt(p1);
                try {
                    dessai.setText("generating...");
                    String lama_req = combinated_hotel_info + nameHotelTextView.getText().toString();
//                    dessai.setText(lama_req);

                    Lama.Write(nameHotelTextView.getText().toString(), dessai);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
