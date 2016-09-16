package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Classes.Etab;
import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

public class EtablissementManager extends AppCompatActivity {

    SharedPreferences pref;
    Context context;
    static EtablissementManager instEtabMan;

    TextView txtEt;
    EditText edtDesc, edtPrixLivr, edtTel;
    Switch switchConges;
    RelativeLayout layoutLoading;

    String idUt, script;
    Etab etab = new Etab();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissement_manager);

        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        instEtabMan = this;

        txtEt = (TextView) findViewById(R.id.txtEtab);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        edtPrixLivr = (EditText) findViewById(R.id.edtPrixLivr);
        edtTel = (EditText) findViewById(R.id.edtTel);
        switchConges = (Switch) findViewById(R.id.switchConges);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);
        layoutLoading.setVisibility(View.VISIBLE);

        idUt = pref.getString(getString(R.string.prefId), "");

        // Recherche des infos de l'étab
        ServerSide getEtabByManager = new ServerSide(context);
        getEtabByManager.execute(getString(R.string.getEtabByManager), getString(R.string.read), idUt);
    }

    // Reçoit les infos, et si il n'y en a pas, ouvre le placePicker
    public void getInfos(Etab et) {
        try {
            if (et.getId() != null) {
                etab = et;

                txtEt.setText(etab.getNom());
                edtDesc.setText(etab.getDescription());
                edtPrixLivr.setText(etab.getPrixLivr());
                edtTel.setText(etab.getTel());
                switchConges.setChecked(etab.isConges());

                script = getString(R.string.updateEtab);

                SharedPreferences.Editor edit = pref.edit();
                edit.putString(getString(R.string.prefIdEt), etab.getId());
                edit.apply();
            } else {
                Intent intent = new Intent(this, MapPlacePicker.class);
                intent.putExtra(getString(R.string.useType), getString(R.string.useTypeModif));

                Toast.makeText(context, getString(R.string.choosePlace), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        } finally {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    // Enregistre les modifications
    public void onClickSaveChanges(View v) {
        try {
            layoutLoading.setVisibility(View.VISIBLE);

            etab.setNom(txtEt.getText().toString());
            etab.setDescription(edtDesc.getText().toString());
            if (!edtPrixLivr.getText().toString().equals(""))
                etab.setPrixLivr(edtPrixLivr.getText().toString());
            etab.setTel(edtTel.getText().toString());
            etab.setConges(switchConges.isChecked());

            if (etab.getDescription().equals("") || etab.getTel().equals(""))
                Toast.makeText(context, getString(R.string.errorFields), Toast.LENGTH_SHORT).show();
            else {
                ServerSide etabl = new ServerSide(context);

                if (script.equals(getString(R.string.updateEtab))) {
                    etabl.execute(script, getString(R.string.write), etab.getId(), idUt, etab.getDescription(), etab.getPrixLivr(), etab.getTel(), Utilitaire.returnStringFromBool(etab.isConges()));
                } else if (script.equals(getString(R.string.insertEtab))) {
                    etabl.execute(script, getString(R.string.write), etab.getId(), etab.getNom(), idUt, etab.getDescription(), etab.getPrixLivr(), etab.getTel(), Utilitaire.returnStringFromBool(etab.isConges()), etab.getCp(), etab.getVille(), etab.getAdresse());
                }
            }
        } finally {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    public void okUpdateEtab() {
        Toast.makeText(context, getString(R.string.placeUpdated), Toast.LENGTH_SHORT).show();
        finish();
    }

    public void okInsertEtab() {
        Toast.makeText(context, getString(R.string.placeInserted), Toast.LENGTH_SHORT).show();
    }

    public void getPlaceInfos(Etab et) {
        script = getString(R.string.insertEtab);
        etab = et;
        txtEt.setText(etab.getNom());
    }

    public void onClickHoraires(View v) {
        launchInfosActivity(R.string.txtHoraires);
    }

    public void onClickPaiements(View v) {
        launchInfosActivity(R.string.txtPay);
    }

    public void onClickCategories(View v) {
        launchInfosActivity(R.string.txtCateg);
    }

    public void onClickConsommables(View v) {
        launchInfosActivity(R.string.txtConso);
    }

    private void launchInfosActivity(int typeInfo) {
        Intent intent = new Intent(this, EtablissementManagerInfos.class);
        intent.putExtra(getString(R.string.typeInfos), getString(typeInfo));
        startActivity(intent);
    }

    public static EtablissementManager getInstance() {
        return instEtabMan;
    }
}
