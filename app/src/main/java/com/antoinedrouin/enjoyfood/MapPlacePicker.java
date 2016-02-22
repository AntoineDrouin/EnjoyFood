package com.antoinedrouin.enjoyfood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapPlacePicker extends Activity {

    Context context;

    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_place_picker);

        context = getApplicationContext();

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    // S'execute lorsqu'un point d'intérêt est sélectionné
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = com.google.android.gms.location.places.ui.PlacePicker.getPlace(this, data);

            // Insère l'établissement dans la base SQLite
            SQLiteDatabase dbEF = openOrCreateDatabase(getString(R.string.varDbName), MODE_PRIVATE, null);

            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            String ville = "";

            try {
                addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                if (addresses.size() > 0)
                    ville = addresses.get(0).getLocality();

                Cursor loadEtabs = dbEF.rawQuery("Select nomEt from Etablissement where nomEt = '" + place.getName() + "'", null);
                loadEtabs.moveToFirst();

                // Si l'établissement n'est pas déjà dans la base
                if (!loadEtabs.moveToFirst()) {
                    dbEF.execSQL("Insert into Etablissement (nomEt, adresseEt, villeEt) values ('" +
                            place.getName() + "', '" + place.getAddress() + "', '" + ville + "')");

                    // Et met à jour la listview
                    Etablissements.getInstance().fillLvWithDb();
                }

                Intent intentEtab = new Intent(this, Etablissement.class);
                intentEtab.putExtra(getString(R.string.extraEtabName), place.getName());
                startActivity(intentEtab);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finish();
    }
}
