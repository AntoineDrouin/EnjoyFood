package com.antoinedrouin.enjoyfood.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.antoinedrouin.enjoyfood.Activities.PanierDetails;
import com.antoinedrouin.enjoyfood.Activities.Tabs;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.List;

public class Panier extends Fragment {

    private Context context;
    private static Panier instPanier;
    private SharedPreferences pref;

    private ArrayAdapter<String> arrayAdapter;
    private SQLiteDatabase dbEF;

    private ListView lvPanier;
    private SwipeRefreshLayout swipeContainer;

    private String nomEt;

    public static Panier newInstance() {
        return new Panier();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instPanier = this;
        context = getContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panier, container, false);
        lvPanier = (ListView) view.findViewById(R.id.lvPanier);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Création de la bdd si elle n'existe pas
        dbEF = getActivity().openOrCreateDatabase(getString(R.string.varDbName), Context.MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        Utilitaire.createBasePanier(dbEF);

        fillLv();

        // Click sur l'établissement dans le panier
        lvPanier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                nomEt = lvPanier.getItemAtPosition(position).toString();
                Cursor loadEtabs = dbEF.rawQuery("Select idEt from Etablissement where nomEt = ?", new String[]{nomEt});

                // Recherche du panier pour cet établissement
                if (loadEtabs.moveToFirst()) {
                    String idEt = loadEtabs.getString(loadEtabs.getColumnIndex("idEt"));

                    Intent intent = new Intent(context, PanierDetails.class);
                    intent.putExtra(getString(R.string.extraEtabId), idEt);
                    intent.putExtra(getString(R.string.extraEtabName), nomEt);
                    startActivity(intent);
                }

                loadEtabs.close();
            }
        });

        // Rafraichissement de la listView
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillLv();
                swipeContainer.setRefreshing(false);
            }
        });

        return view;
    }

    public void fillLv() {
        if (!pref.getString(getString(R.string.prefCompte), "").equals(getString(R.string.varGerant))) {
            List<String> listPanier = new ArrayList<>();
            Cursor loadEtabs = dbEF.rawQuery("Select distinct nomEt From panier", null);

            if (loadEtabs.moveToFirst()) {
                do {
                    nomEt = loadEtabs.getString(loadEtabs.getColumnIndex("nomEt"));
                    listPanier.add(nomEt);
                } while (loadEtabs.moveToNext());
            }

            loadEtabs.close();

            arrayAdapter = new ArrayAdapter<>(
                    context,
                    R.layout.listitem,
                    listPanier);

            lvPanier.setAdapter(arrayAdapter);
        }
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

        loadEtabs.close();

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
