package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.R;

public class EtablissementManager extends AppCompatActivity {

    SharedPreferences pref;
    Context context;
    static EtablissementManager instEtabMan;

    TextView txtEt;
    EditText edtDesc, edtPrixLivr, edtTel;
    Switch switchConges;

    String idUt, idEt, script, nomEt, desc, prixLivr, tel, conges, cp, ville, adresse;

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

        idUt = pref.getString(getString(R.string.prefId), "");

        // Recherche des infos de l'étab
        ServerSide getEtabByManager = new ServerSide(context);
        getEtabByManager.execute(getString(R.string.getEtabByManager), getString(R.string.read), idUt);
    }

    // Reçoit les infos, et si il n'y en a pas, ouvre le placePicker
    public void getInfos(String eIdEt, String eNomEt, String eDesc, String ePrixLivr, String eTel, String eConges) {
        if (eIdEt != null) {
            idEt = eIdEt;
            txtEt.setText(eNomEt);
            edtDesc.setText(eDesc);
            edtPrixLivr.setText(ePrixLivr);
            edtTel.setText(eTel);
            switchConges.setChecked(eConges.equals("1"));

            script = getString(R.string.updateEtab);

            SharedPreferences.Editor edit = pref.edit();
            edit.putString(getString(R.string.prefIdEt), idEt);
            edit.apply();
        }
        else {
            Intent intent = new Intent(this, MapPlacePicker.class);
            intent.putExtra(getString(R.string.useType), getString(R.string.useTypeModif));

            Toast.makeText(context, getString(R.string.choosePlace), Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }

    // Enregistre les modifications
    public void onClickSaveChanges(View v) {
        nomEt = txtEt.getText().toString();
        desc = edtDesc.getText().toString();
        prixLivr = edtPrixLivr.getText().toString();
        tel = edtTel.getText().toString();
        if (switchConges.isChecked())
            conges = "1";
        else
            conges = "0";

        if (desc.equals("") || prixLivr.equals("") || tel.equals(""))
            Toast.makeText(context, getString(R.string.errorFields), Toast.LENGTH_SHORT).show();
        else {
            ServerSide etab = new ServerSide(context);

            if (script.equals(getString(R.string.updateEtab))) {
                etab.execute(script, getString(R.string.write), idEt, idUt, desc, prixLivr, tel, conges);
            } else if (script.equals(getString(R.string.insertEtab))) {
                etab.execute(script, getString(R.string.write), idEt, nomEt, idUt, desc, prixLivr, tel, conges, cp, ville, adresse);
            }
        }
    }

    public void okUpdateEtab() {
        Toast.makeText(context, getString(R.string.placeUpdated), Toast.LENGTH_SHORT).show();
        finish();
    }

    public void okInsertEtab() {
        Toast.makeText(context, getString(R.string.placeInserted), Toast.LENGTH_SHORT).show();
    }

    public void getPlaceInfos(String pId, String pNom, String pCp, String pVille, String cAdresse) {
        script = getString(R.string.insertEtab);
        idEt = pId;
        txtEt.setText(pNom);
        cp = pCp;
        ville = pVille;
        adresse = cAdresse;
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