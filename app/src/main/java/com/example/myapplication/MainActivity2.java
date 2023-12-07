//package com.example.myapplication;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.Button;
//import android.widget.LinearLayout;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class MainActivity2 extends AppCompatActivity {
//    private DBHelper dbHelper;
//    private String url;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        dbHelper = new DBHelper(this);
//
//        Button button1 = findViewById(R.id.a101);
//        Button button2 = findViewById(R.id.a102);
//        Button button3 = findViewById(R.id.a105);
//
//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String formattedId = getResources().getResourceEntryName(v.getId());
//
//                PopupFragment popupFragment = new PopupFragment();
//                Bundle args = new Bundle();
//
//                // Передайте инициализированный dbHelper в PopupFragment
//                popupFragment.setDBHelper(dbHelper);
//
//                args.putString("formattedButtonId", formattedId);
//                popupFragment.setArguments(args);
//                popupFragment.show(getSupportFragmentManager(), "popup_fragment");
//            }
//        };
//
//        button1.setOnClickListener(onClickListener);
//        button2.setOnClickListener(onClickListener);
//        button3.setOnClickListener(onClickListener);
//
//        // Добавляем кнопку для открытия веб-сайта
//        Button openWebsiteButton = findViewById(R.id.openWebsiteButtonInBottomSheet);
//        openWebsiteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Здесь вы указываете URL вашего веб-сайта
//                String url = "https://eda.yandex.ru/r/aloha?placeSlug=aloha_hujze";
//                openWebsite(url);
//            }
//
//        });
//        Button closeButton = findViewById(R.id.closeButton);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Здесь добавьте код для закрытия bottomSheet с анимацией
//                LinearLayout bottomSheet = findViewById(R.id.bottomSheet);
//
//                Animation animation = AnimationUtils.loadAnimation(MainActivity2.this, R.anim.slide_out);
//                bottomSheet.startAnimation(animation);
//
//                // После анимации закрытия bottomSheet, скройте его
//                bottomSheet.setVisibility(View.GONE);
//            }
//        });
//
//
//    }
//
//
//
//    private void openWebsite(String url) {
//        Uri webpage = Uri.parse(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//        startActivity(intent);
//    }
//
//}
