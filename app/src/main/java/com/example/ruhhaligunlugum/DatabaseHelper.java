package com.example.ruhhaligunlugum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ruh_hali_gunlugum.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "diaries";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "note TEXT, " +
                "mood TEXT, " +
                "date TEXT, " +
                "temperature TEXT, " +
                "isFavorite INTEGER DEFAULT 0)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN temperature TEXT DEFAULT 'Bilinmiyor'");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN isFavorite INTEGER DEFAULT 0");
        }
    }

    public void addDiary(String title, String note, String mood, String date, String temperature) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("mood", mood);
        values.put("date", date);
        values.put("temperature", temperature);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateDiary(int id, String title, String note, String mood, String temperature) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("mood", mood);
        values.put("temperature", temperature);
        db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<DiaryModel> getAllDiaries() {
        return getDiaries("", "Tümü", "En Yeni");
    }

    public ArrayList<DiaryModel> getDiaries(String searchText, String moodFilter) {
        return getDiaries(searchText, moodFilter, "En Yeni");
    }

    public ArrayList<DiaryModel> getDiaries(String searchText, String moodFilter, String sortOrder) {
        ArrayList<DiaryModel> diaryList = new ArrayList<>();
        ArrayList<String> args = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE 1=1";

        if (searchText != null && !searchText.trim().isEmpty()) {
            sql += " AND (title LIKE ? OR note LIKE ?)";
            String search = "%" + searchText.trim() + "%";
            args.add(search);
            args.add(search);
        }

        if (moodFilter != null && !moodFilter.equals("Tümü")) {
            sql += " AND mood=?";
            args.add(moodFilter);
        }

        if ("En Eski".equals(sortOrder)) {
            sql += " ORDER BY id ASC";
        } else {
            sql += " ORDER BY id DESC";
        }
        Cursor cursor = db.rawQuery(sql, args.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                diaryList.add(createDiaryFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return diaryList;
    }

    public DiaryModel getDiaryById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{String.valueOf(id)});
        DiaryModel diary = null;

        if (cursor.moveToFirst()) {
            diary = createDiaryFromCursor(cursor);
        }

        cursor.close();
        db.close();
        return diary;
    }

    public void deleteDiary(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isFavorite", isFavorite ? 1 : 0);
        db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getTotalCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public int getMoodCount(String mood) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE mood=?", new String[]{mood});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public int getFavoriteCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE isFavorite=1", null);
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public String getMostUsedMood() {
        String[] moods = {"Mutlu", "Normal", "Yorgun", "Üzgün"};
        String mostUsedMood = "Yok";
        int maxCount = 0;

        for (String mood : moods) {
            int count = getMoodCount(mood);
            if (count > maxCount) {
                maxCount = count;
                mostUsedMood = mood;
            }
        }

        return mostUsedMood;
    }

    private DiaryModel createDiaryFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
        String mood = cursor.getString(cursor.getColumnIndexOrThrow("mood"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        String temperature = "Bilinmiyor";
        int temperatureIndex = cursor.getColumnIndex("temperature");
        boolean favorite = false;
        int favoriteIndex = cursor.getColumnIndex("isFavorite");

        if (temperatureIndex >= 0) {
            temperature = cursor.getString(temperatureIndex);
            if (temperature == null || temperature.isEmpty()) {
                temperature = "Bilinmiyor";
            }
        }

        if (favoriteIndex >= 0) {
            favorite = cursor.getInt(favoriteIndex) == 1;
        }

        return new DiaryModel(id, title, note, mood, date, temperature, favorite);
    }
}
