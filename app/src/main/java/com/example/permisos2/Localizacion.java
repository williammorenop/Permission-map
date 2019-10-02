package com.example.permisos2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission_group.LOCATION;

public class Localizacion extends AppCompatActivity {

    private final static int LOCATION_PERMISSION = 0;
    private static final double RADIUS_OF_EARTH_KM = 6371;
    private static final double LATPLAZA = 4.5981259;
    private static final double LONGPLAZA = -74.0782322;

    private TextView latitud;
    private TextView longitud;
    private TextView altitud;
    private TextView distancia;
    private Button boton;

    //JSON
    private JSONArray locations;
    //linear
    private LinearLayout linear;
    //una
    private FusedLocationProviderClient mFusedLocationClient;
    //varias
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacion);
        /////////////
        latitud = findViewById(R.id.lat);
        longitud = findViewById(R.id.longi);
        altitud = findViewById(R.id.altitud);
        distancia = findViewById(R.id.distancia);
        boton = findViewById(R.id.button);
        linear = findViewById(R.id.historial);
        locations = new JSONArray();
/////////////Permiso
        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Se necesita acceder a la ubicacion", LOCATION_PERMISSION);
        /////////
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = createLocationRequest();

        ///////////// Encender ubicacion
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates(); //Todas las condiciones para recibir localizaciones
            }
        });
//////dialogo para activar localizacion
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
// Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                        try {// Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(Localizacion.this,
                                    LOCATION_PERMISSION);
                        } catch (IntentSender.SendIntentException sendEx) {
// Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
// Location settings are not satisfied. No way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });


        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addlocation();
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    latitud.setText("Latitud: " + String.valueOf(location.getLatitude()));
                    longitud.setText("Longitud: " + String.valueOf(location.getLongitude()));
                    altitud.setText("Altitud: " + String.valueOf(location.getAltitude()));
                    distancia.setText("Distancia: " + String.valueOf(distance(location.getLatitude(),
                            location.getLongitude(), LATPLAZA, LONGPLAZA)) + " Km");
                }
            }
        };
    }

    /////////////////// on resume startupdates , onpause stop it
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    ///////////////////////Distancia
    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result * 100.0) / 100.0;
    }

    /////////////////////star and stop location updates

    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    //////////////////Permisos


    private void requestPermission(Activity context, String permission, String explanation, int requestId) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                Toast.makeText(context, explanation, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestId);
        }
    }

    ///////////////////

    public void addlocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    latitud.setText("Latitud: " + String.valueOf(location.getLatitude()));
                    longitud.setText("Longitud: " + String.valueOf(location.getLongitude()));
                    altitud.setText("Altitud: " + String.valueOf(location.getAltitude()));
                    distancia.setText("Distancia: " + String.valueOf(distance(location.getLatitude(),
                            location.getLongitude(), LATPLAZA, LONGPLAZA)) + " Km");

                    //Guardar en JSON

                    MyLocation myLocation = new MyLocation();
                    myLocation.setFecha(new Date(System.currentTimeMillis()));
                    myLocation.setLatitud(location.getLatitude());
                    myLocation.setLongitud(location.getLongitude());
                    locations.put(myLocation.toJSON());
                    //añadir en linear historial
                    TextView locati = new TextView(getApplicationContext());
                    locati.setText(myLocation.getFecha() + "  " + myLocation.getLatitud() + "  " + myLocation.getLongitud());
                    linear.addView(locati);
                    //////
                    Writer output = null;
                    String filename = "locations.json";
                    try {
                        File file = new File(getBaseContext().getExternalFilesDir(null), filename);
                        Log.i("LOCATION", "Ubicacion de archivo: " + file);
                        output = new BufferedWriter(new FileWriter(file));
                        output.write(locations.toString());
                        output.close();
                        Toast.makeText(getApplicationContext(), "Location saved",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "ERROR!!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


}
