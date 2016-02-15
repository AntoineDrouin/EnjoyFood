package com.antoinedrouin.enjoyfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Panier extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);
    }

    public void onClickOpenMaps(View v) {
        startActivity(new Intent(this, MapsActivity.class));
    }
}
