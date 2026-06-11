package com.example.ruhhaligunlugum;

public class DiaryModel {
    private int id;
    private String title;
    private String note;
    private String mood;
    private String date;
    private String temperature;
    private boolean favorite;

    public DiaryModel(int id, String title, String note, String mood, String date, String temperature, boolean favorite) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.mood = mood;
        this.date = date;
        this.temperature = temperature;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getMood() {
        return mood;
    }

    public String getDate() {
        return date;
    }

    public String getTemperature() {
        return temperature;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
