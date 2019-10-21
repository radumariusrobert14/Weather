package com.example.application.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


public class MainActivity extends AppCompatActivity {

    private double latitude;
    private double longitude;

    private ImageView imageView;
    private TextView tempView;
    private TextView summaryView;
    private TextView precipProbabilityView;
    private TextView precipIntensityView;
    private TextView humidityView;
    private TextView windSpeedView;
    private ProgressBar progressBar;

    private WeatherViewModel viewModel;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.loading_indicator);

        viewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    viewModel.setLocation(latitude,longitude);
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0,
                    locationListener);
        }

        viewModel.getWeatherInfo().observe(this, new Observer<WeatherData>() {
            @Override
            public void onChanged(WeatherData weatherData) {
                progressBar.setVisibility(View.VISIBLE);
                updateUI(weatherData);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0,
                        locationListener);

            }
        }
    }


    private void updateUI(WeatherData currentWeatherData){
        imageView = (ImageView) findViewById(R.id.imageView);
        tempView = (TextView) findViewById(R.id.tempView);
        summaryView = (TextView) findViewById(R.id.summaryView);
        precipIntensityView = (TextView) findViewById(R.id.precipIntensityView);
        precipProbabilityView = (TextView) findViewById(R.id.precipProbabilityView);
        humidityView = (TextView) findViewById(R.id.humidityView);
        windSpeedView = (TextView) findViewById(R.id.windSpeedView);

        tempView.setText((int)Double.parseDouble(currentWeatherData.getTemperature()) + "ºC");
        summaryView.setText(currentWeatherData.getSummary());
        precipProbabilityView.setText("Precipitation probability: " + String.format( "%.0f", currentWeatherData.getPrecipProbability() * 100) +"%");
        precipIntensityView.setText("Precipitation intensity: " + String.format("%.2f", currentWeatherData.getPrecipIntensity()) + "mm/h");
        humidityView.setText("Humidity: " + String.format("%.0f", currentWeatherData.getHumidity() * 100) + "%");
        windSpeedView.setText("Wind speed: " + String.format("%.2f",currentWeatherData.getWindSpeed()) + "m/s");
        imageView.setImageResource(getWeatherImg(currentWeatherData));
        progressBar.setVisibility(View.GONE);
    }

    public int getWeatherImg(WeatherData currentWeather){
        int imgId = 0;
        switch (currentWeather.getIcon()){
            case "clear-day":
                imgId =  R.drawable.clearday;
                break;
            case "clear-night":
                imgId =  R.drawable.clearnight;
                break;
            case "partly-cloudy-day":
                imgId =  R.drawable.partlycloudyday;
                break;
            case "partly-cloudy-night":
                imgId =  R.drawable.partlycloudlynight;
                break;
            case "cloudy":
                imgId =  R.drawable.cloudy;
                break;
            case "rain":
                imgId =  R.drawable.rain;
                break;
            case "sleet":
                imgId =  R.drawable.sleet;
                break;
            case "snow":
                imgId =  R.drawable.snow;
                break;
            case "wind":
                imgId =  R.drawable.wind;
                break;
            case "fog":
                imgId =  R.drawable.fog;
                break;
        }
        return imgId;
    }
}
