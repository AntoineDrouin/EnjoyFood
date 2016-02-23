package com.antoinedrouin.enjoyfood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Etablissements extends Activity {

    Context context;
    static Etablissements instEtabs;

    ArrayAdapter<String> arrayAdapter;
    SQLiteDatabase dbEF;

    ListView lvEtab;
    Spinner spinEtab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissements);

        context = getApplicationContext();
        instEtabs = this;

        // Création de la bdd si elle n'existe pas
        dbEF = openOrCreateDatabase(getString(R.string.varDbName), MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        dbEF.execSQL("CREATE TABLE IF NOT EXISTS Etablissement (nomEt VARCHAR, adresseEt VARCHAR, villeEt VARCHAR)");

        lvEtab = (ListView) findViewById(R.id.lvEtab);
        spinEtab = (Spinner) findViewById(R.id.spinEtab);
        final ImageButton btnEmptyLvEtab = (ImageButton) findViewById(R.id.btnEmptyLvEtab);

        fillLvWithDb();

        lvEtab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvEtab.getItemAtPosition(position);
                openEtab(o.toString());
            }
        });

        spinEtab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String result = spinEtab.getSelectedItem().toString();

                // Charge les établissements récents
                if (result.equals(getString(R.string.spinVarLvRecent))) {
                    btnEmptyLvEtab.setVisibility(View.VISIBLE);
                    searchInLv();
                }

                // Prépare l'interface à la recherche
                else if (result.equals(getString(R.string.spinVarLvSearch))) {
                    btnEmptyLvEtab.setVisibility(View.GONE);
                    emptyLvEtab();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    public void fillLvWithDb() {
        List<String> listEtab = new ArrayList<>();

        Cursor loadEtabs = dbEF.rawQuery("Select nomEt from Etablissement", null);

        if (loadEtabs.moveToFirst()) {
            String nom;
            do {
                nom = loadEtabs.getString(loadEtabs.getColumnIndex("nomEt"));
                listEtab.add(nom);
            } while (loadEtabs.moveToNext());
        }

        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listEtab);

        lvEtab.setAdapter(arrayAdapter);
    }

    public void openDrawerEtab(View v) {
        Tabs.getInstance().openDrawer();
    }

    public void searchInLv() {
        String nom, ville, nomEt, query;
        List<String> listEtab = new ArrayList<>();
        nom = ((EditText) Tabs.getInstance().findViewById(R.id.edtSearchEtab)).getText().toString();
        ville = ((EditText) Tabs.getInstance().findViewById(R.id.edtSearchVille)).getText().toString();

        emptyLvEtab();

        // Pour le "like"
        if (!nom.equals(""))
            nom = "%" + nom + "%";
        if (!ville.equals(""))
            ville = "%" + ville + "%";

        // Requête par défaut
        query = "Select nomEt from Etablissement ";
        Cursor loadEtabs = dbEF.rawQuery(query, null);

        // Requêtes avec les paramètres
        if (!nom.equals("") && !ville.equals(""))
            loadEtabs = dbEF.rawQuery(query + "Where nomEt like ? and villeEt like ?", new String[]{nom, ville});
        else if (!nom.equals(""))
            loadEtabs = dbEF.rawQuery(query + "Where nomEt like ?", new String[]{nom});
        else if (!ville.equals(""))
            loadEtabs = dbEF.rawQuery(query + "Where villeEt like ?", new String[]{ville});

        if (loadEtabs.moveToFirst()) {
            do {
                nomEt = loadEtabs.getString(loadEtabs.getColumnIndex("nomEt"));
                listEtab.add(nomEt);
            } while (loadEtabs.moveToNext());
        }

        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listEtab);

        lvEtab.setAdapter(arrayAdapter);
    }

    public void emptyLvEtab(View v) {
        emptyLvEtab();

        // Également les établissements dans la base
        dbEF.execSQL("Delete from Etablissement");
    }

    private void emptyLvEtab(){
        // Supprime tous les éléments de la listview
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    public void openPlacePicker(View v) {
        startActivity(new Intent(this, MapPlacePicker.class));
    }

    private void openEtab(String nomEtab) {
        // Ouvre la fiche d'un établissement en passant en paramètre son nom
        Intent intentEtab = new Intent(this, Etablissement.class);
        intentEtab.putExtra(getString(R.string.extraEtabName), nomEtab);
        startActivity(intentEtab);
    }

    public static Etablissements getInstance() {
        return instEtabs;
    }

}
