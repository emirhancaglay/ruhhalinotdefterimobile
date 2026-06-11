package com.example.ruhhaligunlugum;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                showDiaries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        filterSpinner.setOnItemSelectedListener(listener);
        sortSpinner.setOnItemSelectedListener(listener);
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
            emptyText.setText(databaseHelper.getTotalCount() == 0
                    ? "Henüz günlük eklenmedi."
                    : "Kayıt bulunamadı.");
            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.rgb(90, 90, 102));
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(dp(18), dp(28), dp(18), dp(28));
            emptyText.setBackgroundResource(R.drawable.bg_card);
            listContainer.addView(emptyText);
            return;
        }

        for (DiaryModel diary : diaries) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(dp(18), dp(16), dp(18), dp(16));
            itemLayout.setBackground(createMoodBackground(diary.getMood()));
            itemLayout.setElevation(dp(1));

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            itemParams.setMargins(0, 0, 0, dp(14));
            itemLayout.setLayoutParams(itemParams);

            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setGravity(Gravity.CENTER_VERTICAL);

            TextView titleText = new TextView(this);
            titleText.setText(diary.getTitle());
            titleText.setTextSize(19);
            titleText.setTextColor(Color.rgb(40, 40, 50));
            titleText.setTypeface(null, Typeface.BOLD);
            titleText.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1
            ));
            headerLayout.addView(titleText);

            if (diary.isFavorite()) {
                TextView favoriteText = new TextView(this);
                favoriteText.setText("★ Favori");
                favoriteText.setTextSize(13);
                favoriteText.setTypeface(null, Typeface.BOLD);
                favoriteText.setTextColor(Color.rgb(145, 96, 15));
                favoriteText.setPadding(dp(9), dp(5), dp(9), dp(5));
                favoriteText.setBackground(createBadgeBackground());
                headerLayout.addView(favoriteText);
            }

            TextView moodText = createInfoText("Ruh hali: " + diary.getMood());
            moodText.setTypeface(null, Typeface.BOLD);

            TextView noteText = createInfoText(diary.getNote());
            noteText.setTextSize(15);
            noteText.setPadding(dp(12), dp(10), dp(12), dp(10));
            noteText.setBackground(createNoteBackground());

            TextView temperatureText = createInfoText("Sıcaklık: " + diary.getTemperature());
            TextView dateText = createInfoText("Tarih: " + diary.getDate());

            itemLayout.addView(headerLayout);
            addTopMargin(itemLayout, moodText, 8);
            addTopMargin(itemLayout, noteText, 10);
            addTopMargin(itemLayout, temperatureText, 10);
            addTopMargin(itemLayout, dateText, 4);

            Button favoriteButton = createActionButton(
                    diary.isFavorite() ? "Favoriden Çıkar" : "Favoriye Ekle",
                    Color.rgb(238, 233, 218),
                    Color.rgb(120, 82, 18)
            );
            LinearLayout.LayoutParams favoriteParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(46)
            );
            favoriteParams.setMargins(0, dp(12), 0, dp(6));
            favoriteButton.setLayoutParams(favoriteParams);
            favoriteButton.setOnClickListener(v -> {
                databaseHelper.updateFavorite(diary.getId(), !diary.isFavorite());
                showDiaries();
            });
            itemLayout.addView(favoriteButton);

            LinearLayout actionRow = new LinearLayout(this);
            actionRow.setOrientation(LinearLayout.HORIZONTAL);

            Button editButton = createActionButton(
                    "Düzenle",
                    Color.rgb(231, 229, 243),
                    Color.rgb(80, 74, 132)
            );
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(0, dp(46), 1);
            editParams.setMargins(0, 0, dp(4), 0);
            editButton.setLayoutParams(editParams);
            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(DiaryListActivity.this, EditDiaryActivity.class);
                intent.putExtra("diaryId", diary.getId());
                startActivity(intent);
            });

            Button deleteButton = createActionButton(
                    "Sil",
                    Color.rgb(247, 226, 226),
                    Color.rgb(151, 63, 63)
            );
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(0, dp(46), 1);
            deleteParams.setMargins(dp(4), 0, 0, 0);
            deleteButton.setLayoutParams(deleteParams);
            deleteButton.setOnClickListener(v -> {
                databaseHelper.deleteDiary(diary.getId());
                Toast.makeText(this, "Kayıt silindi", Toast.LENGTH_SHORT).show();
                showDiaries();
            });

            actionRow.addView(editButton);
            actionRow.addView(deleteButton);
            itemLayout.addView(actionRow);
            listContainer.addView(itemLayout);
        }
    }

    private TextView createInfoText(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(Color.rgb(75, 75, 86));
        textView.setLineSpacing(0, 1.08f);
        return textView;
    }

    private void addTopMargin(LinearLayout parent, TextView view, int marginTop) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(marginTop), 0, 0);
        view.setLayoutParams(params);
        parent.addView(view);
    }

    private Button createActionButton(String text, int backgroundColor, int textColor) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(13);
        button.setTextColor(textColor);
        button.setAllCaps(false);
        button.setTypeface(null, Typeface.BOLD);
        button.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        return button;
    }

    private GradientDrawable createMoodBackground(String mood) {
        int fillColor = Color.rgb(250, 250, 252);
        int borderColor = Color.rgb(220, 220, 228);

        if ("Mutlu".equals(mood)) {
            fillColor = Color.rgb(255, 250, 232);
            borderColor = Color.rgb(234, 220, 174);
        } else if ("Normal".equals(mood)) {
            fillColor = Color.rgb(239, 248, 245);
            borderColor = Color.rgb(204, 226, 219);
        } else if ("Yorgun".equals(mood)) {
            fillColor = Color.rgb(244, 242, 250);
            borderColor = Color.rgb(218, 212, 234);
        } else if ("Üzgün".equals(mood)) {
            fillColor = Color.rgb(239, 246, 251);
            borderColor = Color.rgb(207, 222, 235);
        }

        GradientDrawable background = new GradientDrawable();
        background.setColor(fillColor);
        background.setCornerRadius(dp(8));
        background.setStroke(dp(1), borderColor);
        return background;
    }

    private GradientDrawable createBadgeBackground() {
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(250, 240, 210));
        background.setCornerRadius(dp(12));
        return background;
    }

    private GradientDrawable createNoteBackground() {
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.argb(150, 255, 255, 255));
        background.setCornerRadius(dp(6));
        return background;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
