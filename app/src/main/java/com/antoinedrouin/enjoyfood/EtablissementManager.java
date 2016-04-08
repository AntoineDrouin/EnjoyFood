package com.antoinedrouin.enjoyfood;

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

public class EtablissementManager extends AppCompatActivity {

    SharedPreferences pref;
    Context context;
    static EtablissementManager instEtabMan;

    TextView txtEt;
    EditText edtDesc, edtPrixLivr;
    Switch switchConges;

    String id/*, nomEt, cp, ville, adresse, tel, desc, prixLivr*/;
   // boolean conges;

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
        switchConges = (Switch) findViewById(R.id.switchConges);

        id = pref.getString(getString(R.string.prefId), "");

        /** Recherche des infos de l'étab **/

        ServerSide getEtabByManager = new ServerSide(context);
        getEtabByManager.execute(getString(R.string.getEtabByManager), getString(R.string.read), id);

        /** Fin recherche des infos de l'étab **/
    }

    // Reçoit les infos, et si il n'y en a pas, ouvre le placePicker
    public void getInfos(String eNomEt, String eDesc, String ePrixLivr, String eConges) {
        if (eNomEt != null) {
            txtEt.setText(eNomEt);
            edtDesc.setText(eDesc);
            edtPrixLivr.setText(ePrixLivr);
            switchConges.setChecked(eConges.equals("1"));
        }
        else {
            Intent intent = new Intent(this, MapPlacePicker.class);
            intent.putExtra(getString(R.string.useType), getString(R.string.useTypeModif));

            Toast.makeText(context, getString(R.string.choosePlace), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    // Enregistre les modifications
    public void onClickSaveChanges(View v) {


        finish();
    }

    public static EtablissementManager getInstance() {
        return instEtabMan;
    }
}
