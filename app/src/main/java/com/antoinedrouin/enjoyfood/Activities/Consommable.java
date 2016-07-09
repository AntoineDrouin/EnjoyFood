package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.R;

public class Consommable extends AppCompatActivity {

    Context context;
    static Consommable instConso;
    String idEt, nomConso;
    int quantity;
    ScrollView scrollViewConso;
    RelativeLayout layoutLoading;
    TextView txtQuantity, txtDesc, txtPrice;
    SQLiteDatabase dbEF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consommable);

        context = getApplicationContext();
        instConso = this;

        TextView txtNomConso = (TextView) findViewById(R.id.txtNomConso);
        txtQuantity = (TextView) findViewById(R.id.txtQuantity);
        txtDesc = (TextView) findViewById(R.id.txtDescConso);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        scrollViewConso = (ScrollView) findViewById(R.id.scrollViewConso);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);

        Bundle extras = getIntent().getExtras();
        idEt = extras.getString(getString(R.string.extraEtabId), "");
        nomConso = extras.getString(getString(R.string.nameObject), "");

        scrollViewConso.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        txtNomConso.setText(nomConso);

        // Création de la bdd si elle n'existe pas
        dbEF = openOrCreateDatabase(getString(R.string.varDbName), context.MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
//        dbEF.execSQL("CREATE TABLE IF NOT EXISTS Etablissement (idEt VARCHAR, nomEt VARCHAR, adresseEt VARCHAR, villeEt VARCHAR, codePostalEt VARCHAR)");

        ServerSide getConso = new ServerSide(context);
        getConso.execute(getString(R.string.getConso), getString(R.string.read), idEt, nomConso);
    }

    public void onClickLess(View v) {
        quantity = Integer.parseInt(txtQuantity.getText().toString());

        if (quantity > 0) {
            txtQuantity.setText(Integer.toString(quantity - 1));
        }
    }

    public void onClickMore(View v) {
        quantity = Integer.parseInt(txtQuantity.getText().toString());
        txtQuantity.setText(Integer.toString(quantity + 1));
    }

    public void getConso(String[][] conso) {
        txtDesc.setText(conso[0][0]);
        txtPrice.setText(conso[0][1]);
        scrollViewConso.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
    }

    public void onClickAddToCart(View v) {
        finish();
    }

    public static Consommable getInstance() {
        return instConso;
    }
}
