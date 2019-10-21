package com.example.application.weather;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public final class Utils {

    public static URL getQueryURL (double latitude, double longitude){
        String latitudeString = Double.toString(latitude);
        String longitudeString = Double.toString(longitude);
        String stringURLWeather = "https://api.darksky.net/forecast/4f8a53d2c363fdcf39eba4d32d59ea19/" + latitudeString + "," + longitudeString
                + "?units=si&exclude=hourly,daily,flags";
        URL url = null;
        // convert to URL
        try {
            url = new URL(stringURLWeather);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return  output.toString();
    }

    public static String makeHttpRequest (URL url) throws IOException {
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    public static WeatherData extractWeatherData(String jsonResponse){

        String summary = null;
        String icon = null;
        Double precipIntensity = null;
        Double precipProbability = null;
        String temperature = null;
        Double humidity = null;
        Double windSpeed = null;

        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        try {
            JSONObject weatherJSON = new JSONObject(jsonResponse);
            JSONObject currently = weatherJSON.getJSONObject("currently");
            summary = currently.getString("summary");
            icon = currently.getString("icon");
            precipIntensity = currently.getDouble("precipIntensity");
            precipProbability = currently.getDouble("precipProbability");
            temperature = currently.getString("temperature");
            humidity = currently.getDouble("humidity");
            windSpeed = currently.getDouble("windSpeed");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WeatherData currentWeather = new WeatherData(summary,icon,precipIntensity,precipProbability,temperature,humidity,windSpeed);

        return currentWeather;
    }

}

