package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;

public class Informations extends AppCompatActivity {

    Context context;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        /** CODE DE DEBUG **/

        ListView lvValues = (ListView) findViewById(R.id.lvValues);

        ArrayList listInfos = new ArrayList();
        listInfos.add("idUt : " + pref.getString(getString(R.string.prefId), ""));
        listInfos.add("pseudo : " + pref.getString(getString(R.string.prefPseudo), ""));
        listInfos.add("mdp : " + pref.getString(getString(R.string.prefMdp), ""));
        listInfos.add("nom : " + pref.getString(getString(R.string.prefNom), ""));
        listInfos.add("prenom : " + pref.getString(getString(R.string.prefPrenom), ""));
        listInfos.add("compte : " + pref.getString(getString(R.string.prefCompte), ""));
        listInfos.add("ville : " + pref.getString(getString(R.string.prefVille), ""));
        listInfos.add("cp : " + pref.getString(getString(R.string.prefCp), ""));
        listInfos.add("tel : " + pref.getString(getString(R.string.prefTel), ""));
        listInfos.add("adresse : " + pref.getString(getString(R.string.prefAdresse), ""));
        listInfos.add("idEt : " + pref.getString(getString(R.string.prefIdEt), ""));
        listInfos.add("nomEt : " + pref.getString(getString(R.string.prefNomEt), ""));

        ArrayAdapter<String> arrayInfos = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                listInfos);

        lvValues.setAdapter(arrayInfos);
    }
}

