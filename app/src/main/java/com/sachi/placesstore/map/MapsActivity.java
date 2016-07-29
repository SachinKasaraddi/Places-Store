package com.sachi.placesstore.map;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sachi.placesstore.R;
import com.sachi.placesstore.db.Travel;
import com.sachi.placesstore.db.TravelStore;
import com.sachi.placesstore.util.PlacesStoreConstants;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;


public class MapsActivity extends AppCompatActivity implements MapWindowView, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @Bind(R.id.edit_title)
    AutoCompleteTextView titleText;

    private GoogleMap mGoogleMap;
    private MapPresenter mapPresenter;
    private GoogleApiClient mGoogleApiClient;
    private Marker mMarker;
    RelativeLayout imgInfo;
    private ArrayAdapter<Travel> travelRealmAdapter;
    ArrayList<String> travelRouteStored;
    AutoCompleteAdapter autoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googlePlayServicesAvailability()) {
            setContentView(R.layout.activity_main);
            initMap();
        }
        ButterKnife.bind(this);
        autoCompleteAdapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, readAllTravelRoutes());
        titleText.setAdapter(autoCompleteAdapter);
        titleText.setThreshold(1);
        titleText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        titleText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                String title = textView.getText().toString();
                Travel travel = new Travel();
                travel = TravelStore.getInstance(MapsActivity.this).getAllTravelRoutes(title);
                Bundle bundle = new Bundle();
                bundle.putInt(PlacesStoreConstants.VISIBILITY, 1);
                bundle.putString(PlacesStoreConstants.TITLE, travel.getTitle());
                bundle.putString(PlacesStoreConstants.ADDRESS, travel.getAddress());
                bundle.putDouble(PlacesStoreConstants.LATITUDE, Double.parseDouble(travel.getLatitude()));
                bundle.putDouble(PlacesStoreConstants.LONGITUDE, Double.parseDouble(travel.getLongitude()));
                Intent intent = new Intent(MapsActivity.this, LocationInfoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        mapPresenter = MapPresenterImpl.getInstance(this);

    }


    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_types, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;

            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleApiClient.connect();
        mapPresenter.getCurrentLocation(googleMap,mGoogleApiClient);

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                mapPresenter.setInfoWindowText(marker,v);
                return v;
            }
        });
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                mapPresenter.gotoLocationActivity();
            }
        });
    }




    public boolean googlePlayServicesAvailability() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int status = apiAvailability.isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            return true;
        } else if (apiAvailability.isUserResolvableError(status)) {
            Dialog dialog = apiAvailability.getErrorDialog(this, status, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot Connect to play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void setMarker(String locality, double lat, double lng) {
        LatLng latLng = new LatLng(lat,lng);
        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public void setMap(CameraUpdate cameraUpdate) {
        mGoogleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            Toast.makeText(this, "Cannot get current Location", Toast.LENGTH_SHORT).show();
        } else {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mGoogleMap.animateCamera(cameraUpdate);

        }

    }


    private ArrayList<String> readAllTravelRoutes() {
        travelRouteStored = new ArrayList<String>();
       RealmResults<Travel> results = TravelStore.getInstance(this).getAllTravels();
        for(int i =0;i<results.size();i++){
            travelRouteStored.add(results.get(i).getTitle());
        }
        titleText.setAdapter(travelRealmAdapter);
        return travelRouteStored;
    }
}
