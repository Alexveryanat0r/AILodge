package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends DialogFragment implements HistoryAdapter.OnItemClickListener {

    private HistoryAdapter historyAdapter;
    private RecyclerView recyclerViewHistory;
    private DatabaseHelperLocal mDataBaseHelper;
    private OnItemClickListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            listener = (OnItemClickListener) context;
        } else {
            throw new ClassCastException(context + " must implement OnItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        mDataBaseHelper = new DatabaseHelperLocal(requireContext());
        setupHistoryAdapter();
        populateHistory();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = (int) (displayMetrics.heightPixels * 0.4);

            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = height;
            params.x = 0;
            params.y = 0;

            getDialog().getWindow().setAttributes(params);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setupHistoryAdapter() {
        if (historyAdapter == null) {
            historyAdapter = new HistoryAdapter();
            recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewHistory.setAdapter(historyAdapter);

            // Установка обработчика клика в адаптер
            historyAdapter.setOnItemClickListener(this);
        }
    }

    private void populateHistory() {
        Cursor cursor = mDataBaseHelper.getAllHistoryData("history");
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
        mDataBaseHelper.closeCursor(cursor);
        mDataBaseHelper.closeDatabase();

        historyAdapter.setHistoryList(historyList);
    }

    @Override
    public void onItemClick(HistoryItem historyItem) {
        // Обработка клика в BottomSheetFragment
        // Передача данных обратно в активити через интерфейс
        listener.onItemClick(historyItem);
        dismiss();
    }

    public interface OnItemClickListener {
        void onItemClick(HistoryItem historyItem);
    }
}
