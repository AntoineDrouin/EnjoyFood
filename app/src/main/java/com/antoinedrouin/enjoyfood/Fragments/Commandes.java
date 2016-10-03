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

import com.antoinedrouin.enjoyfood.Activities.CommandeDetails;
import com.antoinedrouin.enjoyfood.Activities.Login;
import com.antoinedrouin.enjoyfood.Activities.Tabs;
import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.List;

public class Commandes extends Fragment {

    Context context;
    static Commandes instCommandes;
    SharedPreferences pref;

    ArrayAdapter<String> arrayAdapter;
    SQLiteDatabase dbEF;

    ListView lvCommande;
    SwipeRefreshLayout swipeContainer;

    String idCom;
    List<String> listIdCom;

    public static Commandes newInstance() {
        Commandes fragment = new Commandes();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instCommandes = this;
        context = getContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commandes, container, false);
        lvCommande = (ListView) view.findViewById(R.id.lvCommande);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Création de la bdd si elle n'existe pas
        dbEF = getActivity().openOrCreateDatabase(getString(R.string.varDbName), Context.MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        Utilitaire.createBaseCommande(dbEF);

        fillLv();

        // Click sur l'établissement dans le panier
        lvCommande.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                idCom = listIdCom.get(position);
                Intent intent = new Intent(context, CommandeDetails.class);
                intent.putExtra(getString(R.string.extraIdCom), idCom);
                startActivity(intent);
            }
        });

        // Rafraichissement de la listView
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String idUt, compte;
                idUt = pref.getString(getString(R.string.prefId), "");
                compte = pref.getString(getString(R.string.prefCompte), "");

                dbEF.execSQL("Delete From Commande");

                if (!idUt.equals("") && !compte.equals("")) {
                    ServerSide getCommandes = new ServerSide(context);
                    getCommandes.execute(getString(R.string.getCommandes), getString(R.string.read), idUt, compte);
                }
                else {
                    fillLv();
                    startActivity(new Intent(context, Login.class));
                }

                swipeContainer.setRefreshing(false);
            }
        });

        return view;
    }

    public void getCom(String[][] com) {
        for (String[] aCom : com) {
            dbEF.execSQL("Insert into Commande (idCom, idEt, etatCom, prixTotalCom) values (?, ?, ?, ?)", new String[]{aCom[0], aCom[1], aCom[2], aCom[3]});
        }

        fillLv();
    }

    public void fillLv() {
        String etat, prix;

        listIdCom = new ArrayList<>();
        List<String> listToDisplay = new ArrayList<>();

        Cursor loadCom = dbEF.rawQuery("Select idCom, etatCom, prixTotalCom From Commande", null);

        if (loadCom.moveToFirst() && !pref.getString(getString(R.string.prefId), "").equals("")) {
            do {
                idCom = loadCom.getString(loadCom.getColumnIndex("idCom"));
                etat = loadCom.getString(loadCom.getColumnIndex("etatCom"));
                prix = loadCom.getString(loadCom.getColumnIndex("prixTotalCom"));

                listIdCom.add(idCom);
                listToDisplay.add(etat + " - " + prix + getString(R.string.txtCurrency));

            } while (loadCom.moveToNext());
        }

        loadCom.close();

        arrayAdapter = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                listToDisplay);

        lvCommande.setAdapter(arrayAdapter);
    }

    public void searchInLv() {
        emptyLv();
        List<String> listCom = new ArrayList<>();

        String nom = "%" + ((EditText) Tabs.getInstance().findViewById(R.id.edtSearchCommande)).getText().toString() + "%";
        Cursor loadEtabs = dbEF.rawQuery("Select etatCom, prixTotalCom from Commande Where etatCom like ?", new String[]{nom});

        if (loadEtabs.moveToFirst()) {
            do {
                listCom.add(loadEtabs.getString(loadEtabs.getColumnIndex("etatCom")) + " - " + loadEtabs.getColumnIndex("prixTotalCom") + getString(R.string.txtCurrency));
            } while (loadEtabs.moveToNext());
        }

        loadEtabs.close();

        arrayAdapter = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                listCom);

        lvCommande.setAdapter(arrayAdapter);
    }

    private void emptyLv(){
        // Supprime tous les éléments de la listView
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    public static Commandes getInstance() {
        return instCommandes;
    }
}
