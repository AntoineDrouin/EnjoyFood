package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Informations extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        context = getApplicationContext();
    }
}
