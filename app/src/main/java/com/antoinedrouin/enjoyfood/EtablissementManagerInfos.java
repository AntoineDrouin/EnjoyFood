package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EtablissementManagerInfos extends AppCompatActivity {

    Context context;
    SharedPreferences pref;
    static EtablissementManagerInfos instEtabManInf;

    String idEt, typeInfo;
    String[][] info;
    ArrayList listInfos;
    ArrayAdapter<String> arrayInfos;

    ListView lvInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissement_manager_infos);

        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        instEtabManInf = this;

        idEt = pref.getString(getString(R.string.prefIdEt), "");

        String script, method;
        method = getString(R.string.read);
        script = "";

        Bundle extras = getIntent().getExtras();
        typeInfo = extras.getString(getString(R.string.typeInfos));

        ((TextView) findViewById(R.id.txtTypeInfos)).setText(typeInfo);
        lvInfos = (ListView) findViewById(R.id.lvInfos);

        ServerSide getObjectsInfo = new ServerSide(context);

        // En fonction du bouton cliqué on charge différentes données
         if (typeInfo.equals(getString(R.string.txtConso)))  {
            script = "";
        }
        else if (typeInfo.equals(getString(R.string.txtCateg))) {
            script = getString(R.string.getCategInfos);
        }
        else if (typeInfo.equals(getString(R.string.txtHoraires))) {
            script = getString(R.string.getHorairesInfos);
        }
        else if (typeInfo.equals(getString(R.string.txtPay))) {
            script = getString(R.string.getPaiementsInfos);
        }

        // Cherche les données de l'établissement
        getObjectsInfo.execute(script, method, idEt);

        lvInfos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvInfos.getItemAtPosition(position);
                Intent intent = new Intent(context, EtablissementManagerInfosDetails.class);

                intent.putExtra(getString(R.string.typeInfos), typeInfo);

                if (o.toString().equals("")){
                    intent.putExtra(getString(R.string.idObject), "");
                    intent.putExtra(getString(R.string.nameObject), "");
                }
                else {
                    intent.putExtra(getString(R.string.idObject), info[position][0]);
                    intent.putExtra(getString(R.string.nameObject), info[position][1]);

                    if (typeInfo.equals(getString(R.string.txtHoraires))) {
                        intent.putExtra(getString(R.string.prefHeureDebut1), info[position][2]);
                        intent.putExtra(getString(R.string.prefHeureFin1), info[position][3]);
                        intent.putExtra(getString(R.string.prefHeureDebut2), info[position][4]);
                        intent.putExtra(getString(R.string.prefHeureFin2), info[position][5]);
                    }
                    else if (typeInfo.equals(getString(R.string.txtConso))) {

                    }
                }

                startActivity(intent);
            }
        });
    }

    public void fillLvInfos(String[][] infos)  {
        listInfos = new ArrayList();
        info = infos;

        for (int i = 0; i < info.length ; i++) {
            listInfos.add(info[i][1]);
        }

        arrayInfos = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                listInfos);

        lvInfos.setAdapter(arrayInfos);
    }

    public void onClickAdd(View v) {
        listInfos.add("");
        arrayInfos.notifyDataSetChanged();
    }

    public static EtablissementManagerInfos getInstance() {
        return instEtabManInf;
    }
}
