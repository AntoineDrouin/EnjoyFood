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
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Classes.Comm;
import com.antoinedrouin.enjoyfood.Classes.Conso;
import com.antoinedrouin.enjoyfood.Classes.Etab;
import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.Fragments.Panier;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.List;

public class PanierDetails extends AppCompatActivity {

    Context context;
    static PanierDetails instPanierDetails;
    SQLiteDatabase dbEF;
    SharedPreferences pref;

    ListView lvPanier;
    EditText edtRemarque;
    RelativeLayout layoutLoading;

    Etab etab;
    Comm commande;
    List<Conso> consos;
    String script, methode, idUt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier_details);

        instPanierDetails = this;
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
            String adresse, tel;
            adresse = Utilitaire.returnFullAdressOrNull(context, pref);
            tel = pref.getString(getString(R.string.prefTel), "");
            idUt = pref.getString(getString(R.string.prefId), "");

            // Si ce n'est pas un client
            if (!pref.getString(getString(R.string.prefCompte), "").equals(getString(R.string.varClient))) {
                startActivity(new Intent(context, Login.class));
            }
            // Si des informations sont vidés
            else if (adresse.equals("") || tel.equals("")) {
                Toast.makeText(context, getString(R.string.infosMissing),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, Compte.class));
            }
            // Si tout est ok, lancement de l'insertion de la commande
            else {
                layoutLoading.setVisibility(View.VISIBLE);
                commande.setRemarque(edtRemarque.getText().toString());
                commande.setAdresse(adresse);
                commande.setTel(tel);

                script = getString(R.string.insertCommandeGetId);
                methode = getString(R.string.read);

                // Insertion de la commande
                ServerSide insertCommandeGetId = new ServerSide(context);
                insertCommandeGetId.execute(script, methode, etab.getId(), idUt, commande.getEtat(), commande.getRemarque(), commande.getAdresse(),
                        commande.getTel(), commande.getPrixStr(), commande.getPrixLivrStr(), commande.getTotal(), commande.getQuantiteStr());
            }
        } catch (Exception e) {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    public void insertArticles(String idCom) {
        try {
            // Création de la bdd si elle n'existe pas
            dbEF = openOrCreateDatabase(getString(R.string.varDbName), MODE_PRIVATE, null);
            // Création de la table si elle n'existe pas
            Utilitaire.createBasePanier(dbEF);

            // Commande dans la base
            dbEF.execSQL("Insert into Commande (idCom, idEt, etatCom, prixTotalCom) values (?, ?, ?, ?)", new String[]{idCom, etab.getId(), commande.getEtat(), commande.getPrixStr()});

            // Retour de l'id de la commande, insertion des articles
            script = getString(R.string.insertArticle);
            methode = getString(R.string.write);

            for (Conso conso : consos) {
                ServerSide insertArticle = new ServerSide(context);
                insertArticle.execute(script, methode, idCom, etab.getId(), idUt, conso.getNom(), conso.getPrixStr(), conso.getQuantiteStr());
            }
        } finally {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    public void orderSend() {
        // Efface le panier de l'établissement
        dbEF.execSQL("Delete from Panier Where idEt = ?", new String[]{etab.getId()});
        Panier.getInstance().fillLv();
        Tabs.getInstance().viewPager.setCurrentItem(2);
        Toast.makeText(context, getString(R.string.orderSend), Toast.LENGTH_SHORT).show();
        finish();
    }

    public static PanierDetails getInstance() {
        return instPanierDetails;
    }
}
