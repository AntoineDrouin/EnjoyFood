package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.antoinedrouin.enjoyfood.Classes.Comm;
import com.antoinedrouin.enjoyfood.Classes.Conso;
import com.antoinedrouin.enjoyfood.Classes.Etab;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.List;

public class PanierDetails extends AppCompatActivity {

    Context context;
    SQLiteDatabase dbEF;
    SharedPreferences pref;

    ListView lvPanier;
    EditText edtRemarque;
    RelativeLayout layoutLoading;

    Etab etab;
    Comm commande;
    List<Conso> consos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier_details);

        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        Bundle extras = getIntent().getExtras();

        etab = new Etab(extras.getString(getString(R.string.extraEtabId), ""), extras.getString(getString(R.string.extraEtabName), ""));
        consos = new ArrayList<>();
        commande = new Comm();

        lvPanier = (ListView) findViewById(R.id.lvPanierDetails);
        edtRemarque = (EditText) findViewById(R.id.edtRemarque);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);

        // Création de la bdd si elle n'existe pas
        dbEF = openOrCreateDatabase(getString(R.string.varDbName), MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        Utilitaire.createBasePanier(dbEF);

        fillLv();
    }

    private void fillLv() {
        Conso conso;
        List<String> listPanier = new ArrayList<>();
        Cursor loadEtabs = dbEF.rawQuery("Select nomConso, qteConso, prixConso from Panier Where idEt = ?", new String[]{etab.getId()});

        if (loadEtabs.moveToFirst()) {
            do {
                conso = new Conso(loadEtabs.getString(loadEtabs.getColumnIndex("nomConso")),
                        loadEtabs.getDouble(loadEtabs.getColumnIndex("prixConso")),
                        loadEtabs.getInt(loadEtabs.getColumnIndex("qteConso")));

                // Ajout d'infos à la commande
                commande.addPrix(conso.getTotal());
                commande.addQuantite(conso.getQuantite());
                consos.add(conso);

                listPanier.add(conso.displayForPanier(context));
            } while (loadEtabs.moveToNext());

            commande.setEtat(getString(R.string.spinEtatCom1)); // Premier état de la commande

            // Ajout du prix de livraison de l'établissement
            loadEtabs.close();
            loadEtabs = dbEF.rawQuery("Select prixLivrEt from Etablissement Where idEt = ?", new String[]{etab.getId()});

            if (loadEtabs.moveToFirst()) {
                commande.setPrixLivr(loadEtabs.getDouble(loadEtabs.getColumnIndex("prixLivrEt")));
                listPanier.add(commande.getPrixLivrDisplay(context));
            }

            loadEtabs.close();
            listPanier.add(commande.getTotalDisplay(context));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                listPanier);

        lvPanier.setAdapter(arrayAdapter);
    }

    public void onClickOrder(View v) {
        try {
            if (pref.getString(getString(R.string.prefCompte), "").equals(getString(R.string.varClient))) {
                layoutLoading.setVisibility(View.VISIBLE);
                // pref.getString(getString(R.string.prefId), "")
                commande.setRemarque(edtRemarque.getText().toString());
                commande.setAdresse(pref.getString(getString(R.string.prefAdresse), ""));

                // Insertion de la commande

            }
            else {
                startActivity(new Intent(context, Login.class));
            }
        } catch (Exception e) {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    public void insertArticles(String idCom) {
        try {
            // Retour de l'id de la commande, insertion des articles

            for (Conso conso : consos) {

            }
        } finally {
            layoutLoading.setVisibility(View.GONE);
        }
    }
}
