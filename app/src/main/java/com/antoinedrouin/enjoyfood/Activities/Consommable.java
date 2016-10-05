package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.antoinedrouin.enjoyfood.Classes.Conso;
import com.antoinedrouin.enjoyfood.Classes.Etab;
import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

public class Consommable extends AppCompatActivity {

    private Context context;
    private static Consommable instConso;
    private SharedPreferences pref;

    private Etab etab;
    private Conso conso;

    private ScrollView scrollViewConso;
    private RelativeLayout layoutLoading;
    private TextView txtQuantity, txtDesc, txtPrice;
    private FloatingActionButton btnLess, btnMore;
    private SQLiteDatabase dbEF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consommable);

        context = getApplicationContext();
        instConso = this;
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        TextView txtNomConso = (TextView) findViewById(R.id.txtNomConso);
        txtQuantity = (TextView) findViewById(R.id.txtQuantity);
        txtDesc = (TextView) findViewById(R.id.txtDescConso);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        btnLess = (FloatingActionButton) findViewById(R.id.btnLess);
        btnMore = (FloatingActionButton) findViewById(R.id.btnMore);
        scrollViewConso = (ScrollView) findViewById(R.id.scrollViewConso);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);

        Bundle extras = getIntent().getExtras();
        etab = new Etab(extras.getString(getString(R.string.extraEtabId), ""), extras.getString(getString(R.string.extraEtabName), ""));
        conso = new Conso(extras.getString(getString(R.string.nameObject), ""));

        scrollViewConso.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        txtNomConso.setText(conso.getNom());

        // Création de la bdd si elle n'existe pas
        dbEF = openOrCreateDatabase(getString(R.string.varDbName), MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        Utilitaire.createBasePanier(dbEF);

        Cursor loadConsos = dbEF.rawQuery("Select qteConso From Panier Where idEt = ? and nomConso = ?", new String[]{etab.getId(), conso.getNom()});

        // Cherche la quantité de ce consommable
        if (loadConsos.moveToFirst())
            conso.setQuantite(loadConsos.getInt(loadConsos.getColumnIndex("qteConso")));
        else
            conso.setQuantite(0);

        majQte();
        txtQuantity.setText(conso.getQuantiteStr());
        loadConsos.close();

        if (pref.getString(getString(R.string.prefCompte), "").equals(getString(R.string.varGerant))) {
            btnLess.setVisibility(View.GONE);
            btnMore.setVisibility(View.GONE);
        }

        ServerSide getConso = new ServerSide(context);
        getConso.execute(getString(R.string.getConso), getString(R.string.read), etab.getId(), conso.getNom());
    }

    public void onClickLess(View v) {
        conso.addQuantite(-1);
        txtQuantity.setText(conso.getQuantiteStr());

        // Delete si quantité = 0
        if (conso.getQuantite() == 0)
            dbEF.execSQL("Delete from Panier Where idEt = ? and nomConso = ?", new String[]{etab.getId(), conso.getNom()});
        else
            updatePanier();

        majQte();
    }

    public void onClickMore(View v) {
        conso.addQuantite(1);
        txtQuantity.setText(conso.getQuantiteStr());

        if (conso.getQuantite() > 1)
            updatePanier();
        else
            dbEF.execSQL("Insert into Panier (idEt, nomEt, nomConso, qteConso, prixConso) values (?, ?, ?, ?, ?)", new String[]{etab.getId(), etab.getNom(), conso.getNom(), conso.getQuantiteStr(), conso.getPrixStr()});

        majQte();
    }

    private void updatePanier() {
        dbEF.execSQL("Update Panier Set qteConso = ?, prixConso = ? Where idEt = ? and nomConso = ?", new String[]{conso.getQuantiteStr(), conso.getPrixStr(), etab.getId(), conso.getNom()});
    }

    public void getConso(String[][] co) {
        conso.setDescription(co[0][0]);
        conso.setPrix(co[0][1]);

        txtDesc.setText(conso.getDescription());
        txtPrice.setText(conso.getPrixStr());
        scrollViewConso.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
    }

    private void majQte() {
        if (conso.getQuantite() > 0)
            btnLess.setVisibility(View.VISIBLE);
        else
            btnLess.setVisibility(View.GONE);
    }

    public static Consommable getInstance() {
        return instConso;
    }
}
