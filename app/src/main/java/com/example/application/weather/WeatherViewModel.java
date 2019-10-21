package com.example.application.weather;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.net.URL;

public class WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<WeatherData> currentWeather = new MutableLiveData<>();

    public WeatherViewModel(@NonNull Application application) {
        super(application);
    }

    public void setLocation(double latitude, double longitude) {
        new WeatherAsyncTask(latitude, longitude, new WeatherInformation() {
            @Override
            public void onBackgroundTaskCompleted(WeatherData weatherData) {
                currentWeather.setValue(weatherData);
            }
        }).execute();
    }

    public LiveData<WeatherData> getWeatherInfo(){
        return currentWeather;
    }

    private static class WeatherAsyncTask extends AsyncTask<Void, Void, WeatherData> {
        private double latitude;
        private double longitude;
        private WeatherInformation caller;

        public WeatherAsyncTask(double latitude, double longitude, WeatherInformation caller) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.caller = caller;
        }

        @Override
        protected WeatherData doInBackground(Void... voids) {
            URL url = Utils.getQueryURL(latitude,longitude);
            String jsonResponse = "";
            try {
                jsonResponse = Utils.makeHttpRequest(url);
            } catch (IOException e) {
            }
            WeatherData currentWeatherData = Utils.extractWeatherData(jsonResponse);
            return currentWeatherData;
        }

        @Override
        protected void onPostExecute(WeatherData weatherData) {
            caller.onBackgroundTaskCompleted(weatherData);
        }
    }

}
