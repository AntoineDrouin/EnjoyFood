package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;


public class Coordonnees extends Fragment {

    Context context;
    static Coordonnees instCoord;
    View view;

    TextView txtDesc, txtAdr, txtTel, txtConges, txtPrixLivr;
    LinearLayout layoutInfos;
    RelativeLayout layoutLoading;
    ListView lvHoraires, lvPay;

    String idEt, nomEt, cp, ville, adresse, tel, desc, prixLivr, conges;
    boolean charged = false;
    ArrayList<String> horaires, paiements;

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
        idEt = extras.getString(getString(R.string.extraEtabId), "");
        nomEt = extras.getString(getString(R.string.extraEtabName), getString(R.string.tabEtab));

        // Requête pour trouver les données de l'établissement
        ServerSide getEtabInfo = new ServerSide(context);
        getEtabInfo.execute(getString(R.string.getEtabById), getString(R.string.read), idEt);
    }

    public void getInfos(String cCp, String cVille, String cAdresse, String cTel, String cDesc, String cPrixLivr, String cConges) {
        cp = cCp;
        ville = cVille;
        adresse = cAdresse;
        tel = cTel;
        desc = cDesc;
        prixLivr = cPrixLivr;
        conges = cConges;
        charged = true;

        // Si des infos ont étés trouvés, on cherche les horaires puis les moyens de paiements
        if (conges != null) {
            ServerSide getHor = new ServerSide(context);
            getHor.execute(getString(R.string.getHoraires), getString(R.string.read), idEt);
        }
        else {
            setCompo();
        }
    }

    public void getHor(ArrayList<String> cHor) {
        horaires = new ArrayList<>();
        horaires = cHor;
        ServerSide getPai = new ServerSide(context);
        getPai.execute(getString(R.string.getPaiements), getString(R.string.read), idEt);
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
            ((TextView) Etablissement.getInstance().findViewById(R.id.txtNomEtab)).setText(nomEt);

            if (conges != null) {
                txtDesc.setText(desc);
                txtAdr.setText(adresse);
                txtTel.setText(tel);
                txtPrixLivr.setText(prixLivr);
                if (conges.equals("0"))
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
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel)));
    }

    // Ouvre Maps
    public void openMaps() {
        startActivity(new Intent(context, MapsActivity.class));
    }

    public static Coordonnees getInstance() {
        return instCoord;
    }
}
