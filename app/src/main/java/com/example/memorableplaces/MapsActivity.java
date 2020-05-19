package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    JSONArray poiArray = new JSONArray();
    String r;
    Boolean isCurrentMarkerPresent = false;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0,locationListener);
            }
        }
    }

    public void addCurrentPositionMarker(LatLng loc){
        Log.i("Position is --- ", loc.toString());
//        LatLng position = new LatLng(-34, 151);
        if(!isCurrentMarkerPresent){
            mMap.addMarker(new MarkerOptions().position(loc).title("Current Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            isCurrentMarkerPresent = true;
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        poiArray = new JSONArray();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                Log.i("xxxxxxx", location.toString());
                positionChangeEvent(location);
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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0,locationListener);
        }
    }

    public void positionChangeEvent(Location location){
        Log.i("xxxxxxx", location.toString());
        addCurrentPositionMarker(new LatLng(location.getLatitude(), location.getLongitude()));


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addPlaceOfInterest(LatLng poi){
        String poiString;
        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        double lat = poi.latitude;
        double lng = poi.longitude;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        try{
            List<Address> currentAddress = geoCoder.getFromLocation(lat, lng, 1);
            JSONObject addressDetail = new JSONObject();
            if(currentAddress != null && currentAddress.size() >0){
                Address address = currentAddress.get(0);
                String street = address.getAddressLine(0) != null ? address.getAddressLine(0): null;
                String area = address.getAdminArea() != null ? address.getAdminArea(): null;

                if(street != null){
                    poiString = street;
                }else if(area != null){
                    poiString = area;
                }else{
                    poiString = "Address Unavailable";
                }
                addressDetail.put("name", poiString);
                addressDetail.put("position", new LatLng(address.getLatitude(), address.getLongitude()));
                poiArray.put(addressDetail);

                Intent i = new Intent();
                i.putExtra("poiList",  poiArray.toString());
                setResult(RESULT_OK, i);


            }
        }catch(Exception e){
            e.printStackTrace();
        }


        mMap.addMarker((new MarkerOptions().position(poi).title(date).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMapLongClick(LatLng latLng) {
                addPlaceOfInterest(latLng);
            }
        });

    }
}
