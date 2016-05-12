package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Etablissements extends Fragment {

    Context context;
    static Etablissements instEtabs;

    ArrayAdapter<String> arrayAdapter;
    SQLiteDatabase dbEF;

    ListView lvEtab;
    Spinner spinEtab;
    ImageButton btnEmptyLvEtab;

    public static Etablissements newInstance() {
        Etablissements fragment = new Etablissements();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instEtabs = this;
        context = getContext();

        // Création de la bdd si elle n'existe pas
        dbEF = getActivity().openOrCreateDatabase(getString(R.string.varDbName), context.MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        dbEF.execSQL("CREATE TABLE IF NOT EXISTS Etablissement (idEt VARCHAR, nomEt VARCHAR, adresseEt VARCHAR, villeEt VARCHAR, codePostalEt VARCHAR)");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_etablissements, container, false);

        lvEtab = (ListView) view.findViewById(R.id.lvEtab);
        spinEtab = (Spinner) view.findViewById(R.id.spinEtab);
        btnEmptyLvEtab = (ImageButton) view.findViewById(R.id.btnEmptyLvEtab);

        fillLvWithDb();
        changeItem();

        lvEtab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvEtab.getItemAtPosition(position);
                Cursor loadEtabs = dbEF.rawQuery("Select idEt from Etablissement where nomEt = ?", new String[]{o.toString()});

                if (loadEtabs.moveToFirst()) {
                    openEtab(loadEtabs.getString(loadEtabs.getColumnIndex("idEt")), o.toString());
                }
            }
        });

        spinEtab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                changeItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        return view;
    }

    private void changeItem() {
        String result = spinEtab.getSelectedItem().toString();

        // Charge les établissements récents
        if (result.equals(getString(R.string.spinVarLvRecent))) {
            btnEmptyLvEtab.setVisibility(View.VISIBLE);
            searchInLv();
        }

        // Prépare l'interface à la recherche
        else if (result.equals(getString(R.string.spinVarLvSearch))) {
            btnEmptyLvEtab.setVisibility(View.GONE);
            emptyLv();
        }
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
                context,
                R.layout.listitem,
                listEtab);

        lvEtab.setAdapter(arrayAdapter);
    }

    public void searchInLv() {
        String nom, ville, nomEt, query;
        List<String> listEtab = new ArrayList<>();
        nom = ((EditText) Tabs.getInstance().findViewById(R.id.edtSearchEtab)).getText().toString();
        ville = ((EditText) Tabs.getInstance().findViewById(R.id.edtSearchVille)).getText().toString();

        emptyLv();

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
                context,
                R.layout.listitem,
                listEtab);

        lvEtab.setAdapter(arrayAdapter);
    }

    public void emptyLvEtab() {
        emptyLv();

        // Également les établissements dans la base
        dbEF.execSQL("Delete from Etablissement");
    }

    private void emptyLv(){
        // Supprime tous les éléments de la listview
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    private void openEtab(String idEtab, String nomEtab) {
        // Ouvre la fiche d'un établissement en passant en paramètre son id
        Intent intentEtab = new Intent(context, Etablissement.class);
        intentEtab.putExtra(getString(R.string.extraEtabId), idEtab);
        intentEtab.putExtra(getString(R.string.extraEtabName), nomEtab);
        startActivity(intentEtab);
    }

    public static Etablissements getInstance() {
        return instEtabs;
    }
}
