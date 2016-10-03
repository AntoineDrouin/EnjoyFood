package com.antoinedrouin.enjoyfood.Fragments;

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

import com.antoinedrouin.enjoyfood.Activities.Etablissement;
import com.antoinedrouin.enjoyfood.Activities.Tabs;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.List;

public class Etablissements extends Fragment {

    Context context;
    static Etablissements instEtabs;

    ArrayAdapter<String> arrayAdapter;
    SQLiteDatabase dbEF;

    ListView lvEtab;
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
        dbEF = getActivity().openOrCreateDatabase(getString(R.string.varDbName), Context.MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        Utilitaire.createBaseEtab(dbEF);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_etablissements, container, false);

        lvEtab = (ListView) view.findViewById(R.id.lvEtab);
        btnEmptyLvEtab = (ImageButton) view.findViewById(R.id.btnEmptyLvEtab);

        fillLvWithDb();

        lvEtab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvEtab.getItemAtPosition(position);
                Cursor loadEtabs = dbEF.rawQuery("Select idEt from Etablissement where nomEt = ?", new String[]{o.toString()});

                if (loadEtabs.moveToFirst()) {
                    openEtab(loadEtabs.getString(loadEtabs.getColumnIndex("idEt")), o.toString());
                }

                loadEtabs.close();
            }
        });

        return view;
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

        loadEtabs.close();

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

        loadEtabs.close();

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
        // Supprime tous les éléments de la listView
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
