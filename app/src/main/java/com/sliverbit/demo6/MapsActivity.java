package com.sliverbit.demo6;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestQueue = Volley.newRequestQueue(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        UiSettings settings = mMap.getUiSettings();

        settings.setAllGesturesEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMapToolbarEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setZoomControlsEnabled(true);

        String urlLocation = "http://microapi.theride.org/Location/4";

        GsonRequest<Location[]> locationRequest = new GsonRequest<>(urlLocation, Location[].class, null,
                new Response.Listener<Location[]>() {
                    @Override
                    public void onResponse(Location[] response) {
                        if (response != null && response.length > 0) {
                            LatLng busLatLng = null;
                            Marker busMarker = null;

                            for (Location busLocation : response) {
                                String lat = busLocation.getLat();
                                String lng = busLocation.getLongitude();

                                busLatLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

                                busMarker = mMap.addMarker(new MarkerOptions()
                                        .position(busLatLng)
                                        .title(busLocation.getAdherenceText())
                                        .snippet("Bus# " + busLocation.getBusNum() + " Updated: " + busLocation.getTimestamp())
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_bus)));
                            }

                            if (busLatLng != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLatLng, 11));
                            }
                            if (busMarker != null) {
                                busMarker.showInfoWindow();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_location_data, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        requestQueue.add(locationRequest);
    }
}
