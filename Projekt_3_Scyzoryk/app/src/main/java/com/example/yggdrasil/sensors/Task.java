package com.example.yggdrasil.sensors;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Yggdrasil on 2016-12-04.
 */

public class Task extends AsyncTask <Void,Void,Void>{
    GoogleMap googleMap;
    LatLng latLng;

    public Task(GoogleMap _googleMap, LatLng _latLng){
        googleMap=_googleMap;
        latLng=_latLng;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(googleMap!=null && latLng!=null) {
            googleMap.clear();
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            googleMap.addMarker(markerOptions);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            googleMap.moveCamera(update);
        }
    }
}
