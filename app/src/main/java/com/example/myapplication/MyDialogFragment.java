package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class MyDialogFragment extends AlertDialog {

    protected MyDialogFragment(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Используйте LayoutInflater для создания макета диалогового окна
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);

        // Добавление изображения (фотографии) сверху градиентного фона
        ImageView imageView = dialogView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.hotel1);

        // Установка представления в диалоговое окно
        setView(dialogView);
    }
}
