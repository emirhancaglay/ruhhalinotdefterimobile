package com.example.ruhhaligunlugum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DiaryListActivity extends Activity {
    private EditText searchEditText;
    private Spinner filterSpinner;
    private Spinner sortSpinner;
    private LinearLayout listContainer;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        searchEditText = findViewById(R.id.searchEditText);
        filterSpinner = findViewById(R.id.filterSpinner);
        sortSpinner = findViewById(R.id.sortSpinner);
        listContainer = findViewById(R.id.listContainer);
        databaseHelper = new DatabaseHelper(this);

        String[] filters = {"Tümü", "Mutlu", "Normal", "Yorgun", "Üzgün"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        String[] sortOptions = {"En Yeni", "En Eski"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showDiaries();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                showDiaries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                showDiaries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        showDiaries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDiaries();
    }

    private void showDiaries() {
        listContainer.removeAllViews();

        String searchText = searchEditText.getText().toString();
        String moodFilter = filterSpinner.getSelectedItem().toString();
        String sortOrder = sortSpinner.getSelectedItem().toString();
        ArrayList<DiaryModel> diaries = databaseHelper.getDiaries(searchText, moodFilter, sortOrder);

        if (diaries.isEmpty()) {
            TextView emptyText = new TextView(this);
            if (databaseHelper.getTotalCount() == 0) {
                emptyText.setText("Henüz günlük eklenmedi.");
            } else {
                emptyText.setText("Kayıt bulunamadı.");
            }
            emptyText.setTextSize(16);
            listContainer.addView(emptyText);
            return;
        }

        for (DiaryModel diary : diaries) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(20, 20, 20, 20);
            itemLayout.setBackgroundColor(getMoodColor(diary.getMood()));

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            itemParams.setMargins(0, 0, 0, 20);
            itemLayout.setLayoutParams(itemParams);

            TextView titleText = new TextView(this);
            titleText.setText(diary.getTitle());
            titleText.setTextSize(18);
            titleText.setTypeface(null, Typeface.BOLD);
            itemLayout.addView(titleText);

            if (diary.isFavorite()) {
                TextView favoriteText = new TextView(this);
                favoriteText.setText("Favori");
                favoriteText.setTypeface(null, Typeface.BOLD);
                favoriteText.setTextColor(Color.rgb(180, 120, 0));
                itemLayout.addView(favoriteText);
            }

            TextView moodText = new TextView(this);
            moodText.setText("Ruh hali: " + diary.getMood());

            TextView noteText = new TextView(this);
            noteText.setText("Not: " + diary.getNote());

            TextView dateText = new TextView(this);
            dateText.setText("Tarih: " + diary.getDate());

            TextView temperatureText = new TextView(this);
            temperatureText.setText("Sıcaklık: " + diary.getTemperature());

            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.VERTICAL);

            Button editButton = new Button(this);
            editButton.setText("Düzenle");
            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(DiaryListActivity.this, EditDiaryActivity.class);
                intent.putExtra("diaryId", diary.getId());
                startActivity(intent);
            });

            Button favoriteButton = new Button(this);
            favoriteButton.setText(diary.isFavorite() ? "Favoriden Çıkar" : "Favoriye Ekle");
            favoriteButton.setOnClickListener(v -> {
                databaseHelper.updateFavorite(diary.getId(), !diary.isFavorite());
                showDiaries();
            });

            Button deleteButton = new Button(this);
            deleteButton.setText("Sil");
            deleteButton.setOnClickListener(v -> {
                databaseHelper.deleteDiary(diary.getId());
                Toast.makeText(this, "Kayıt silindi", Toast.LENGTH_SHORT).show();
                showDiaries();
            });

            buttonLayout.addView(editButton);
            buttonLayout.addView(favoriteButton);
            buttonLayout.addView(deleteButton);

            itemLayout.addView(moodText);
            itemLayout.addView(noteText);
            itemLayout.addView(dateText);
            itemLayout.addView(temperatureText);
            itemLayout.addView(buttonLayout);
            listContainer.addView(itemLayout);
        }
    }

    private int getMoodColor(String mood) {
        if ("Mutlu".equals(mood)) {
            return Color.rgb(255, 249, 196);
        }
        if ("Normal".equals(mood)) {
            return Color.rgb(224, 242, 241);
        }
        if ("Yorgun".equals(mood)) {
            return Color.rgb(232, 234, 246);
        }
        if ("Üzgün".equals(mood)) {
            return Color.rgb(227, 242, 253);
        }
        return Color.rgb(240, 240, 240);
    }
}
