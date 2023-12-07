package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.viewpagerindicator.CirclePageIndicator;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SmallPhotoAdapter.OnItemClickListener {
    private RecyclerView recyclerViewHistory;
    private FragmentManager fragmentManager;
    private HistoryAdapter historyAdapter;
    private AutoCompleteTextView cityAutoComplete;
    private AutoCompleteTextView editTextHotel;
    private RecyclerView smallPhotoRecyclerView;
    private SmallPhotoAdapter smallPhotoAdapter;


    private DataBaseHelperSetting mDatabaseHelperS;

    private final DatabaseHelperLocal mDatabaseHelperL = new DatabaseHelperLocal(this);

    private ImageView avatarImageView;
    private Button btnGetDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelperS = new DataBaseHelperSetting(this);

        this.fragmentManager = fragmentManager;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            setupPic(user.getUid());
        } else {
        }
        setcity();

        setupHistoryAdapter();

        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryItem historyItem) {
            }
        });

        // Инициализация адаптера для фотографий и истории
        setupAdapters();

        // Настройка AutoCompleteTextView для ввода города
        setupCityAutoComplete();
        setupHotelAutoComplete();

        // Настройка кнопки "Получить описание"
        setupGetDescriptionButton();
        populateHistory();

    }
    private void setupAdapters() {
        List<Integer> photoList = Arrays.asList(
                R.drawable.photo1, R.drawable.photo2, R.drawable.photo3,
                R.drawable.photo4, R.drawable.photo5, R.drawable.photo6,
                R.drawable.photo7);

        // Correct the second parameter to be `this` (MainActivity)
        smallPhotoAdapter = new SmallPhotoAdapter(photoList, this);

        // Настройка RecyclerView
        smallPhotoRecyclerView = findViewById(R.id.smallPhotoRecyclerView);
        smallPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        smallPhotoRecyclerView.setAdapter(smallPhotoAdapter);
    }

    @Override
    public void onItemClick(int position) {
        MyDialogFragment dialog = new MyDialogFragment(this);
        dialog.show();

    }

    private void setupCityAutoComplete() {
        cityAutoComplete = findViewById(R.id.cityAutoComplete);

        String[] cities = mDatabaseHelperS.getUniqueCities().toArray(new String[0]);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        cityAutoComplete.setAdapter(cityAdapter);

        cityAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            cityAutoComplete.showDropDown();
        });

        // Обработка выбора города из выпадающего списка
        cityAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = (String) parent.getItemAtPosition(position);
            mDatabaseHelperL.addData(selectedCity);
            cityAutoComplete.setText(selectedCity);
            hideKeyboard();
            cityAutoComplete.clearFocus();
            setupHotelAutoComplete();
        });
    }

    private void setupHotelAutoComplete() {
        editTextHotel = findViewById(R.id.editTextHotel);

        String city = mDatabaseHelperL.getCity();
        if (city != null) {
            List<String> hotelsInCity = mDatabaseHelperS.getHotelsInCity(city);

            ArrayAdapter<String> hotelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, hotelsInCity);
            editTextHotel.setAdapter(hotelAdapter);

            editTextHotel.setOnItemClickListener((parent, view, position, id) -> {
                editTextHotel.showDropDown();
            });

            editTextHotel.setOnItemClickListener((parent, view, position, id) -> {
                String selectedHotel = (String) parent.getItemAtPosition(position);
                editTextHotel.setText(selectedHotel);
                hideKeyboard();
                editTextHotel.clearFocus();
            });
        } else {
        }
    }


    private void setupHistoryAdapter() {
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

        // Initialize historyAdapter if not already initialized
        if (historyAdapter == null) {
            recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
            historyAdapter = new HistoryAdapter();
            recyclerViewHistory.setAdapter(historyAdapter);
        }

    }

    private void setupGetDescriptionButton() {
        Button btnGetDescription = findViewById(R.id.btnGetDescription);
        btnGetDescription.setOnClickListener(v -> {
            String enteredCity = cityAutoComplete.getText().toString();
            String hotel = editTextHotel.getText().toString();
            performCitySearch(enteredCity, hotel);

        });
    }


    private void performCitySearch(String city, String hotel) {
        if (mDatabaseHelperS.exists(city, hotel)) {
            editTextHotel = findViewById(R.id.editTextHotel);
            editTextHotel.getText().clear();

            DatabaseReference generationRef = FirebaseDatabase.getInstance().getReference("Generation");

            String uid = generationRef.push().getKey();
            String url = mDatabaseHelperS.getLinkReservation(hotel, city);
            String dess = mDatabaseHelperS.getdess(hotel, city);

            GenerationData generationData = new GenerationData(hotel, city, "urlPic", dess, "dessAi", url, uid);
            generationRef.child(uid).setValue(generationData);

            mDatabaseHelperL.insertHistory("history", hotel, city, "https://a.travelcdn.mts.ru/property-photos/1633728227/2347900126/e57b314c64e29e65b8c666a65707143bfd0f426d.jpeg", dess, " ", url, uid);
            mDatabaseHelperS.getCombinedInfoForHotel(hotel, city);
            populateHistory();

        }

    }

    private void populateHistory() {

        Cursor cursor = mDatabaseHelperL.getAllHistoryData("history");
        List<HistoryItem> historyList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int urlPicIndex = cursor.getColumnIndexOrThrow("url_pic");
            int nameIndex = cursor.getColumnIndexOrThrow("name");
            int uidIndex = cursor.getColumnIndexOrThrow("uid");

            if (urlPicIndex != -1 && nameIndex != -1 && uidIndex != -1) {
                String urlPic = cursor.getString(urlPicIndex);
                String name = cursor.getString(nameIndex);
                String uid = cursor.getString(uidIndex);

                historyList.add(new HistoryItem(name, urlPic, uid));
            }
        }

        mDatabaseHelperL.closeCursor(cursor);
        mDatabaseHelperL.closeDatabase();

        historyAdapter.setHistoryList(historyList);

    }


    private void setcity(){
        String city = null;
        try {
            city = mDatabaseHelperL.getCity();
        } catch (Exception e){
            Log.e("MainActivity", "Ошибка в блоке setcity: ", e );
        }
        cityAutoComplete = findViewById(R.id.cityAutoComplete);
        cityAutoComplete.setText(city);


    }

    private void setupPic(String uid){
        avatarImageView = findViewById(R.id.userAvatar);

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
                            Glide.with(MainActivity.this)
                                    .load(imageUrl)
                                    .apply(new RequestOptions().override(250, 250))
                                    .into(avatarImageView);
                        } else {
                            Toast.makeText(MainActivity.this, "Image URL is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Avatar node does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void openUserProfile(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager(); // Получите FragmentManager в вашей активности
            LoginDialog loginDialog = new LoginDialog(this, fragmentManager);
            loginDialog.show();

        }

    }
    public void openRegist() {
        RegistrationDialogFragment dialogFragment = new RegistrationDialogFragment();
        dialogFragment.show(fragmentManager, "registration_dialog");
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(cityAutoComplete.getWindowToken(), 0);
        }
    }

}
