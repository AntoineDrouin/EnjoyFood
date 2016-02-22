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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Etablissements extends Activity {

    Context context;
    static Etablissements instEtabs;

    ArrayAdapter<String> arrayAdapter;
    SQLiteDatabase dbEF;

    ListView lvEtab;

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

        fillLvWithDb();

        lvEtab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvEtab.getItemAtPosition(position);

                if (o.toString().equals(getString(R.string.lvIndicSearch)))
                    openPlacePicker();
                else
                    openEtab(o.toString());
            }
        });
    }


    public void fillLvWithDb() {
        List<String> listEtab = new ArrayList<>();

        Cursor loadEtabs = dbEF.rawQuery("Select nomEt from Etablissement", null);
        loadEtabs.moveToFirst();

        if (loadEtabs.moveToFirst()) {
            String nom;
            do {
                nom = loadEtabs.getString(loadEtabs.getColumnIndex("nomEt"));
                listEtab.add(nom);
            } while (loadEtabs.moveToNext());
        }

        listEtab.add(getString(R.string.lvIndicSearch));

        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listEtab);

        lvEtab.setAdapter(arrayAdapter);
    }

    public void openDrawerEtab(View v) {
        Tabs.getInstance().openDrawer();
    }

    public void emptyLvEtab(View v) {
        // Supprime tous les éléments de la listview sauf celui qui permet d'ouvrir le PlacePicker
        arrayAdapter.clear();
        arrayAdapter.add(getString(R.string.lvIndicSearch));
        arrayAdapter.notifyDataSetChanged();

        dbEF.execSQL("Delete from Etablissement");
    }

    private void openPlacePicker() {
        startActivity(new Intent(this, MapPlacePicker.class));
    }

    private void openEtab(String nomEtab) {
        Intent intentEtab = new Intent(this, Etablissement.class);
        intentEtab.putExtra(getString(R.string.extraEtabName), nomEtab);
        startActivity(intentEtab);
    }

    public static Etablissements getInstance() {
        return instEtabs;
    }

}
