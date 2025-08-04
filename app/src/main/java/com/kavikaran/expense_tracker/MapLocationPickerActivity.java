package com.kavikaran.expense_tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapLocationPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Marker selectedLocationMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private Button confirmLocationBtn;
    private LatLng selectedLatLng;
    private String selectedLocationName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location_picker);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        confirmLocationBtn = findViewById(R.id.confirmLocationBtn);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        confirmLocationBtn.setOnClickListener(v -> {
            if (selectedLatLng != null && !selectedLocationName.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("location_name", selectedLocationName);
                resultIntent.putExtra("latitude", selectedLatLng.latitude);
                resultIntent.putExtra("longitude", selectedLatLng.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check and request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Set map click listener
        mMap.setOnMapClickListener(latLng -> {
            // Remove previous marker
            if (selectedLocationMarker != null) {
                selectedLocationMarker.remove();
            }

            // Add new marker
            selectedLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Location"));

            selectedLatLng = latLng;

            // Get address from coordinates
            getAddressFromLatLng(latLng);
        });
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            // Move camera to current location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        }
                    });
        }
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressBuilder = new StringBuilder();

                // Build a readable address
                if (address.getFeatureName() != null) {
                    addressBuilder.append(address.getFeatureName()).append(", ");
                }
                if (address.getThoroughfare() != null) {
                    addressBuilder.append(address.getThoroughfare()).append(", ");
                }
                if (address.getSubLocality() != null) {
                    addressBuilder.append(address.getSubLocality()).append(", ");
                }
                if (address.getLocality() != null) {
                    addressBuilder.append(address.getLocality()).append(", ");
                }
                if (address.getCountryName() != null) {
                    addressBuilder.append(address.getCountryName());
                }

                selectedLocationName = addressBuilder.toString().replaceAll(", $", "");

                // Update marker title
                if (selectedLocationMarker != null) {
                    selectedLocationMarker.setTitle(selectedLocationName);
                    selectedLocationMarker.showInfoWindow();
                }

                Toast.makeText(this, "Location selected: " + selectedLocationName, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            selectedLocationName = "Lat: " + String.format("%.6f", latLng.latitude) +
                    ", Lng: " + String.format("%.6f", latLng.longitude);
            Toast.makeText(this, "Unable to get address. Using coordinates.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}