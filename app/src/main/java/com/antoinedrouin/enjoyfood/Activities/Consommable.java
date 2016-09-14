package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
    String idUt, idEt, nomEt, nomConso;
    int quantity;
    ScrollView scrollViewConso;
    RelativeLayout layoutLoading;
    TextView txtQuantity, txtDesc, txtPrice;
    FloatingActionButton btnLess;
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
        btnLess = (FloatingActionButton) findViewById(R.id.btnLess);
        scrollViewConso = (ScrollView) findViewById(R.id.scrollViewConso);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);

        Bundle extras = getIntent().getExtras();
        idUt = extras.getString(getString(R.string.prefId), "");
        idEt = extras.getString(getString(R.string.extraEtabId), "");
        nomEt = extras.getString(getString(R.string.extraEtabName), "");
        nomConso = extras.getString(getString(R.string.nameObject), "");

        scrollViewConso.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        txtNomConso.setText(nomConso);

        // Création de la bdd si elle n'existe pas
        dbEF = openOrCreateDatabase(getString(R.string.varDbName), MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        dbEF.execSQL("CREATE TABLE IF NOT EXISTS PANIER (idUt VARCHAR, idEt VARCHAR, nomEt VARCHAR, nomConso VARCHAR, qteConso INTEGER)");

        Cursor loadConsos = dbEF.rawQuery("Select qteConso From Panier Where idUt = ? and idEt = ? and nomConso = ?", new String[]{idUt, idEt, nomConso});

        // Cherche la quantité de ce consommable
        if (loadConsos.moveToFirst())
            quantity = loadConsos.getInt(loadConsos.getColumnIndex("qteConso"));
        else
            quantity = 0;

        majQte();
        txtQuantity.setText(Integer.toString(quantity));
        loadConsos.close();

        ServerSide getConso = new ServerSide(context);
        getConso.execute(getString(R.string.getConso), getString(R.string.read), idEt, nomConso);
    }

    public void onClickLess(View v) {
        quantity--;
        txtQuantity.setText(Integer.toString(quantity));

        // Delete si quantité = 0
        if (quantity == 0)
            dbEF.execSQL("Delete from Panier Where idUt = ? and idEt = ? and nomConso = ?", new String[]{idUt, idEt, nomConso});
        else
            dbEF.execSQL("Update Panier Set qteConso = ? Where idUt = ? and idEt = ? and nomConso = ?", new String[]{Integer.toString(quantity), idUt, idEt, nomConso});

        majQte();
    }

    public void onClickMore(View v) {
        quantity++;
        txtQuantity.setText(Integer.toString(quantity));

        if (quantity > 1)
            dbEF.execSQL("Update Panier Set qteConso = ? Where idUt = ? and idEt = ? and nomConso = ?", new String[]{Integer.toString(quantity), idUt, idEt, nomConso});
        else
            dbEF.execSQL("Insert into Panier (idUt, idEt, nomEt, nomConso, qteConso) values (?, ?, ?, ?, ?)",  new String[]{idUt, idEt, nomEt, nomConso, Integer.toString(quantity)});

        majQte();
    }

    public void getConso(String[][] conso) {
        txtDesc.setText(conso[0][0]);
        txtPrice.setText(conso[0][1]);
        scrollViewConso.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
    }

    public void majQte() {
        if (quantity > 0)
            btnLess.setVisibility(View.VISIBLE);
        else
            btnLess.setVisibility(View.GONE);
    }

    public static Consommable getInstance() {
        return instConso;
    }
}
