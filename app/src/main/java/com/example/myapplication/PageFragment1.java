package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class PageFragment1 extends Fragment {

    public PageFragment1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Замените R.layout.fragment_page1 на ваш макет для первой страницы
        return inflater.inflate(R.layout.fragment_page1, container, false);
    }
}
