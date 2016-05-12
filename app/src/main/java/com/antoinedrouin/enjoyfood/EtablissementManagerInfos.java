package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EtablissementManagerInfos extends AppCompatActivity {

    SharedPreferences pref;
    Context context;
    static EtablissementManagerInfos instEtabManInf;

    String idEt;

    ListView lvInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissement_manager_infos);

        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        instEtabManInf = this;

        idEt = pref.getString(getString(R.string.prefIdEt), "");

        Bundle extras = getIntent().getExtras();
        String typeInfos = extras.getString(getString(R.string.typeInfos));

        ((TextView) findViewById(R.id.txtTypeInfos)).setText(typeInfos);
        lvInfos = (ListView) findViewById(R.id.lvInfos);

        // En fonction du bouton cliqué on charge différentes données
        if (typeInfos.equals(getString(R.string.txtCateg))) {

        }
        else if (typeInfos.equals(getString(R.string.txtConso)))  {

        }
        else if (typeInfos.equals(getString(R.string.txtHoraires)))  {

        }
        else if (typeInfos.equals(getString(R.string.txtPay)))  {
            ServerSide getPaiementsInfos = new ServerSide(context);
            getPaiementsInfos.execute(getString(R.string.getPaiementsInfos), getString(R.string.read), idEt);
        }

        lvInfos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvInfos.getItemAtPosition(position);
                Toast.makeText(context, o.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fillLvInfos(String[][] infos)  {
        ArrayList listInfos = new ArrayList();

        for (int i = 0; i < infos.length ; i++) {
            listInfos.add(infos[i][1]);
        }

        ArrayAdapter<String> arrayInfos = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                listInfos);

        lvInfos.setAdapter(arrayInfos);
    }

    public static EtablissementManagerInfos getInstance() {
        return instEtabManInf;
    }
}
