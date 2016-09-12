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
import android.widget.ListView;

import com.antoinedrouin.enjoyfood.Activities.MapsActivity;
import com.antoinedrouin.enjoyfood.Activities.Tabs;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.List;

public class Panier extends Fragment {

    Context context;
    static Panier instPanier;

    ArrayAdapter<String> arrayAdapter;
    SQLiteDatabase dbEF;

    ListView lvPanier;

    String nomEt;

    public static Panier newInstance() {
        Panier fragment = new Panier();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instPanier = this;
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panier, container, false);
        lvPanier = (ListView) view.findViewById(R.id.lvPanier);

        // Création de la bdd si elle n'existe pas
        dbEF = getActivity().openOrCreateDatabase(getString(R.string.varDbName), context.MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        dbEF.execSQL("CREATE TABLE IF NOT EXISTS PANIER (idUt VARCHAR, idEt VARCHAR, nomEt VARCHAR, nomConso VARCHAR, qteConso INTEGER)");

        fillLv();

        // Click sur l'établissement dans le panier
        lvPanier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvPanier.getItemAtPosition(position);
                Cursor loadEtabs = dbEF.rawQuery("Select idEt from Etablissement where nomEt = ?", new String[]{o.toString()});

                // Recherche du panier pour cet établissement
                if (loadEtabs.moveToFirst()) {
                    String idEt = loadEtabs.getString(loadEtabs.getColumnIndex("idEt"));
                }
            }
        });

        return view;
    }

    private void fillLv() {
        List<String> listPanier = new ArrayList<>();
        // Requête
        Cursor loadEtabs = dbEF.rawQuery("Select distinct nomEt From panier", null);

        if (loadEtabs.moveToFirst()) {
            do {
                nomEt = loadEtabs.getString(loadEtabs.getColumnIndex("nomEt"));
                listPanier.add(nomEt);
            } while (loadEtabs.moveToNext());
        }

        arrayAdapter = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                listPanier);

        lvPanier.setAdapter(arrayAdapter);
    }

    public void searchInLv() {
        emptyLv();
        List<String> listEtab = new ArrayList<>();

        String nom = "%" + ((EditText) Tabs.getInstance().findViewById(R.id.edtSearchPanier)).getText().toString() + "%";
        Cursor loadEtabs = dbEF.rawQuery("Select distinct nomEt from Panier Where nomEt like ?", new String[]{nom});

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

        lvPanier.setAdapter(arrayAdapter);
    }

    private void emptyLv(){
        // Supprime tous les éléments de la listView
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    public static Panier getInstance() {
        return instPanier;
    }
}
