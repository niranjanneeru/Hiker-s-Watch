package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView latitude,longitude,accuracy,altitude,addressTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.lat);
        longitude = findViewById(R.id.longitude);
        accuracy = findViewById(R.id.accuracy);
        altitude = findViewById(R.id.altitude);
        addressTV = findViewById(R.id.address);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateInfo(location);
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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(lastLocation!=null){
                updateInfo(lastLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==1 ){
            if(grantResults.length>0){
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startListening();
                }
            }
        }
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    public void updateInfo(Location location) {
        String lat = "Latitude: "+location.getLatitude();
        try {
            latitude.setText(lat.substring(0, 17));
        }catch (Exception e){
            latitude.setText(lat);
        }
        String longi = "Longitude: "+location.getLongitude();
        try {
            longitude.setText(longi.substring(0, 18));
        }catch (Exception e){
            longitude.setText(longi);
        }
        String alti = "Altitude: "+location.getAltitude();
        try {
            altitude.setText(alti.substring(0, 17));
        }catch (Exception e){
            altitude.setText(alti);
        }
            accuracy.setText("Accuracy: "+location.getAccuracy());

        String address = "Address:\nUnable to Process";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
        List<Address> list = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        if(list != null){
            if(list.size()>0){
                address = "Address:\n";
                if(list.get(0).getThoroughfare() != null){
                    address += list.get(0).getThoroughfare() + '\n';
                }
                if(list.get(0).getLocality() != null){
                    address += list.get(0).getLocality() + " ";
                }
                if(list.get(0).getPostalCode() != null){
                    address += list.get(0).getPostalCode() + " ";
                }
                if(list.get(0).getAdminArea() != null){
                    address += list.get(0).getAdminArea();
                }
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
        addressTV.setText(address);
    }
}