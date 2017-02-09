package com.example.yggdrasil.sensors;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener{

    private LocationManager locationManager;
    LatLng latLng;
    LatLng lastLatLng;
    GoogleMap googleMap;
    Marker lastMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        //GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, Float.parseFloat("2"), (LocationListener) this);
        }

        if(googleServicesAvaible()){
            initMap();
        }
    }

    public void onLocationChanged(Location location){
        latLng=new LatLng(location.getLatitude(),location.getLongitude());//Do mapy
        goToLocation(latLng.latitude, latLng.longitude, 17);//Do mapy
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
    //Koniec

    private void initMap() {
        MapFragment mapFragment=(MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvaible(){
        GoogleApiAvailability api=GoogleApiAvailability.getInstance();
        int isAvaible=api.isGooglePlayServicesAvailable(this);
        if (isAvaible == ConnectionResult.SUCCESS){
            return true;
        }
        else if(api.isUserResolvableError(isAvaible)){
            Dialog dialog=api.getErrorDialog(this, isAvaible,0);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Can't connect to play services",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
    }

    private void goToLocation(double lat, double lng, float zoom) {
        LatLng ll =new LatLng(lat,lng);
        if (lastLatLng==null){
            lastLatLng=ll;
        }
        googleMap.addPolyline(new PolylineOptions().add(lastLatLng,ll).width(5).color(Color.RED));
        lastLatLng=ll;
        MarkerOptions markerOptions=new MarkerOptions().position(ll);
        if(lastMarker!=null){
            lastMarker.remove();
        }
        Marker marker=googleMap.addMarker(markerOptions);
        lastMarker=marker;
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(ll,zoom);
        googleMap.moveCamera(update);
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
