package com.example.myapplication;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class PageFragment3 extends Fragment {

    public PageFragment3() {
        // Обязательный конструктор
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Замените R.layout.fragment_page1 на ваш макет для первой страницы
        return inflater.inflate(R.layout.fragment_page3, container, false);
    }
}
