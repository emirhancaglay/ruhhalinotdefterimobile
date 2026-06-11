package com.example.ruhhaligunlugum;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditDiaryActivity extends Activity {
    private EditText titleEditText;
    private EditText noteEditText;
    private Spinner moodSpinner;
    private DatabaseHelper databaseHelper;
    private int diaryId;
    private String temperature = "Bilinmiyor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        titleEditText = findViewById(R.id.editTitleEditText);
        noteEditText = findViewById(R.id.editNoteEditText);
        moodSpinner = findViewById(R.id.editMoodSpinner);
        TextView temperatureText = findViewById(R.id.editTemperatureText);
        Button updateButton = findViewById(R.id.updateButton);
        databaseHelper = new DatabaseHelper(this);

        String[] moods = {"Mutlu", "Normal", "Yorgun", "Üzgün"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);

        diaryId = getIntent().getIntExtra("diaryId", -1);
        DiaryModel diary = databaseHelper.getDiaryById(diaryId);

        if (diary == null) {
            Toast.makeText(this, "Kayıt bulunamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleEditText.setText(diary.getTitle());
        noteEditText.setText(diary.getNote());
        temperature = diary.getTemperature();
        temperatureText.setText("Sıcaklık: " + temperature);
        setSpinnerMood(diary.getMood());

        updateButton.setOnClickListener(v -> updateDiary());
    }

    private void updateDiary() {
        String title = titleEditText.getText().toString().trim();
        String note = noteEditText.getText().toString().trim();
        String mood = moodSpinner.getSelectedItem().toString();

        if (title.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Başlık ve not boş bırakılamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseHelper.updateDiary(diaryId, title, note, mood, temperature);
        Toast.makeText(this, "Günlük güncellendi", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setSpinnerMood(String mood) {
        for (int i = 0; i < moodSpinner.getCount(); i++) {
            if (moodSpinner.getItemAtPosition(i).toString().equals(mood)) {
                moodSpinner.setSelection(i);
                return;
            }
        }
    }
}
