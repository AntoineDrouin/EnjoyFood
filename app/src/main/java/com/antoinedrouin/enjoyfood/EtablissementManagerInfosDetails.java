package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EtablissementManagerInfosDetails extends AppCompatActivity {

    Context context;
    SharedPreferences pref;
    static EtablissementManagerInfosDetails instEtabManInfDet;

    String idObject, nameObject, typeInfo, idEt;

    LinearLayout layoutHo, layoutCo;
    EditText edtObjectName, edtHd1, edtHf1, edtHd2, edtHf2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissement_manager_infos_details);

        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        instEtabManInfDet = this;

        layoutCo = (LinearLayout) findViewById(R.id.layoutEditConso);
        layoutHo = (LinearLayout) findViewById(R.id.layoutEditHoraires);
        edtObjectName = (EditText) findViewById(R.id.edtObjectName);
        edtHd1 = (EditText) findViewById(R.id.edtHd1);
        edtHf1 = (EditText) findViewById(R.id.edtHf1);
        edtHd2 = (EditText) findViewById(R.id.edtHd2);
        edtHf2 = (EditText) findViewById(R.id.edtHf2);

        Bundle extras = getIntent().getExtras();
        idObject = extras.getString(getString(R.string.idObject), "");
        nameObject = extras.getString(getString(R.string.nameObject), "");
        typeInfo = extras.getString(getString(R.string.typeInfos), "");

        idEt = pref.getString(getString(R.string.prefIdEt), "");

        edtObjectName.setText(nameObject);

        // Charge les données de l'élément sélectionné
        if (typeInfo.equals(getString(R.string.txtHoraires))) {
            edtHd1.setText(extras.getString(getString(R.string.prefHeureDebut1), ""));
            edtHf1.setText(extras.getString(getString(R.string.prefHeureFin1), ""));
            edtHd2.setText(extras.getString(getString(R.string.prefHeureDebut2), ""));
            edtHf2.setText(extras.getString(getString(R.string.prefHeureFin2), ""));
            layoutHo.setVisibility(View.VISIBLE);
        }
        else if (typeInfo.equals(getString(R.string.txtConso))) {
            layoutCo.setVisibility(View.VISIBLE);
        }
    }

    public void onClickOkObject(View v) {
        String method, label, hd1, hf1, hd2, hf2;

        label = edtObjectName.getText().toString();
        hd1 = edtHd1.getText().toString();
        hf1 = edtHf1.getText().toString();
        hd2 = edtHd2.getText().toString();
        hf2 = edtHf2.getText().toString();

        // Test les champs
        /*if ((label.equals("") || hd1.equals("") || hf1.equals("") || hd2.equals("") || hf2.equals(""))) {
            Toast.makeText(context, getString(R.string.errorFields), Toast.LENGTH_SHORT).show();

        }
        else {*/
            method = getString(R.string.write);
            ServerSide modifyObject = new ServerSide(context);
        Log.i("marquage", "typeinfo : " + typeInfo + " nameObject : " + nameObject);

            // Si au départ le nom de l'objet était null, il est inséré
            if (nameObject.equals("")) {
                if (typeInfo.equals(getString(R.string.txtHoraires)))
                    modifyObject.execute(getString(R.string.insertHoraire), method, idEt, label, hd1, hf1, hd2, hf2);
                else if (typeInfo.equals(getString(R.string.txtPay)))
                    modifyObject.execute(getString(R.string.insertPaiement), method, idEt, label);
                /*else if (typeInfo.equals(getString(R.string.txtConso)))*/

                else if (typeInfo.equals(getString(R.string.txtCateg)))
                    modifyObject.execute(getString(R.string.insertCateg), method, idEt, label);
            }
            // Sinon cela veut dire qu'il existe déjà, donc on l'update
            else {
                if (typeInfo.equals(getString(R.string.txtHoraires)))
                    modifyObject.execute(getString(R.string.updateHoraire), method, idObject, label, hd1, hf1, hd2, hf2);
                /*else if (typeInfo.equals(getString(R.string.txtPay)))

                else if (typeInfo.equals(getStringR.string.txtConso)))
                */
                else if (typeInfo.equals(getString(R.string.txtCateg)))
                    modifyObject.execute(getString(R.string.updateCateg), method, idObject, label);
            }
        //}
    }

    public void okUpdateHoraire() {
        EtablissementManagerInfos.getInstance().recreate();
        Toast.makeText(context, getString(R.string.update), Toast.LENGTH_SHORT).show();
        finish();
    }

    public static EtablissementManagerInfosDetails getInstance() {
        return instEtabManInfDet;
    }
}
