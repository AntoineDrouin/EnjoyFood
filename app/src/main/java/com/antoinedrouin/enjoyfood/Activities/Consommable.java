package com.antoinedrouin.enjoyfood.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.antoinedrouin.enjoyfood.R;

public class Consommable extends AppCompatActivity {

    String nomConso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consommable);

        Bundle extras = getIntent().getExtras();
        nomConso = extras.getString(getString(R.string.nameObject), "");

        ((TextView) findViewById(R.id.txtNomConso)).setText(nomConso);
    }
}
