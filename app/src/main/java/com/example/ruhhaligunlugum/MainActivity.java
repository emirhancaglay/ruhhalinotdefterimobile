package com.example.ruhhaligunlugum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private TextView weatherText;
    private String lastTemperature = "Bilinmiyor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherText = findViewById(R.id.weatherText);
        Button addButton = findViewById(R.id.addButton);
        Button listButton = findViewById(R.id.listButton);
        Button statsButton = findViewById(R.id.statsButton);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddDiaryActivity.class);
            intent.putExtra("temperature", lastTemperature);
            startActivity(intent);
        });

        listButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DiaryListActivity.class);
            startActivity(intent);
        });

        statsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        getWeather();
    }

    private void getWeather() {
        weatherText.setText("Hava durumu yükleniyor...");

        new Thread(() -> {
            String[] result = loadWeatherFromApi();
            runOnUiThread(() -> {
                weatherText.setText(result[0]);
                lastTemperature = result[1];
            });
        }).start();
    }

    private String[] loadWeatherFromApi() {
        HttpURLConnection connection = null;

        try {
            String urlText = "https://api.open-meteo.com/v1/forecast?latitude=41.0082&longitude=28.9784&current=temperature_2m,wind_speed_10m,weather_code";
            URL url = new URL(urlText);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return new String[]{"Hava durumu alınamadı", "Bilinmiyor"};
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject current = jsonObject.getJSONObject("current");
            double temperature = current.getDouble("temperature_2m");
            double windSpeed = current.getDouble("wind_speed_10m");
            int weatherCode = current.getInt("weather_code");

            String temperatureText = temperature + " °C";
            String weatherInfo = "Durum: " + getWeatherDescription(weatherCode) +
                    "\nSıcaklık: " + temperatureText +
                    "\nRüzgar: " + windSpeed + " km/sa";
            return new String[]{weatherInfo, temperatureText};
        } catch (Exception e) {
            return new String[]{"Hava durumu alınamadı", "Bilinmiyor"};
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String getWeatherDescription(int code) {
        if (code == 0) {
            return "Açık";
        }
        if (code <= 3) {
            return "Parçalı bulutlu";
        }
        if (code == 45 || code == 48) {
            return "Sisli";
        }
        if (code >= 51 && code <= 67) {
            return "Yağmurlu";
        }
        if (code >= 71 && code <= 77) {
            return "Karlı";
        }
        if (code >= 80 && code <= 82) {
            return "Sağanak yağışlı";
        }
        if (code >= 95) {
            return "Fırtınalı";
        }
        return "Bulutlu";
    }
}
