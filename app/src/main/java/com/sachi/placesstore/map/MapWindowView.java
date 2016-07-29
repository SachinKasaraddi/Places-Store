package com.sachi.placesstore.map;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;

/**
 * Created by sachin.kasaraddi on 26/07/16.
 */
public interface MapWindowView {
    void setMarker(String locality, double lat, double lng);
    void setMap(CameraUpdate cameraUpdate);

}
