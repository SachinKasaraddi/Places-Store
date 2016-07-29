package com.sachi.placesstore.map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sachi.placesstore.R;
import com.sachi.placesstore.util.PlacesStoreConstants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by sachin.kasaraddi on 26/07/16.
 */
public class MapPresenterImpl implements MapPresenter {

    private static MapPresenterImpl instance;
    private static MapWindowView mMapWindowView;
    private static Context mContext;
    private Location mLastLocation;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private String address;
    private String locality;
    // GPSTracker class
    GPSTracker gps;
    LatLng ll;
    String city;
    String state;
    String country;
    String postalCode;
    String knownName;
    public static MapPresenterImpl getInstance(MapWindowView mapWindowView) {
        if (instance == null)
            instance = new MapPresenterImpl();
        mMapWindowView = mapWindowView;
        mContext = (Context) mMapWindowView;
        return instance;
    }

    public static MapPresenterImpl getInstance() {
        if (instance == null)
            instance = new MapPresenterImpl();
        return instance;
    }

    @Override
    public void getCurrentLocation(GoogleMap googleMap, final GoogleApiClient googleApiClient) {
        this.mGoogleMap = googleMap;
        this.mGoogleApiClient = googleApiClient;
        gps = new GPSTracker(mContext);
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    gotoLocationZoom(latitude, longitude, 15);
                    mMapWindowView.setMarker("Null", latitude, longitude);
                }
            }
        });
    }

    @Override
    public void setInfoWindowText(final Marker marker, View view) {

        TextView tvLocality = (TextView) view.findViewById(R.id.tv_locality);
        TextView tvLat = (TextView) view.findViewById(R.id.tv_lat);
        TextView tvLng = (TextView) view.findViewById(R.id.tv_lng);
        TextView tvSnippet = (TextView) view.findViewById(R.id.tv_snippet);
        RelativeLayout imgInfo = (RelativeLayout) view.findViewById(R.id.rel_layout);
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(mContext,"Clicked",Toast.LENGTH_SHORT).show();
                gotoLocationActivity();
            }
        });
        ll = marker.getPosition();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            locality = addresses.get(0).getLocality();
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tvLocality.setText(locality);
        tvLat.setText("Latitude: " + ll.latitude);
        tvLng.setText("Longitude: " + ll.longitude);
        tvSnippet.setText(marker.getSnippet());
     }

    public void gotoLocationActivity(){
                Bundle mBundle = new Bundle();
                mBundle.putInt(PlacesStoreConstants.VISIBILITY,0);
                mBundle.putString(PlacesStoreConstants.TITLE, locality);
                mBundle.putDouble(PlacesStoreConstants.LATITUDE, ll.latitude);
                mBundle.putDouble(PlacesStoreConstants.LONGITUDE, ll.longitude);
                mBundle.putString(PlacesStoreConstants.ADDRESS, address +'\n'+knownName+'\n'+city+"-"+postalCode
                        +'\n'+state+'\n'+country );
                Intent intent  = new Intent(mContext,LocationInfoActivity.class);
                intent.putExtras(mBundle);
                mContext.startActivity(intent);
    }

    public void gotoLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMapWindowView.setMap(cameraUpdate);
    }

}
