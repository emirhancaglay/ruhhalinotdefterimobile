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
        totalText.setText(String.valueOf(databaseHelper.getTotalCount()));
        favoriteCountText.setText(String.valueOf(databaseHelper.getFavoriteCount()));
        mutluText.setText(String.valueOf(databaseHelper.getMoodCount("Mutlu")));
        normalText.setText(String.valueOf(databaseHelper.getMoodCount("Normal")));
        yorgunText.setText(String.valueOf(databaseHelper.getMoodCount("Yorgun")));
        uzgunText.setText(String.valueOf(databaseHelper.getMoodCount("Üzgün")));
        mostUsedText.setText(databaseHelper.getMostUsedMood());
    }
}
