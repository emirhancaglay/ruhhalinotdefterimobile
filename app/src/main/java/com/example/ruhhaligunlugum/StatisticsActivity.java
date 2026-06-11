package com.example.ruhhaligunlugum;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class StatisticsActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private TextView totalText;
    private TextView favoriteCountText;
    private TextView mutluText;
    private TextView normalText;
    private TextView yorgunText;
    private TextView uzgunText;
    private TextView mostUsedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        databaseHelper = new DatabaseHelper(this);
        totalText = findViewById(R.id.totalText);
        favoriteCountText = findViewById(R.id.favoriteCountText);
        mutluText = findViewById(R.id.mutluText);
        normalText = findViewById(R.id.normalText);
        yorgunText = findViewById(R.id.yorgunText);
        uzgunText = findViewById(R.id.uzgunText);
        mostUsedText = findViewById(R.id.mostUsedText);

        showStatistics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showStatistics();
    }

    private void showStatistics() {
        totalText.setText("Toplam günlük sayısı: " + databaseHelper.getTotalCount());
        favoriteCountText.setText("Toplam favori günlük: " + databaseHelper.getFavoriteCount());
        mutluText.setText("Mutlu: " + databaseHelper.getMoodCount("Mutlu"));
        normalText.setText("Normal: " + databaseHelper.getMoodCount("Normal"));
        yorgunText.setText("Yorgun: " + databaseHelper.getMoodCount("Yorgun"));
        uzgunText.setText("Üzgün: " + databaseHelper.getMoodCount("Üzgün"));
        mostUsedText.setText("En çok kullanılan ruh hali: " + databaseHelper.getMostUsedMood());
    }
}
