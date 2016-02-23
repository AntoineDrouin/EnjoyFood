package com.antoinedrouin.enjoyfood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Panier extends Activity {

    Context context;

    static Panier instPanier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        context = getApplicationContext();

        instPanier = this;
    }

    public void onClickOpenMaps(View v) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void searchInLv() {

    }

    public static Panier getInstance() {
        return instPanier;
    }

}
