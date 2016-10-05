package com.antoinedrouin.enjoyfood.Activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.Calendar;

public class EtablissementManagerInfosDetails extends AppCompatActivity {

    /** Modèle utilisé pour gérer un objet en détail des modules : catégories, consommables, horaires, moyens de paiements */

    private Context context;
    private SharedPreferences pref;
    private static EtablissementManagerInfosDetails instEtabManInfDet;
    private Bundle extras;

    private String idObject, nameObject, typeInfo, idEt;
    private String[][] categs;
    private int numHor;

    private LinearLayout layoutHo, layoutCo;
    private EditText edtObjectName, edtHd1, edtHf1, edtHd2, edtHf2, edtDesc, edtPrix;
    private Spinner spinCateg;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissement_manager_infos_details);

        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        instEtabManInfDet = this;

        extras = getIntent().getExtras();
        idObject = extras.getString(getString(R.string.idObject), "");
        nameObject = extras.getString(getString(R.string.nameObject), "");
        typeInfo = extras.getString(getString(R.string.typeInfos), "");

        idEt = pref.getString(getString(R.string.prefIdEt), "");

        layoutCo = (LinearLayout) findViewById(R.id.layoutEditConso);
        layoutHo = (LinearLayout) findViewById(R.id.layoutEditHoraires);
        edtObjectName = (EditText) findViewById(R.id.edtObjectName);
        edtHd1 = (EditText) findViewById(R.id.edtHd1);
        edtHf1 = (EditText) findViewById(R.id.edtHf1);
        edtHd2 = (EditText) findViewById(R.id.edtHd2);
        edtHf2 = (EditText) findViewById(R.id.edtHf2);
        edtDesc = (EditText) findViewById(R.id.edtDescConso);
        edtPrix = (EditText) findViewById(R.id.edtPrixConso);
        spinCateg = (Spinner) findViewById(R.id.spinCateg);
        btnDelete = (Button) findViewById(R.id.btnDeleteObject);

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
            ServerSide fillLvCateg = new ServerSide(context);
            fillLvCateg.execute(getString(R.string.getCategs), getString(R.string.read), idEt);
        }

        if (nameObject.equals(""))
            btnDelete.setVisibility(View.GONE);
    }

    public void onClickHd1(View v) {
        numHor = 0;
        openTimePicker(context.getString(R.string.txtStart));
    }

    public void onClickHf1(View v) {
        numHor = 1;
        openTimePicker(context.getString(R.string.txtEnd));
    }

    public void onClickHd2(View v) {
        numHor = 2;
        openTimePicker(context.getString(R.string.txtStart));
    }

    public void onClickHf2(View v) {
        numHor = 3;
        openTimePicker(context.getString(R.string.txtEnd));
    }

    // Ouvre un TimePicker pour saisir les horaires plus facilement
    private void openTimePicker(String title) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                switch (numHor) {
                    case 0 : edtHd1.setText( selectedHour + ":" + selectedMinute); break;
                    case 1 : edtHf1.setText( selectedHour + ":" + selectedMinute); break;
                    case 2 : edtHd2.setText( selectedHour + ":" + selectedMinute); break;
                    case 3 : edtHf2.setText( selectedHour + ":" + selectedMinute); break;
                }
            }
        }, hour, minute, true); // Format 24h
        mTimePicker.setTitle(title);
        mTimePicker.show();
    }

    // Click sur le bouton de validation
    public void onClickOkObject(View v) {
        String method, label, error = "",
                hd1 = "", hf1 = "", hd2 = "", hf2 = "",
                desc = "", prix = "", categ = "", indexCateg = "";

        label = edtObjectName.getText().toString();

        // Check les champs à vérifier en fonction de 'lobjet que l'on modifie
        if (typeInfo.equals(getString(R.string.txtHoraires))) {
            hd1 = edtHd1.getText().toString();
            hf1 = edtHf1.getText().toString();
            hd2 = edtHd2.getText().toString();
            hf2 = edtHf2.getText().toString();
        }
        else if (typeInfo.equals(getString(R.string.txtConso))) {
            desc = edtDesc.getText().toString();
            prix = edtPrix.getText().toString();
            categ = spinCateg.getSelectedItem().toString();
        }

        // Test les champs
        if (label.equals(""))
            error = getString(R.string.errorFields);
        else if (typeInfo.equals(getString(R.string.txtHoraires))) {
            if (hd1.equals("") || hf1.equals("") || hd2.equals("") || hf2.equals(""))
                error = getString(R.string.errorFields);
        }
        else if (typeInfo.equals(getString(R.string.txtConso))) {
            if (prix.equals(""))
                error = getString(R.string.errorFields);
            else {
                for (String[] categ1 : categs) {
                    if (categ1[1].equals(categ))
                        indexCateg = categ1[0];
                }
            }
        }

        if (!error.equals("")) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        }
        // Insert ou update l'objet
        else {
            method = getString(R.string.write);
            ServerSide modifyObject = new ServerSide(context);
            Log.i("marquage", "typeinfo : " + typeInfo + " nameObject : " + nameObject);

            // Si au départ le nom de l'objet était null, il est inséré
            if (nameObject.equals("")) {
                if (typeInfo.equals(getString(R.string.txtHoraires)))
                    modifyObject.execute(getString(R.string.insertHoraire), method, idEt, label, hd1, hf1, hd2, hf2);
                else if (typeInfo.equals(getString(R.string.txtPay)))
                    modifyObject.execute(getString(R.string.insertPaiement), method, idEt, label);
                else if (typeInfo.equals(getString(R.string.txtConso)))
                    modifyObject.execute(getString(R.string.insertConso), method, idEt, label, desc, prix, indexCateg);
                else if (typeInfo.equals(getString(R.string.txtCateg)))
                    modifyObject.execute(getString(R.string.insertCateg), method, idEt, label);
            }
            // Sinon cela veut dire qu'il existe déjà, donc on l'update
            else {
                if (typeInfo.equals(getString(R.string.txtHoraires)))
                    modifyObject.execute(getString(R.string.updateHoraire), method, idObject, label, hd1, hf1, hd2, hf2);
                else if (typeInfo.equals(getString(R.string.txtPay)))
                    modifyObject.execute(getString(R.string.updatePaiement), method, idObject, label);
                else if (typeInfo.equals(getString(R.string.txtConso)))
                    modifyObject.execute(getString(R.string.updateConso), method, idObject, label, desc, prix, indexCateg);
                else if (typeInfo.equals(getString(R.string.txtCateg)))
                    modifyObject.execute(getString(R.string.updateCateg), method, idObject, label);
            }
        }
    }

    public void onClickDeleteObject(View v) {
        String method = getString(R.string.write);
        ServerSide deleteObject = new ServerSide(context);

        if (typeInfo.equals(getString(R.string.txtHoraires)))
            deleteObject.execute(getString(R.string.deleteHoraire), method, idObject);
        else if (typeInfo.equals(getString(R.string.txtPay)))
            deleteObject.execute(getString(R.string.deletePaiement), method, idObject);
        else if (typeInfo.equals(getString(R.string.txtConso)))
            deleteObject.execute(getString(R.string.deleteConso), method, idObject);
        else if (typeInfo.equals(getString(R.string.txtCateg)))
            deleteObject.execute(getString(R.string.deleteCateg), method, idObject);
    }

    // L'objet a été correctement ajouté
    public void okUpdateObject() {
        EtablissementManagerInfos.getInstance().recreate();
        Toast.makeText(context, getString(R.string.update), Toast.LENGTH_SHORT).show();
        finish();
    }

    // Retour de la recherche des catégories pour les consommables
    public void setComposConso(String[][] categories) {
        if (categories.length == 0) {
            Toast.makeText(context, getString(R.string.errorNoCateg), Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            int position = 0;
            categs = categories;
            edtDesc.setText(extras.getString(getString(R.string.prefDescriptionConso), ""));
            edtPrix.setText(extras.getString(getString(R.string.prefPrixConso), ""));

            ArrayList listInfos = new ArrayList();

            // Change l'index du spinner par rapport à la catégorie passé en paramètre
            for (int i = 0; i < categories.length; i++) {
                listInfos.add(categories[i][1]);
                if (categories[i][0].equals(extras.getString(getString(R.string.prefIdCa), "")))
                    position = i;
            }

            ArrayAdapter arrayInfos = new ArrayAdapter<>(
                    context,
                    R.layout.listitem,
                    listInfos);

            spinCateg.setAdapter(arrayInfos);
            spinCateg.setSelection(position);
            layoutCo.setVisibility(View.VISIBLE);
        }
    }

    public static EtablissementManagerInfosDetails getInstance() {
        return instEtabManInfDet;
    }
}
