package com.example.yggdrasil.sensors;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener, SensorEventListener {

    private LocationManager locationManager;

    GoogleMap googleMap;
    LatLng latLng;
    TextView textViewLocation;

    private SensorManager sensorManager;
    private Sensor sensorLight;
    private TextView textViewLight;

    private TextView textViewOrientation;
    private Sensor sensorOrientation;

    private TextView textViewAccelerometer;
    private Sensor sensorAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLocation = (TextView)findViewById(R.id.textViewLocation);
        textViewLocation.setText("Location:\n\tNo GPS Signal");

        //GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, Float.parseFloat("2"), this);
        }

        if(googleServicesAvaible()){
            initMap();
        }

        textViewLight=(TextView) findViewById(R.id.textViewLight);
        textViewLight.setText("Light Sensor [lx]:\n\tNo Data");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        textViewOrientation=(TextView)findViewById(R.id.textViewOrientation);
        textViewOrientation.setText("Orientation (N):\n\tNo Data");
        sensorOrientation=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        textViewAccelerometer=(TextView)findViewById(R.id.textViewAccelerometer);
        textViewAccelerometer.setText("Accelerometer:\n\tNo Data");
        sensorAccelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    //Metody do GPS
    public void onLocationChanged(Location location){
        String msg="Location:\n\t"+location.getLatitude()+"\n\t"+location.getLongitude();
        textViewLocation.setText(msg);
        latLng=new LatLng(location.getLatitude(),location.getLongitude());//Do mapy
        Task task=new Task(googleMap,latLng);
        task.execute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        textViewLocation.setText("Location:\n\tNo GPS Signal");
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
    }

    public void onClickTrack(View view){
        Intent intent=new Intent(this,TrackActivity.class);
        startActivity(intent);
    }

    //Latarka
    private Boolean isFleshlightOn=false;
    public static Camera camera=null;
    public void fleshLight(View view){
        Button buttonFleshlight=(Button)findViewById(R.id.buttonFlashlight);
        Drawable drawable=buttonFleshlight.getBackground();
        if(!isFleshlightOn){
            try {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                    isFleshlightOn=true;
                    drawable.setColorFilter(Color.parseColor("#FF1BA918"),PorterDuff.Mode.DARKEN );
                    buttonFleshlight.setBackground(drawable);
                }
            }
            catch (Exception e) {

            }
        }
        else{
            try {
                if (getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA_FLASH)) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    isFleshlightOn=false;
                    drawable.setColorFilter(null);
                }
            }
            catch (Exception e) {

            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor==sensorLight) {
            float lightSensor = event.values[0];
            textViewLight.setText("Light Sensor [lx]:\n\t" + lightSensor);
        }
        else if(event.sensor==sensorOrientation) {
            float degree = Math.round(event.values[0]);
            degree = (degree+360)%360;
            textViewOrientation.setText("Orientation (N):\n\t" + degree+'\u00B0');
        }
        else if(event.sensor==sensorAccelerometer){
            String string="Accelerometer [m/s\u2072]:\n\tx: "+event.values[0]+"\n\ty: "+event.values[1]+"\n\tz: "+event.values[2];
            textViewAccelerometer.setText(string);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
