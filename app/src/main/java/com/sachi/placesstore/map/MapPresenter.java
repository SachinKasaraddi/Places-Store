package com.sachi.placesstore.map;

import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by sachin.kasaraddi on 26/07/16.
 */
public interface MapPresenter {

    void getCurrentLocation(GoogleMap mGoogleMap, GoogleApiClient googleApiClient);
    void setInfoWindowText(Marker marker, View view);
    void gotoLocationActivity();
    }
