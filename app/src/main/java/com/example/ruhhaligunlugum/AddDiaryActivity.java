package com.example.ruhhaligunlugum;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddDiaryActivity extends Activity {
    private EditText titleEditText;
    private EditText noteEditText;
    private Spinner moodSpinner;
    private DatabaseHelper databaseHelper;
    private String temperature = "Bilinmiyor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        titleEditText = findViewById(R.id.titleEditText);
        noteEditText = findViewById(R.id.noteEditText);
        moodSpinner = findViewById(R.id.moodSpinner);
        TextView temperatureText = findViewById(R.id.temperatureText);
        Button saveButton = findViewById(R.id.saveButton);
        databaseHelper = new DatabaseHelper(this);

        String incomingTemperature = getIntent().getStringExtra("temperature");
        if (incomingTemperature != null && !incomingTemperature.isEmpty()) {
            temperature = incomingTemperature;
        }

        temperatureText.setText("Hava sıcaklığı: " + temperature);

        String[] moods = {"Mutlu", "Normal", "Yorgun", "Üzgün"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> saveDiary());
    }

    private void saveDiary() {
        String title = titleEditText.getText().toString().trim();
        String note = noteEditText.getText().toString().trim();
        String mood = moodSpinner.getSelectedItem().toString();

        if (title.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Başlık ve not boş bırakılamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("tr", "TR")).format(new Date());
        databaseHelper.addDiary(title, note, mood, date, temperature);
        Toast.makeText(this, "Günlük kaydedildi", Toast.LENGTH_SHORT).show();
        finish();
    }
}
