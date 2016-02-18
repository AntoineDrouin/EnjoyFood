package com.antoinedrouin.enjoyfood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Etablissements extends Activity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissements);

        context = getApplicationContext();

        final ListView lvEtab = (ListView) findViewById(R.id.lvEtab);

        List<String> listEtab = new ArrayList<>();
        listEtab.add("aaah");
        listEtab.add("crap");
        listEtab.add(getString(R.string.lvIndicSearch));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listEtab);

        lvEtab.setAdapter(arrayAdapter);

        lvEtab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lvEtab.getItemAtPosition(position);

                if (o.toString().equals(getString(R.string.lvIndicSearch)))
                    openPlacePicker();
                else
                    openEtab(o.toString());
            }
        });
    }

    private void openPlacePicker() {
        startActivity(new Intent(this, MapPlacePicker.class));
    }

    private void openEtab(String nomEtab) {
        Intent intentEtab = new Intent(this, Etablissement.class);
        intentEtab.putExtra(getString(R.string.extraEtabName), nomEtab);
        startActivity(intentEtab);
    }

}
