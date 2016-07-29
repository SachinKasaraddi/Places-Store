package com.sachi.placesstore.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sachi.placesstore.R;
import com.sachi.placesstore.db.Travel;
import com.sachi.placesstore.db.TravelStore;
import com.sachi.placesstore.util.PlacesStoreConstants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sachin.kasaraddi on 28/07/16.
 */
public class LocationInfoActivity extends AppCompatActivity {

    @Bind(R.id.txt_title)
    TextView txtTitle;

    @Bind(R.id.edit_title)
    EditText editTitle;

    @Bind(R.id.txt_address)
    TextView txtAddress;

    @Bind(R.id.txt_latitude)
    TextView txtLatitude;

    @Bind(R.id.txt_longitude)
    TextView txtLongitude;

    @Bind(R.id.btn_save_address)
    Button btnSaveAddress;

    private double latitude,longitude;
    private String title,address;
    private int visibility;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent!=null){
            title = intent.getStringExtra(PlacesStoreConstants.TITLE);
            latitude = intent.getDoubleExtra(PlacesStoreConstants.LATITUDE,0);
            longitude = intent.getDoubleExtra(PlacesStoreConstants.LATITUDE,0);
            address = intent.getStringExtra(PlacesStoreConstants.ADDRESS);
            visibility = intent.getIntExtra(PlacesStoreConstants.VISIBILITY,0);

            txtAddress.setText(address);
            txtLatitude.setText(""+latitude);
            txtLongitude.setText(""+longitude);

        }
        if (visibility == 1){
            btnSaveAddress.setVisibility(View.INVISIBLE);
            txtTitle.setText(title);
            txtTitle.setVisibility(View.VISIBLE);
        }else {
            editTitle.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.btn_save_address)
    public void saveAddress(){

       title =  editTitle.getText().toString();
        if(!title.toString().equals("")){
            Travel travel = new Travel();
            travel.setTitle(title);
            travel.setAddress(address);
            travel.setLatitude(""+latitude);
            travel.setLongitude(""+longitude);

            TravelStore.getInstance(this).writeMessage(travel);
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(LocationInfoActivity.this,"Enter Title",Toast.LENGTH_SHORT).show();
        }

    }
}
