package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.R;

public class CommandeDetails extends AppCompatActivity {

    Context context;

    String idEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commande_details);

        context = getApplicationContext();

        Bundle extras = getIntent().getExtras();
        idEt = extras.getString(getString(R.string.extraEtabId), "");
    }
}
