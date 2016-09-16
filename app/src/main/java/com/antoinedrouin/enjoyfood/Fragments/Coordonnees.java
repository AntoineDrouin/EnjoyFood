package com.antoinedrouin.enjoyfood.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.antoinedrouin.enjoyfood.Activities.Etablissement;
import com.antoinedrouin.enjoyfood.Classes.Etab;
import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;

public class Coordonnees extends Fragment {

    Context context;
    static Coordonnees instCoord;
    SQLiteDatabase dbEF;
    View view;

    TextView txtDesc, txtAdr, txtTel, txtConges, txtPrixLivr, txtEtabNotRegistered;
    LinearLayout layoutInfos;
    RelativeLayout layoutLoading;
    ListView lvHoraires, lvPay;

    boolean charged = false;
    ArrayList<String> horaires, paiements;
    Etab etab;

    public static Coordonnees newInstance() {
        Coordonnees fragment = new Coordonnees();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        instCoord = this;

        Bundle extras = Etablissement.getInstance().getIntent().getExtras();
        etab = new Etab(extras.getString(getString(R.string.extraEtabId), ""), extras.getString(getString(R.string.extraEtabName), getString(R.string.tabEtab)));

        // Création de la bdd si elle n'existe pas
        dbEF = context.openOrCreateDatabase(getString(R.string.varDbName), Context.MODE_PRIVATE, null);
        // Création de la table si elle n'existe pas
        Utilitaire.createBasePanier(dbEF);

        // Requête pour trouver les données de l'établissement
        ServerSide getEtabInfo = new ServerSide(context);
        getEtabInfo.execute(getString(R.string.getEtabById), getString(R.string.read), etab.getId());
    }

    public void getInfos(Etab et) {
        etab = et;
        charged = true;

        // Si des infos ont étés trouvés, on cherche les horaires puis les moyens de paiements
        if (etab.getId() != null) {
            ServerSide getHor = new ServerSide(context);
            getHor.execute(getString(R.string.getHoraires), getString(R.string.read), etab.getId());
        }
        else {
            setCompo();
        }
    }

    public void getHor(ArrayList<String> cHor) {
        horaires = new ArrayList<>();
        horaires = cHor;
        ServerSide getPai = new ServerSide(context);
        getPai.execute(getString(R.string.getPaiements), getString(R.string.read), etab.getId());
    }

    public void getPai(ArrayList<String> cPai) {
        paiements = new ArrayList<>();
        paiements = cPai;
        setCompo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_coordonnees, container, false);
        layoutInfos = (LinearLayout) view.findViewById(R.id.layoutInfos);
        layoutLoading = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        txtDesc = (TextView) view.findViewById(R.id.txtDesc);
        txtAdr = (TextView) view.findViewById(R.id.txtAdress);
        txtTel = (TextView) view.findViewById(R.id.txtTel);
        txtConges = (TextView) view.findViewById(R.id.txtConges);
        txtPrixLivr = (TextView) view.findViewById(R.id.txtPrixLivr);
        txtEtabNotRegistered = (TextView) view.findViewById(R.id.txtEtabNotRegistered);

        lvHoraires = (ListView) view.findViewById(R.id.lvHor);
        lvPay = (ListView) view.findViewById(R.id.lvPay);

        layoutInfos.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);

        // Recharge les composants parce que la vue est détruite quand on va au dernier onglet
        if (charged)
            setCompo();

        return view;
    }

    // Remplissage des champs
    private void setCompo() {
        try {
            ((TextView) Etablissement.getInstance().findViewById(R.id.txtNomEtab)).setText(etab.getNom());

            // Si l'établissement a été trouvé, remplissage de la fiche
            if (etab.getId() != null) {
                txtDesc.setText(etab.getDescription());
                txtAdr.setText(etab.getAdresse());
                txtTel.setText(etab.getTel());
                txtPrixLivr.setText(etab.getPrixLivr());
                if (!etab.isConges())
                    txtConges.setVisibility(View.GONE);

                ArrayAdapter<String> arrayHor = new ArrayAdapter<>(
                        context,
                        R.layout.listitem,
                        horaires);
                lvHoraires.setAdapter(arrayHor);
                lvHoraires.getLayoutParams().height = arrayHor.getCount() * 150;

                ArrayAdapter<String> arrayPay = new ArrayAdapter<>(
                        context,
                        R.layout.listitem,
                        paiements);
                lvPay.setAdapter(arrayPay);
                lvPay.getLayoutParams().height = arrayPay.getCount() * 150;

                layoutInfos.setVisibility(View.VISIBLE);

                // Ajout d'infos à l'établissement
                dbEF.execSQL("Update Etablissement Set telEt = ?, prixLivrEt = ? Where idEt = ?", new String[]{etab.getTel(), etab.getPrixLivr(), etab.getId()});
            }
            // Sinon on affiche un message
            else {
                txtEtabNotRegistered.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e) {
            Log.i("marquage", "Erreur setCompo : " + e.getMessage());
            layoutInfos.setVisibility(View.VISIBLE);
        }
        finally {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    // Compose le numéro
    public void call() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + etab.getTel())));
    }

    // Ouvre Maps
    public void openMaps() {
        // Lien qui va donner l'itinéraire de l'adresse
        Uri gmmIntentUri = Uri.parse(getString(R.string.varLinkMaps) + etab.getAdresse());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // L'intent va prendre le package de Maps
        mapIntent.setPackage(getString(R.string.varPackageMaps));
        startActivity(mapIntent);
    }

    public static Coordonnees getInstance() {
        return instCoord;
    }
}
