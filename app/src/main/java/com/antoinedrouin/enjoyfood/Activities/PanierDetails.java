package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.antoinedrouin.enjoyfood.R;

public class PanierDetails extends AppCompatActivity {

    Context context;

    String idEt, nomEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier_details);

        context = getApplicationContext();
        Bundle extras = getIntent().getExtras();

        idEt = extras.getString(getString(R.string.extraEtabId), "");
        nomEt = extras.getString(getString(R.string.extraEtabName), "");
    }
}
