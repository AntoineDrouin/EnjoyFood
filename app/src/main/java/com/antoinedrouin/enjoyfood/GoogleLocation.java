package com.antoinedrouin.enjoyfood;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by cdsm04 on 22/02/2016.
 */

public class GoogleLocation implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Context context;
    Activity activity;
    GoogleApiClient mGoogleApiClient;
    boolean popup;

    Address address;

    public GoogleLocation(Context gcontext, boolean gPopup) {
        context = gcontext;
        popup = gPopup;

       connect();
    }

    public GoogleLocation(Context gcontext, Activity gActivity, boolean gPopup) {
        context = gcontext;
        activity = gActivity;
        popup = gPopup;

        connect();
    }

    private void connect() {
        // Crée une instance de GoogleApi
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        double lat, lon;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            try {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();

                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(lat, lon, 1);

                if (addresses.size() > 0)
                    address = addresses.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            if (popup) {
                new AlertDialog.Builder(activity)
                        .setMessage(context.getString(R.string.geolocationFailed))
                        .setCancelable(false)
                        .setNegativeButton(context.getString(R.string.varNo), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton(context.getString(R.string.varYes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, context.getString(R.string.geolocationFailed), Toast.LENGTH_SHORT).show();
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    public String getCity() {
        return address.getLocality();
    }

    public String getCp() {
        return address.getPostalCode();
    }

    public String getAddress() {
        return address.getAddressLine(0);
    }

    public String getFullAddress() {
        String value = "";

        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            value += address.getAddressLine(i);
            if (i < address.getMaxAddressLineIndex())
                value += ", ";
        }

        return value;
    }
}
