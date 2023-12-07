package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.lama.UserOptions;

public class EditActivity extends AppCompatActivity {
    Spinner spinnerEmotionalTone, spinnerTargetAudience, spinnerMainStyle, spinnerFormalityLevel, spinnerHighlightFeatures;

    EditText editTextPromteneble, editTextPromtuneneble;
    DataBaseHelperSetting databaseHelper;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        uid = getIntent().getStringExtra("uid");

        // Инициализация всех Spinner
        spinnerEmotionalTone = findViewById(R.id.spinnerEmotionalTone);
        spinnerTargetAudience = findViewById(R.id.spinnerTargetAudience);
        spinnerFormalityLevel = findViewById(R.id.spinnerFormalityLevel);
        spinnerHighlightFeatures = findViewById(R.id.spinnerHighlightFeatures);
        spinnerMainStyle = findViewById(R.id.spinnerMainStyle);

        // Инициализация всех EditText
        editTextPromteneble = findViewById(R.id.editTextPromteneble);
        editTextPromtuneneble = findViewById(R.id.editTextPromtuneneble);

        // Отображение текущих данных на странице.
        spinnerHighlightFeatures.setSelection(getIndex(spinnerHighlightFeatures, UserOptions.highlightFeatures));
        spinnerEmotionalTone.setSelection(getIndex(spinnerEmotionalTone, UserOptions.emotionalTone));
        spinnerTargetAudience.setSelection(getIndex(spinnerTargetAudience, UserOptions.targetAudience));
        spinnerFormalityLevel.setSelection(getIndex(spinnerFormalityLevel, UserOptions.formalityLevel));
        spinnerMainStyle.setSelection(getIndex(spinnerMainStyle, UserOptions.mainStyle));
        editTextPromtuneneble.setText(UserOptions.promptUneneble);
        editTextPromteneble.setText(UserOptions.promptEneble);

//        // Создаем объект DatabaseHelper
//        databaseHelper = new DataBaseHelperSetting(this);

        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем значения из Spinner и EditText
                UserOptions.emotionalTone = spinnerEmotionalTone.getSelectedItem().toString();
                UserOptions.targetAudience = spinnerTargetAudience.getSelectedItem().toString();
                UserOptions.formalityLevel = spinnerFormalityLevel.getSelectedItem().toString();
                UserOptions.highlightFeatures = spinnerHighlightFeatures.getSelectedItem().toString();
                UserOptions.mainStyle = spinnerMainStyle.getSelectedItem().toString();

                UserOptions.promptEneble = editTextPromteneble.getText().toString();
                UserOptions.promptUneneble = editTextPromtuneneble.getText().toString();

//                long result = databaseHelper.addSettingsWithPrompts(uid,emotionalTone,targetAudience,seasonDescription,formalityLevel,mainColorAccent,highlightFeatures,newEmotionalTone,promptEneble,promptUneneble);

//                if (result != -1) {
//                    Toast.makeText(EditActivity.this, "Данные успешно сохранены в базе данных", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(EditActivity.this, "Ошибка при сохранении данных в базе данных", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                return i;
            }
        }
        return 0;
    }
}